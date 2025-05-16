#!/bin/bash

cd "$(dirname "$0")"

# Make sure we have the project compiled
mvn clean compile

# Find JavaFX jars in the Maven repository
MAVEN_REPO=$HOME/.m2/repository
JAVAFX_VERSION="17.0.2"

# Create module path with explicit JavaFX jar files
MODULE_PATH=""
for module in javafx-base javafx-controls javafx-fxml javafx-graphics; do
  JAR_PATH="$MAVEN_REPO/org/openjfx/$module/$JAVAFX_VERSION/$module-$JAVAFX_VERSION.jar"
  if [ -f "$JAR_PATH" ]; then
    if [ -z "$MODULE_PATH" ]; then
      MODULE_PATH="$JAR_PATH"
    else
      MODULE_PATH="$MODULE_PATH:$JAR_PATH"
    fi
  else
    echo "Error: JavaFX module $module not found at: $JAR_PATH"
    echo "Running maven command to download dependencies..."
    mvn dependency:resolve
    echo "Please run this script again after dependencies are downloaded."
    exit 1
  fi
done

# Get platform-specific JavaFX modules
OS_NAME=$(uname -s)
if [ "$OS_NAME" = "Darwin" ]; then
  PLATFORM="mac"
  ARCH=$(uname -m)
  if [ "$ARCH" = "arm64" ]; then
    PLATFORM="$PLATFORM-aarch64"
  else
    PLATFORM="$PLATFORM"
  fi
elif [ "$OS_NAME" = "Linux" ]; then
  PLATFORM="linux"
else
  PLATFORM="win"
fi

for module in javafx-base javafx-controls javafx-fxml javafx-graphics; do
  JAR_PATH="$MAVEN_REPO/org/openjfx/$module/$JAVAFX_VERSION/$module-$JAVAFX_VERSION-$PLATFORM.jar"
  if [ -f "$JAR_PATH" ]; then
    MODULE_PATH="$MODULE_PATH:$JAR_PATH"
  fi
done

# Get the classpath including project classes and dependencies
PROJECT_CLASSES="target/classes"
DEPS_CLASSPATH=$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)
CLASSPATH="$PROJECT_CLASSES:$DEPS_CLASSPATH"

# Define the JavaFX modules
JAVAFX_MODULES="javafx.controls,javafx.fxml,javafx.graphics,javafx.base"

echo "Starting JavaFX application..."
java --module-path "$MODULE_PATH" \
     --add-modules "$JAVAFX_MODULES" \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED \
     -cp "$CLASSPATH" \
     org.example.client.SimpleFxmlTest

# Save exit code
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
  echo "Application exited with error code: $EXIT_CODE"
else
  echo "Application closed successfully."
fi

exit $EXIT_CODE
