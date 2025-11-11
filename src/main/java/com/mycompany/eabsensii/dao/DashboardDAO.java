/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.DatabaseConnection;
import com.mycompany.eabsensii.model.DashboardModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author shaqy
 */
public class DashboardDAO {
    
    public DashboardModel getDashboardStats() {
        int totalMahasiswa = getTotalMahasiswa();
        int totalDosen = getTotalDosen();
        int totalKelas = getTotalKelas();
        double tingkatKehadiran = getTingkatKehadiranBulanIni();

        return new DashboardModel(totalMahasiswa, totalDosen, totalKelas, tingkatKehadiran);
    }

    private int getTotalMahasiswa() {
        String query = "SELECT COUNT(*) FROM mahasiswa";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung total mahasiswa: " + e.getMessage());
        }
        return 0;
    }

    private int getTotalDosen() {
        String query = "SELECT COUNT(*) FROM dosen";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung total dosen: " + e.getMessage());
        }
        return 0;
    }
    
    private int getTotalKelas() {
        String query = "SELECT COUNT(*) FROM kelas";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung total kelas: " + e.getMessage());
        }
        return 0;
    }

    private double getTingkatKehadiranBulanIni() {
        // Satu query untuk menghitung total kehadiran dan total yang hadir sekaligus
        String query = "SELECT "
                + "COUNT(*) as total_records, "
                + "SUM(CASE WHEN status = 'Hadir' THEN 1 ELSE 0 END) as hadir_records "
                + "FROM absensi "
                + "WHERE MONTH(tanggal_absensi) = MONTH(CURDATE()) AND YEAR(tanggal_absensi) = YEAR(CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int totalRecords = rs.getInt("total_records");
                int hadirRecords = rs.getInt("hadir_records");

                // Hindari pembagian dengan nol
                if (totalRecords > 0) {
                    // Lakukan pembagian dengan cara yang benar-benar menghasilkan desimal
                    return (double) hadirRecords / totalRecords * 100.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung tingkat kehadiran: " + e.getMessage());
        }
        return 0.0;
    }
}
