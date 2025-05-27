include params.mk

JAVA_SRCS := $(shell find $(SRC_DIR) -type f -name '*.java')
CLASS_FILES := $(patsubst $(SRC_DIR)/%.java,$(CLASS_FILES_DIR)/%.class,$(JAVA_SRCS))
STAGING := $(BUILD_DIR)/_staging

all: build

build: xml compile prepare_war package_war
	@echo "[O3JMB] Successful build..."
	@$(MAKE) music

compile: dependencies
	@echo "[O3JMB] Getting dependencies..."
	@mkdir -p $(CLASS_FILES_DIR)
	$(JAVAC) $(JAVACFLAGS) -cp "$(CLASSPATH)" -d $(CLASS_FILES_DIR) $(JAVA_SRCS)

prepare_war:
	@rm -rf $(STAGING)
	@mkdir -p $(STAGING)/WEB-INF/classes $(STAGING)/WEB-INF/lib

	@cp -r $(CLASS_FILES_DIR)/* $(STAGING)/WEB-INF/classes/
	@cp -r src/main/resources/* $(STAGING)/WEB-INF/classes/

	@cp $(LIB_DIR)/*.jar $(STAGING)/WEB-INF/lib/

package_war:
	@mkdir -p $(ARTIFACTS_DIR)
	jar cvf $(ARTIFACTS_DIR)/$(WAR_NAME) -C $(STAGING) .

dependencies:
	@mkdir -p $(LIB_DIR)
	@echo "[O3JMB] Downloading dependencies into $(LIB_DIR)..."
	wget -nc -P $(LIB_DIR) $(POSTGRESQL_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(HIBERNATE_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(JAKARTA_WEB_API_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(LOMBOK_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(JSONB_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(JBCRYPT_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(VALIDATION_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(JAXRS_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(PERSISTENCE_API_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(EJBI_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(EJB_DOWNLOAD_PATH)
	wget -nc -P $(LIB_DIR) $(INJECT_DOWNLOAD_PATH)
	@echo "[O3JMB] All dependencies are downloaded"

music:
	@echo "[O3JMB] Playing $(MUSIC_FILE)..."
	$(MUSIC_PLAYER) -nodisp -autoexit -loglevel quiet $(MUSIC_FILE)

doc: build javadoc
	@echo "[O3JMB] Calculating checksums..."
	@find $(SRC_DIR) -type f -name '*.java' -exec md5sum {} \; > $(BUILD_DIR)/MD5SUMS.txt
	@find $(CLASS_FILES_DIR) -type f -name '*.class' -exec sha1sum {} \; > $(BUILD_DIR)/SHA1SUMS.txt

	@echo "Manifest-Version: 1.0" > $(BUILD_DIR)/MANIFEST.MF
	@echo "Created-By: $(USER)" >> $(BUILD_DIR)/MANIFEST.MF
	@cat $(BUILD_DIR)/MD5SUMS.txt >> $(BUILD_DIR)/MANIFEST.MF
	@cat $(BUILD_DIR)/SHA1SUMS.txt >> $(BUILD_DIR)/MANIFEST.MF

	@echo "[O3JMB] Copying Javadoc into WAR staging..."
	@mkdir -p $(STAGING)/docs
	@cp -r $(JAVADOC_DIR)/* $(STAGING)/docs/

javadoc:
	@echo "[O3JMB] Generating Javadoc..."
	@mkdir -p $(JAVADOC_DIR)
	javadoc -d $(JAVADOC_DIR) -cp "$(CLASSPATH)" $(JAVA_SRCS)

xml:
	@echo "[O3JMB] Validating XML files..."
	@find . -type f -name '*.xml' | while read file; do \
		echo "  -> $$file"; \
		xmllint --noout "$$file" || exit 1; \
	done
	@echo "[O3JMB] All XML files are valid."

scp: build
	@echo "[O3JMB] Copying WAR to remote server via SCP config [$(SERVER_NAME)]..."
	scp $(ARTIFACTS_DIR)/$(WAR_NAME) $(SERVER_NAME):$(SCP_PATH)

native2ascii:
	@echo "[O3JMB] native2ascii…"
	@mkdir -p $(LOCALE_OUT_DIR)
	@for f in $(LOCALE_SRC_DIR)/*.properties; do \
	  [ -f "$$f" ] || continue; \
	  echo "  -> $$f"; \
	  $(NATIVE2ASCII) -encoding UTF-8 "$$f" "$(LOCALE_OUT_DIR)/$$(basename $$f)"; \
	done

alt:
	@echo "[O3JMB] Creating alternative version..."

	@rm -rf $(ALT_SRC_DIR)
	@mkdir -p $(ALT_SRC_DIR)
	@cp -r $(SRC_DIR)/* $(ALT_SRC_DIR)/

	@if [ -f $(REPLACE_FILE) ]; then \
	  echo "[O3JMB] Applying replacements from $(REPLACE_FILE)…"; \
	  while IFS='=' read -r pat rep; do \
	    find $(ALT_SRC_DIR) -type f -name '*.java' -exec sed -i "s/$$pat/$$rep/g" {} +; \
	  done < $(REPLACE_FILE); \
	else \
	  echo "[O3JMB] No $(REPLACE_FILE) — skipping"; \
	fi

	@echo "[O3JMB] Renaming .java files if class names changed..."
	@find $(ALT_SRC_DIR) -type f -name '*.java' | while read file; do \
	  new_name=$$(grep -E 'public (class|interface|enum) ' "$$file" | sed -E 's/.* (class|interface|enum) ([A-Za-z0-9_]+).*/\2.java/'); \
	  dir=$$(dirname "$$file"); \
	  old_name=$$(basename "$$file"); \
	  [ "$$new_name" != "$$old_name" ] && mv "$$file" "$$dir/$$new_name"; \
	done

	@echo "[O3JMB] Building alternative WAR…"
	$(MAKE) build \
	  SRC_DIR=$(ALT_SRC_DIR) \
	  ARTIFACTS_DIR=$(ALT_ARTIFACTS_DIR) \
	  WAR_NAME=$(ALT_WAR_NAME)
	@echo "[O3JMB] Alternative WAR is at $(ALT_ARTIFACTS_DIR)/$(ALT_WAR_NAME)"

