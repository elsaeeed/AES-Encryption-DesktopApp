# SecureFile - AES-256 Encryption Utility

SecureFile is a desktop application built with **JavaFX** that provides a robust interface for encrypting and decrypting files using industry-standard AES-256 bit encryption.

This project was developed as a technical interview task for **Softlock**.

## 🚀 Features
- **High Security:** Implements AES encryption in CBC mode with PKCS5 padding.
- **Background Processing:** Utilizes `javafx.concurrent.Task` and `ExecutorService` to ensure the UI remains responsive during large file operations.
- **Intelligent UX:**
    - Automatically suggests output filenames.
    - Automatically restores original extensions during decryption (stripping `.enc`).
    - Displays file metadata (Size, Extension) upon selection.
- **Robust Validation:** Prevents accidental file overwriting and handles invalid OS characters in filenames.

## 🔐 Security Specifications
- **Algorithm:** AES (Advanced Encryption Standard).
- **Key Size:** 256-bit.
- **Mode:** CBC (Cipher Block Chaining) with a unique, randomly generated 16-byte IV (Initialization Vector) prepended to every encrypted file.
- **Provider:** Bouncy Castle (BC) Security Provider.

## ⚠️ Key Management (Important Note)
To ensure the application is functional and "plug-and-play" for evaluation:
- The app generates a 256-bit key stored in `app.key` in the root directory.
- **Production Roadmap:** For a production-grade deployment, this local file storage would be replaced with a **Java KeyStore (JKS)**, an OS-level Keychain, or **Password-Based Encryption (PBE)** using PBKDF2 to ensure the master key is never stored in plain text.

## 🛠 Tech Stack
- **JDK 25**
- **JavaFX 25** (UI Layer)
- **Bouncy Castle** (Cryptographic Provider)
- **JUnit 5** (Unit Testing)
- **Maven** (Dependency & Build Management)

## 📂 Project Structure
- `com.securefile.service`: Core encryption services and Key management logic.
- `com.securefile.logic`: The `ProcessManager` which handles file path orchestration and validation.
- `com.securefile.main`: JavaFX Controller and Application entry points.
- `src/test`: Comprehensive unit tests for services and business logic.

## ⚙️ How to Run
1. **Clone the repository.**
2. **Build with Maven:**
   ```bash
   mvn clean install
   ```
3. **Run the app:**
   ```bash
   mvn javafx:run
   ```
4. **Run Tests:**
   ```bash
   mvn test
   ```

## 📝 Usage Instructions
1. **Source File:** Click "Browse File" to select the file to be processed.
2. **Destination:** Select the folder where the output should be saved.
3. **Save Paths:** Click "Save Paths" to lock in the configuration and enable the action buttons.
4. **Action:** Click **Encrypt** (adds `.enc` extension) or **Decrypt** (removes `.enc` and restores original extension).

---
