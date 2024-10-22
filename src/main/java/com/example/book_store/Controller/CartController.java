package com.example.book_store.Controller;
import com.example.book_store.Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


public class CartController {

    @FXML
    private TableView<Book> cartTableView;

    @FXML
    private TableColumn<Book, String> product;

    @FXML
    private TableColumn<Book, ImageView> image;

    @FXML
    private TableColumn<Book, Double> price;

    @FXML
    private TableColumn<Book, Integer> quantity;

    @FXML
    private TableColumn<Book, Double> total;

    @FXML
    private Label totalCartLabel;

    @FXML
    private Button buyButton;

    private ObservableList<Book> Books;

    @FXML
    public void initialize() {}

    @FXML
    private void buySelectedBooks() {
        if (!Books.isEmpty()) {
            // Logic xử lý đặt hàng cho các sản phẩm trong giỏ
            System.out.println("Đặt hàng thành công cho " + Books.size() + " sản phẩm!");
        } else {
            System.out.println("Giỏ hàng trống!");
        }
    }
}