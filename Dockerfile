# Use an official OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy only the source directory into the container
COPY src/ ./src/

# Compile the Java source files into the 'out' directory
RUN javac -d out src/*.java

# Expose port 6969 since our built-in HttpServer uses it
EXPOSE 6969

# Command to run the application
CMD ["java", "-cp", "out", "Main"]
