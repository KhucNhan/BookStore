package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BillController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;
}
