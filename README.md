# AWS Movie Theatre

A Java web application showcasing AI-generated movie posters created with Amazon Bedrock. Built for AWS Outposts workshop demonstrating Tomcat 9 deployment.

## Features

- 6 unique movie posters generated with Amazon Bedrock (Stable Diffusion 3.5)
- Responsive movie theatre themed UI
- Java Servlet-based web application
- Tomcat 9 compatible WAR deployment

## Quick Start

### 1. Generate Movie Posters (Optional)

If you want to regenerate the images:

```bash
chmod +x generate-images.sh
./generate-images.sh
```

**Requirements:** AWS CLI configured with Bedrock access in us-west-2

### 2. Build the Application

```bash
chmod +x build.sh
./build.sh
```

This creates `target/ROOT.war` ready for deployment.

### 3. Test Locally

```bash
mvn tomcat7:run
```

Then visit: http://localhost:8080/

### 4. Deploy to Tomcat 9

```bash
sudo cp target/ROOT.war /opt/tomcat/latest/webapps/ROOT.war
sudo systemctl restart tomcat
```

Access at: http://your-server-ip:8080/

## Project Structure

```
aws-movie-theatre/
├── src/
│   └── main/
│       ├── java/com/aws/outposts/
│       │   └── AwsMovieTheatreServlet.java
│       └── webapp/
│           ├── images/          # Movie poster images
│           └── WEB-INF/
│               └── web.xml
├── images/                      # Source images (copied to webapp during build)
├── build.sh                     # Build script
├── generate-images.sh           # Bedrock image generation script
├── pom.xml                      # Maven configuration
└── README.md
```

## Requirements

- Java 25 (OpenJDK)
- Maven 3.x
- Tomcat 9
- AWS CLI (for image generation only)

## Movies Featured

1. **The Brave Little Toaster Returns** - A heartwarming kitchen adventure
2. **The Great Potato Heist** - Underground vegetable caper
3. **Calculator Warriors** - Mathematical office supply adventure
4. **The Rubber Duck Chronicles** - Magical bath time discovery
5. **Space Llamas from Mars** - Intergalactic camelid journey
6. **The Mysterious Stapler** - Enchanted office mystery

## Disclaimer

Movie posters generated with Amazon Bedrock. AWS Movie Theatre is not a real service.
