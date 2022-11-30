package com.example.tankbattle.controller;

import com.example.tankbattle.HelloApplication;
import com.example.tankbattle.model.Avatar;
import com.example.tankbattle.model.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    @FXML
    private TableColumn<Avatar, String> Nombre;

    @FXML
    private TableColumn<Avatar, Integer> Victorias;

    @FXML
    private Label name;

    @FXML
    private Button btnBack;

    @FXML
    private TableView<Avatar> table;

    private Clip clip;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String uri = HelloApplication.class.getResource("audio/menu.wav").getPath();
        System.out.println(uri);
        File musicPath = new File(uri);

        if(musicPath.exists()){

            try {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(3);

            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("No existe");
        }
        Singleton.getInstance();
        ObservableList<Avatar> winnersArr = Singleton.getPlayers();

        Nombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        Victorias.setCellValueFactory(new PropertyValueFactory<>("wins"));
        Victorias.setSortType(TableColumn.SortType.DESCENDING);


        table.setItems(winnersArr);
    }

    @FXML
    void btnBack(ActionEvent event) {
        clip.stop();
        HelloApplication.showWindow("menu.fxml");
        Stage currentStage = (Stage) btnBack.getScene().getWindow();
        currentStage.hide();

    }
}
