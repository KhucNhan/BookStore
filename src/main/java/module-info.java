module com.example.book_store {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.book_store to javafx.fxml;
    exports com.example.book_store;
}