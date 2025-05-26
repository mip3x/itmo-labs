include params.mk

JAVA_SRCS := $(shell find $(SRC_DIR) -type f -name '*.java')
CLASS_FILES := $(patsubst $(SRC_DIR)/%.java,$(CLASS_FILES_DIR)/%.class,$(JAVA_SRCS))
STAGING := $(BUILD_DIR)/_staging

all: build

build: compile doc prepare_war package_war
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

doc: javadoc

javadoc:
	@echo "[O3JMB] Generating Javadoc..."
	@mkdir -p $(JAVADOC_DIR)
	javadoc -d $(JAVADOC_DIR) -cp "$(CLASSPATH)" $(JAVA_SRCS)

clean:
	rm -rf $(BUILD_DIR) $(LIB_DIR)

.PHONY: all compile prepare_war package_war build dependencies clean music javadoc
