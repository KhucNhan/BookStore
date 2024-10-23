package com.example.book_store.Controller;

import com.example.book_store.Entity.Book;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RtatisticalController {


    @FXML
    private TableView<Book> bestsellingBooksTableColumn;

    @FXML
    private TableColumn<Book, String> image;

    @FXML
    private TableColumn<Book, String> title;

    @FXML
    private TableColumn<Book, String> author;

    @FXML
    private TableColumn<Book, Integer> soldCount;

    @FXML
    private TableColumn<Book, Double> price;

    // Hàm khởi tạo
    public void initialize() {
        setupTableColumn();
//        loadBestsellingBooks();
    }

    // Thiết lập các cột trong TableColumn
    private void setupTableColumn() {
        image.setCellValueFactory(new PropertyValueFactory<>("image")); // Thay đổi nếu bạn có thuộc tính hình ảnh
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        soldCount.setCellValueFactory(new PropertyValueFactory<>("soldCount"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

    }
}


