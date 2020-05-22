package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.sql.PreparedStatement;

public class CreationUserController {

    @FXML
    private Button createNewUserButton;

    @FXML
    private TextField newUserLoginField;

    @FXML
    private TextField newUserPasswordField;

    @FXML
    private CheckBox createroleCheck;

    @FXML
    private CheckBox createdbCheck;

    @FXML
    private CheckBox superuserCheck;

    @FXML
    private Label statusLabel;

    @FXML
    void createNewUser(ActionEvent event) {

        String attributes = "LOGIN ";
        if (createdbCheck.isSelected())
            attributes = attributes.concat(" CREATEDB");

        if (createroleCheck.isSelected())
            attributes = attributes.concat(" CREATEROLE");

        if (superuserCheck.isSelected())
            attributes = attributes.concat(" SUPERUSER");

        try {
            PostqresController.connection.setAutoCommit(true);
            PreparedStatement preState = null;
            preState = PostqresController.connection.prepareStatement("SELECT create_user(?,?,?)");
            preState.setString(1, newUserLoginField.getText());
            preState.setString(2, newUserPasswordField.getText());
            preState.setString(3, attributes);
            preState.execute();
            preState.close();
            statusLabel.setTextFill(Paint.valueOf("BLACK"));
            statusLabel.setText("Новый пользователь создан!");
        } catch (Exception exception) {
            statusLabel.setTextFill(Paint.valueOf("RED"));
            statusLabel.setText(exception.getMessage());
        }
    }

}