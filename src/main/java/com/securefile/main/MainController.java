package com.securefile.main;

import com.securefile.logic.ProcessManager;
import com.securefile.service.AESEncryptionService;
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
    private final ProcessManager processManager = new ProcessManager(new AESEncryptionService());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML private Label sourcePathLabel;
    @FXML private Label fileSizeLabel;
    @FXML private Label fileExtLabel;
    @FXML private Label destPathLabel;
    @FXML private TextField fileNameField;
    @FXML private Button encryptBtn;
    @FXML private Button decryptBtn;
    @FXML private Button saveBtn;


    @FXML
    public void initialize() {
        updateActionButtons(false, false, false);
    }

    @FXML
    private void browseSourceFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File");
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            sourcePathLabel.setText(file.getAbsolutePath());
            processManager.setSrcFile(file);
            // Auto-suggest file name
            fileNameField.setText(processManager.suggestFileName());
            // Calculate and show details
            fileExtLabel.setText(UIConstants.DEFAULT_TEXT_LABEL + processManager.getFileExtension());
            fileSizeLabel.setText(UIConstants.DEFAULT_SIZE_LABEL + processManager.getFileSize());
            // enforce user to use the save button
            updateActionButtons(false, false, true);
        }
    }

    @FXML
    private void browseDestinationFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Destination Folder");
        File folder = chooser.showDialog(null);
        if (folder != null){
            destPathLabel.setText(folder.getAbsolutePath());
            processManager.setDestFolder(folder.getAbsolutePath());
            updateActionButtons(false, false, true);
        }
    }

    @FXML
    private void savePaths() {
        String FileName = fileNameField.getText();
        try {
            processManager.setFileName(FileName);
            processManager.validateAllDataExist();
            if (processManager.getFileExtension().equals(UIConstants.EncruptedFileExt))
                updateActionButtons(true, false, false);
            else
                updateActionButtons(false, true, false);
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
            showAlert(Alert.AlertType.INFORMATION, "Process Complete", actionName + " successful!");
        });

        task.setOnFailed(e -> {
            clearInputs();
            showAlert(Alert.AlertType.ERROR, actionName + " Error", "An error occurred: " + task.getException().getMessage());
        });

        updateActionButtons(false, false, false);
        executor.submit(task);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Keeps the dialog simple
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearInputs() {
        sourcePathLabel.setText("");
        destPathLabel.setText("");
        fileNameField.setText("");
        fileSizeLabel.setText(UIConstants.DEFAULT_SIZE_LABEL);
        fileExtLabel.setText(UIConstants.DEFAULT_TEXT_LABEL);
    }

    private void updateActionButtons(boolean decrypt, boolean encrypt, boolean save) {
        decryptBtn.setDisable(!decrypt); // using ! to make true open the button
        encryptBtn.setDisable(!encrypt);
        saveBtn.setDisable(!save);
    }


    public void shutdownExecutor() {
        System.out.println("Shutting down ExecutorService...");
        executor.shutdownNow();
        System.out.println("ExecutorService terminated: " + executor.isShutdown());
    }
}
