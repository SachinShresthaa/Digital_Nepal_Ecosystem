#!/usr/bin/env bash
set -euo pipefail
# Official-style Maven wrapper script.
# Behavior:
# - If 'mvn' exists on PATH, delegate to it.
# - Else if .mvn/wrapper/maven-wrapper.jar exists, run it with java -jar.
# - Else download the official maven-wrapper JAR and then run it.

WRAPPER_DIR=".mvn/wrapper"
WRAPPER_JAR="${WRAPPER_DIR}/maven-wrapper.jar"
WRAPPER_DOWNLOAD_URL="https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

exec_mvn() {
  if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
  fi

  if [ -f "${WRAPPER_JAR}" ]; then
    exec java -Djdk.tls.client.protocols=TLSv1.2 -Dhttps.protocols=TLSv1.2 -Dmaven.multiModuleProjectDirectory="$PWD" -classpath "${WRAPPER_JAR}" org.apache.maven.wrapper.MavenWrapperMain "$@"
  fi

  mkdir -p "${WRAPPER_DIR}"
  echo "maven-wrapper.jar not found — downloading from ${WRAPPER_DOWNLOAD_URL}..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "${WRAPPER_DOWNLOAD_URL}" -o "${WRAPPER_JAR}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "${WRAPPER_DOWNLOAD_URL}" -O "${WRAPPER_JAR}"
  else
    echo "Please install curl or wget to allow mvnw to download the maven wrapper jar." >&2
    exit 1
  fi

  exec java -Djdk.tls.client.protocols=TLSv1.2 -Dhttps.protocols=TLSv1.2 -Dmaven.multiModuleProjectDirectory="$PWD" -classpath "${WRAPPER_JAR}" org.apache.maven.wrapper.MavenWrapperMain "$@"
}

if [ "$#" -eq 0 ]; then
  set -- -v
fi

exec_mvn "$@"
