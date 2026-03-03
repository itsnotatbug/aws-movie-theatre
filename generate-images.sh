#!/bin/bash
REGION="us-west-2"

# Create images directory
mkdir -p images

echo "Generating movie poster images with Amazon Bedrock..."

# 1. The Brave Little Toaster Returns
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster, large bold text at top reads THE BRAVE LITTLE TOASTER RETURNS, shiny chrome toaster with glowing elements in a cozy kitchen, warm lighting, adventure movie style, title text clearly visible and not cut off, no people, no faces","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/toaster.json

# 2. The Great Potato Heist
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster, large bold text at top reads THE GREAT POTATO HEIST, cartoon potatoes with tiny masks and bags, heist movie style, fun and colorful, title text clearly visible and not cut off, no people, no faces","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/potato.json

# 3. Calculator Warriors
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster with large bold text at top that reads CALCULATOR WARRIORS spelled correctly, cartoon calculators with animated arms and legs in warrior poses, action comedy style, office background, absolutely no people, no humans, no faces, only animated calculator characters","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/calculator.json

# 4. The Rubber Duck Chronicles
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster, large bold text at top reads THE RUBBER DUCK CHRONICLES, cheerful yellow rubber ducks in a bubbly bathroom, comedy style, bright and playful lighting, title text clearly visible and not cut off, no people, no faces","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/ducks.json

# 5. Space Llamas from Mars
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster, large bold text at top reads SPACE LLAMAS FROM MARS, llamas in astronaut suits on red Mars landscape, sci-fi comedy style, retro 1950s aesthetic, title text clearly visible and not cut off, no people, no human faces","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/llamas.json

# 6. The Mysterious Stapler
aws bedrock-runtime invoke-model \
  --model-id stability.sd3-5-large-v1:0 \
  --region $REGION \
  --body '{"prompt":"Movie poster, large bold text at top reads THE MYSTERIOUS STAPLER, red stapler glowing with magical sparkles in office setting, mystery comedy style, whimsical lighting, title text clearly visible and not cut off, no people, no faces","mode":"text-to-image","aspect_ratio":"1:1","output_format":"png"}' \
  --cli-binary-format raw-in-base64-out \
  images/stapler.json

# Extract and save images
echo "Extracting images..."
for file in toaster potato calculator ducks llamas stapler; do
  cat images/${file}.json | jq -r '.images[0]' | base64 -d > images/${file}.png
  echo "Created images/${file}.png"
done

# Clean up JSON files
rm -f images/*.json

echo "All images generated in images/ folder!"
echo "Run ./build.sh to package the application."
