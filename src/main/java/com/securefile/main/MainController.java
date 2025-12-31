package com.securefile.main;

import com.securefile.logic.ProcessManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {
    private final ProcessManager processManager = new ProcessManager();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML private Label sourcePathLabel;
    @FXML private Label fileSizeLabel; // New
    @FXML private Label fileExtLabel;  // New
    @FXML private Label destPathLabel;
    @FXML private TextField fileNameField;
    @FXML private Button encryptBtn;
    @FXML private Button decryptBtn;
    @FXML private Button saveBtn;


    @FXML
    public void initialize() {

        disableEncryptionAndDecryption();
        saveBtn.setDisable(true);
    }

    @FXML
    private void browseSourceFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File");
        File file = chooser.showOpenDialog(null);

        // enforce user to use save button
        disableEncryptionAndDecryption();

        if (file != null) {
            sourcePathLabel.setText(file.getAbsolutePath());
            // Auto-suggest file name
            String name = file.getName();
            int dotIndex = name.indexOf('.');
            if (dotIndex > 0) {
                fileNameField.setText(name.substring(0, dotIndex));
            } else {
                fileNameField.setText(name);
            }

            // Calculate and show details
            String sizeInKB = processManager.getFileSize(file)   ;
            String extension = processManager.getFileExtension(file);

            // Update the new UI labels
            fileSizeLabel.setText("File Size: " + sizeInKB );
            fileExtLabel.setText("Extension: " + extension.toUpperCase());

            processManager.setSrcFile(file);
        }

    }

    @FXML
    private void browseDestinationFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Destination Folder");
        File folder = chooser.showDialog(null);

        saveBtn.setDisable(false);
        // enforce user to use save button
        disableEncryptionAndDecryption();

        if (folder != null) {
            destPathLabel.setText(folder.getAbsolutePath());
        }
    }

    @FXML
    private void savePaths() {
        String dest = destPathLabel.getText();
        String FileName = fileNameField.getText();

        try {
            processManager.setDestFolder(dest, FileName);

            encryptBtn.setDisable(false);
            decryptBtn.setDisable(false);

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Issue", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void encryptFile() {
        runTask(processManager::executeEncryption, "Encryption");
    }

    @FXML
    private void decryptFile() {
        runTask(processManager::executeDecryption, "Decryption");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Keeps the dialog simple
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void runTask(Runnable logic, String actionName) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.run();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            clearInputs();
            saveBtn.setDisable(false);
            showAlert(Alert.AlertType.INFORMATION, "Process Complete", actionName + " successful!");
        });

        task.setOnFailed(e -> {
            clearInputs();
            saveBtn.setDisable(false);
            Throwable ex = task.getException();
            showAlert(Alert.AlertType.ERROR, actionName + " Error", "An error occurred: " + ex.getMessage());
        });

        saveBtn.setDisable(true);
        encryptBtn.setDisable(true);
        decryptBtn.setDisable(true);

        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void clearInputs() {
        sourcePathLabel.setText("");
        destPathLabel.setText("");
        fileNameField.setText("");
        fileSizeLabel.setText("File Size: ");
        fileExtLabel.setText("Extension: ");
    }

    private void disableEncryptionAndDecryption() {
        encryptBtn.setDisable(true);
        decryptBtn.setDisable(true);
    }

}
