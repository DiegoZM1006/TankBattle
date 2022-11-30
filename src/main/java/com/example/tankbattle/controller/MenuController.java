package com.example.tankbattle.controller;

import com.example.tankbattle.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.*;

public class MenuController implements Initializable {

    @FXML
    private Button btnBoard;

    @FXML
    private Button btnStart;

    @FXML
    private AnchorPane panel;

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
    }

    @FXML
    void btnBoard(ActionEvent event) {
        clip.stop();
        HelloApplication.showWindow("board.fxml");
        Stage currentStage = (Stage) btnStart.getScene().getWindow();
        currentStage.hide();

    }

    @FXML
    void btnStart(ActionEvent event) {

        clip.stop();
        HelloApplication.showWindow("preGame.fxml");
        Stage currentStage = (Stage) btnStart.getScene().getWindow();
        currentStage.hide();
    }
}