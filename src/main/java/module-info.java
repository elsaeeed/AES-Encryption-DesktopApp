module com.securefile.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.bouncycastle.provider;

    opens com.securefile.main to javafx.fxml;
    exports com.securefile.main;
}