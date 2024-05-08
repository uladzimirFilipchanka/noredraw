# Define the default target to run the Gradle build and then the Docker build
.PHONY: all
build: gradle_build docker_build

# Rule to run the Gradle clean and build process
.PHONY: clean_build
gradle_build:
	./gradlew clean build

# Rule to build a Docker image with a specific tag
.PHONY: docker_build
docker_build:
	docker build app --tag noredraw:latest

# Rule to remove the Docker image
.PHONY: docker_clean
docker_clean:
	docker rmi noredraw:latest