env: build
	@echo "[O3JMB] Running in custom environment from env.properties..."
	@if [ -f env.properties ]; then \
	  . ./env.properties; \
	  echo "[O3JMB] Using JAVA_HOME=$$JAVA_HOME"; \
	  echo "[O3JMB] Using JAVA_ARGS=$$JAVA_ARGS"; \
	  "$$JAVA_HOME/bin/java" $$JAVA_ARGS -cp "$(CLASS_FILES_DIR):$(LIB_DIR)/*" $$MAIN_CLASS; \
	else \
	  echo "[O3JMB] env.properties not found"; \
	  exit 1; \
	fi

history:
	@echo "[O3JMB] Searching for last successful compilation..."
	@branch=$$(git symbolic-ref --short -q HEAD || git rev-parse --short HEAD); \
	last_success=""; \
	for rev in $$(git rev-list --first-parent $$branch); do \
	  echo "-> Testing $$rev…"; \
	  git checkout --quiet $$rev; \
	  if make compile >/dev/null 2>&1; then \
	    last_success=$$rev; \
	    echo "[O3JMB] Success at $$last_success"; \
	    break; \
	  fi; \
	done; \
	if [ -z "$$last_success" ]; then \
	  echo "[O3JMB] No revision compiled successfully"; \
	  git checkout --quiet $$branch; \
	  exit 1; \
	fi; \
	\
	git checkout --quiet $$branch; \
	candidates=$$(git rev-list --first-parent --reverse $$last_success..$$branch); \
	echo "[O3JMB] Candidate commits after $$last_success:"; \
	echo "$$candidates"; \
	next_rev=$$(echo "$$candidates" | head -n1); \
	if [ -z "$$next_rev" ]; then \
	  echo "[O3JMB] Last success is the very first commit — nothing to diff"; \
	else \
	  echo "[O3JMB] Generating diff between $$last_success and $$next_rev..."; \
	  git diff $$last_success $$next_rev > $(DIFF_FILE); \
	  echo "[O3JMB] Wrote $(DIFF_FILE)"; \
	fi

clean:
	rm -rf $(BUILD_DIR) $(LIB_DIR)

.PHONY: all compile prepare_war package_war build dependencies clean music doc javadoc xml native2ascii alt env history

