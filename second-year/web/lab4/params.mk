JAVAC := javac
JAVA := java
JAR := jar

JAVACFLAGS := -encoding UTF-8 -source 17 -target 17

SRC_DIR := src/main/java
RESOURCES_DIR := src/main/resources
BUILD_DIR := build
CLASS_FILES_DIR := build/classes
ARTIFACTS_DIR := build/artifacts
LIB_DIR := lib

GROUP := ru.mip3x
VERSION := 1.0.0

MAIN_CLASS := ru.mip3x.lab4.RestApplication

WAR_NAME := lab4-$(VERSION).war

MUSIC_PLAYER ?= ffplay
MUSIC_FILE ?= music/build_completed.mp3

JAVADOC_DIR := $(BUILD_DIR)/docs/javadoc

SERVER_NAME := helios
SCP_PATH := ~/artifacts/

LOCALE_SRC_DIR := src/main/resources/i18n
LOCALE_OUT_DIR := $(BUILD_DIR)/i18n

NATIVE2ASCII := /usr/lib/jvm/java-8-openjdk/bin/native2ascii

ALT_SRC_DIR := build/alt_src
ALT_ARTIFACTS_DIR := build/alt_artifacts
ALT_WAR_NAME := alt-$(WAR_NAME)

REPLACE_FILE := replace.properties

POSTGRESQL_DOWNLOAD_PATH := https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
HIBERNATE_DOWNLOAD_PATH := https://repo1.maven.org/maven2/org/hibernate/orm/hibernate-core/6.6.1.Final/hibernate-core-6.6.1.Final.jar
JAKARTA_WEB_API_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/platform/jakarta.jakartaee-web-api/11.0.0-M3/jakarta.jakartaee-web-api-11.0.0-M3.jar
LOMBOK_DOWNLOAD_PATH := https://repo1.maven.org/maven2/org/projectlombok/lombok/1.18.30/lombok-1.18.30.jar
JSONB_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/json/bind/jakarta.json.bind-api/2.0.0/jakarta.json.bind-api-2.0.0.jar
JBCRYPT_DOWNLOAD_PATH := https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
VALIDATION_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/validation/jakarta.validation-api/3.0.2/jakarta.validation-api-3.0.2.jar
JAXRS_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/ws/rs/jakarta.ws.rs-api/3.1.0/jakarta.ws.rs-api-3.1.0.jar
PERSISTENCE_API_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar
EJBI_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/enterprise/jakarta.enterprise.cdi-api/4.0.1/jakarta.enterprise.cdi-api-4.0.1.jar
EJB_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/ejb/jakarta.ejb-api/4.0.1/jakarta.ejb-api-4.0.1.jar
INJECT_DOWNLOAD_PATH := https://repo1.maven.org/maven2/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar

CLASSPATH := $(LIB_DIR)/*

