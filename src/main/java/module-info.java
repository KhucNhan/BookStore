module com.example.book_store {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.book_store to javafx.fxml;
    exports com.example.book_store;
    exports com.example.book_store.Entity;
    opens com.example.book_store.Entity to javafx.fxml;
    exports com.example.book_store.Controller;
    opens com.example.book_store.Controller to javafx.fxml;
}