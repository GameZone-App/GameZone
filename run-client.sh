#!/bin/bash

# Compile the project first
mvn compile

# Get JavaFX module path (adjust this path based on your local environment)
JAVAFX_PATH=$(mvn dependency:build-classpath -Dmdep.includeTypes=jar -Dmdep.outputFile=/dev/stdout | grep -o '[^:]*javafx[^:]*')

# Build the classpath
CLASSPATH="target/classes:$(mvn dependency:build-classpath -Dmdep.includeTypes=jar -Dmdep.outputFile=/dev/stdout)"

# Set JavaFX module path
JAVAFX_MODULES="javafx.controls,javafx.fxml,javafx.graphics,javafx.base"

# Run the application
java --module-path "$JAVAFX_PATH" \
     --add-modules $JAVAFX_MODULES \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED \
     -cp "$CLASSPATH" \
     org.example.client.SimpleFxmlTest

# Exit with the application's exit code
exit $?
