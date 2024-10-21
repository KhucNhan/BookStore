package com.example.book_store.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML
    private TextField name;

    @FXML
    private TextField uesrName;

    @FXML
    private TextField birthday;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField address;

    @FXML
    private PasswordField passWord;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private MenuButton gender;
    private String selectedGender;

    public void initialize(){

    }

}
