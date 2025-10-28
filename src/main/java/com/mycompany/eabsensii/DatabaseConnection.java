/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author shaqy
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/eabsensi";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi ke Database Gagal: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Mencoba koneksi ke database...");

        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("SUKSES! Koneksi ke database berhasil.");
            try {
                conn.close(); // Jangan lupa tutup koneksi
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
            }
        } else {
            System.out.println("GAGAL! Tidak bisa terhubung ke database.");
            System.out.println("Periksa: Apakah MySQL sudah jalan? Apakah nama database, user, dan password sudah benar?");
        }
    }
}
