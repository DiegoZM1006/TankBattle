package com.example.tankbattle.controller;

import com.example.tankbattle.HelloApplication;
import com.example.tankbattle.model.Avatar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.example.tankbattle.model.Singleton;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreGameController implements Initializable {

    @FXML
    private Button btnBack;
    private Clip clip;

    @FXML
    private Button btnPlay;

    @FXML
    private TextField txtCpu;

    @FXML
    private TextField txtPlayer1;

    @FXML
    private TextField txtPlayer2;

    @FXML
    void btnBack(ActionEvent event) {
        clip.stop();
        HelloApplication.showWindow("menu.fxml");
        Stage currentStage = (Stage) btnBack.getScene().getWindow();
        currentStage.hide();

    }

    @FXML
    void btnPlay(ActionEvent event) {

        String player1 = txtPlayer1.getText();
        String player2 = txtPlayer2.getText();
        String cputxt = txtCpu.getText();

        if(cputxt.equals("")) {
            cputxt = "CPU";
        }

        if(!player1.equals("") && !player2.equals("")) {

            Singleton.getInstance().setPlayer1(new Avatar(player1 , 0));
            Singleton.getInstance().setPlayer2(new Avatar(player2, 0));
            Singleton.getInstance().setCpu(new Avatar(cputxt, 0));

            clip.stop();
            HelloApplication.showWindow("game.fxml");
            Stage currentStage = (Stage) btnPlay.getScene().getWindow();
            currentStage.hide();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Complete all the fields");
            alert.showAndWait();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String uri = HelloApplication.class.getResource("audio/preGame.wav").getPath();
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
}
