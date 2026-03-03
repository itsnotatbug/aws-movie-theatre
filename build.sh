#!/bin/bash
set -e

echo "Building AWS Movie Theatre application..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Installing Maven..."
    sudo apt-get update
    sudo apt-get install -y maven
fi

# Copy images to webapp directory
echo "Copying images to webapp..."
mkdir -p src/main/webapp/images
if [ -d "images" ] && [ "$(ls -A images/*.png 2>/dev/null)" ]; then
    cp images/*.png src/main/webapp/images/
    echo "Images copied successfully"
else
    echo "Warning: No images found. Run ./generate-images.sh first to create movie posters."
fi

# Build the WAR file
mvn clean package

echo ""
echo "=========================================="
echo "Build complete!"
echo "=========================================="
echo "ROOT.war created in target/ directory"
echo ""
echo "To test locally: mvn tomcat7:run"
echo "Then visit: http://localhost:8080/"
echo ""
