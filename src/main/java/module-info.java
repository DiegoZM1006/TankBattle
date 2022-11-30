module com.example.tankbattle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens com.example.tankbattle to javafx.fxml;
    exports com.example.tankbattle;
    exports com.example.tankbattle.controller;
    opens com.example.tankbattle.controller to javafx.fxml;
    opens com.example.tankbattle.model to javafx.base;
    exports com.example.tankbattle.model;


}