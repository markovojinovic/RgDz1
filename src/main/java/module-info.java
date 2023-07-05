module com.example.rgdz1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.rgdz1 to javafx.fxml;
    exports com.example.rgdz1;
}