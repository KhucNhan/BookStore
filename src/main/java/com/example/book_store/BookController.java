package com.example.book_store;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BookController {
    @FXML
    public void getBooks(ActionEvent event) {
        ConnectDB connectDB = new ConnectDB();
        String query = "select TenSach, TacGia, NoiDungTomTat, NamXuatBan, LanXuatBan, GiaBan, SoLuong, TenLoaiSach, TenNhaXuatBan from sach " +
                "join loaisach on sach.MaLoaiSach = loaisach.MaLoaiSach " +
                "join nhaxuatban on sach.MaNhaXuatBan = nhaxuatban.MaNhaXuatBan";
        Connection connection;

        try {
            connection = connectDB.connectionDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                System.out.println(
                        "Tên sách: " + resultSet.getString(2)
                                + "|| Tác giả: " + resultSet.getString(3)
                                + "|| Nội dung tóm tắt: " + resultSet.getString(4)
                                + "|| Năm xuất bản: " + resultSet.getInt(5)
                                + "|| Lần xuất bản: " + resultSet.getInt(6)
                                + "|| Giá bán: " + resultSet.getDouble(7)
                                + "|| Loại sách: " + resultSet.getString("TenLoaiSach")
                                + "|| Nhà xuất bản: " + resultSet.getString("TenNhaXuatBan")
                );
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}