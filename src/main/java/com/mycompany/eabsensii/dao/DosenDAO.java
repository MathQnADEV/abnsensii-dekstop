/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.model.DosenModel;
import com.mycompany.eabsensii.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaqy
 */
public class DosenDAO {

    // Method untuk menambah dosen baru
    public boolean addDosen(DosenModel dosen) {
        // Cek apakah NIDN sudah ada
        if (isNidnExists(dosen.getNidn())) {
            System.err.println("Gagal menambah: NIDN sudah terdaftar.");
            return false;
        }

        String query = "INSERT INTO dosen (nidn, nama_dosen) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, dosen.getNidn());
            pstmt.setString(2, dosen.getNamaDosen());
            pstmt.executeUpdate();
            return true; // Berhasil

        } catch (SQLException e) {
            System.err.println("Error menambah dosen: " + e.getMessage());
            return false;
        }
    }

    // Method helper untuk cek duplikasi NIDN
    private boolean isNidnExists(String nidn) {
        String query = "SELECT COUNT(*) FROM dosen WHERE nidn = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nidn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek NIDN: " + e.getMessage());
        }
        return false;
    }

    // Method untuk mengambil semua data Dosen
    public List<DosenModel> getAllDosen() {
        List<DosenModel> listDosen = new ArrayList<>();
        String query = "SELECT id_dosen, nidn, nama_dosen FROM dosen ORDER BY nama_dosen";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DosenModel dosen = new DosenModel();
                dosen.setIdDosen(rs.getInt("id_dosen"));
                dosen.setNidn(rs.getString("nidn"));
                dosen.setNamaDosen(rs.getString("nama_dosen"));
                listDosen.add(dosen);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data dosen: " + e.getMessage());
        }
        return listDosen;
    }

    public void deleteDosen(int idDosen) {
        String query = "DELETE FROM dosen WHERE id_dosen = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idDosen);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error menghapus dosen: " + e.getMessage());
        }
    }

    public boolean updateDosen(DosenModel dosen) {
        // Cek apakah NIDN baru sudah ada
        if (isNidnExistsForUpdate(dosen.getNidn(), dosen.getIdDosen())) {
            System.err.println("Gagal mengupdate: NIDN sudah digunakan oleh dosen lain.");
            return false;
        }

        String query = "UPDATE dosen SET nidn = ?, nama_dosen = ? WHERE id_dosen = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, dosen.getNidn());
            pstmt.setString(2, dosen.getNamaDosen());
            pstmt.setInt(3, dosen.getIdDosen());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error mengupdate dosen: " + e.getMessage());
            return false;
        }
    }

    private boolean isNidnExistsForUpdate(String nidn, int currentIdDosen) {
        String query = "SELECT COUNT(*) FROM dosen WHERE nidn = ? AND id_dosen != ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nidn);
            pstmt.setInt(2, currentIdDosen);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek NIDN untuk update: " + e.getMessage());
        }
        return false;
    }

}
