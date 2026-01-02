# SecureFile Application

A desktop application built with JavaFX for secure file encryption and decryption using AES encryption.

## Features

- **File Encryption/Decryption**: Secure your files using robust AES encryption.
- **Smart UI Management**: The interface dynamically enables/disables actions based on the selected file type (e.g., encryption for regular files, decryption for `.enc` files).
- **File Metadata**: View file size and extension details before processing.
- **Asynchronous Processing**: Files are processed in the background to keep the UI responsive.

## Tech Stack

- **Java**: Version 25
- **Framework**: JavaFX 21
- **Build Tool**: Maven
- **Security**: Bouncy Castle (bcprov-jdk18on)
- **Testing**: JUnit 5

## Project Structure

- `com.securefile.logic`: Core business logic, file utilities, and process management.
- `com.securefile.service`: Encryption services and cryptographic key factories.
- `com.securefile.main`: JavaFX UI controllers, application entry point, and UI constants.

## Getting Started

### Prerequisites

- JDK 25
- Maven 3.x

### Running the Application

You can run the application using the Maven JavaFX plugin:

```bash
mvn clean javafx:run
```
