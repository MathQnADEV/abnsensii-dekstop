/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.model.KelasModel;
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
public class KelasDAO {

    // Method ambil data kelas
    public List<KelasModel> getAllKelas() {
        List<KelasModel> listKelas = new ArrayList<>();
        String query = "SELECT k.id_kelas, k.nama_kelas, p.nama_prodi, p.tahun "
                     + "FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi "
                     + "ORDER BY p.nama_prodi, k.nama_kelas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KelasModel kelas = new KelasModel(
                    rs.getInt("id_kelas"),
                    rs.getString("nama_kelas"),
                    rs.getString("nama_prodi"),
                    rs.getString("tahun")
                );
                listKelas.add(kelas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data kelas: " + e.getMessage());
        }
        return listKelas;
    }

    // Method tambah kelas
    public void addKelas(KelasModel kelas) {
        // Cari id_prodi berdasarkan nama prodi yang dipilih di ComboBox
        int idProdi = getProdiIdByName(kelas.getNamaProdi());
        if (idProdi == -1) {
            System.err.println("Prodi tidak ditemukan!");
            return;
        }

        String query = "INSERT INTO kelas (nama_kelas, id_prodi) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, kelas.getNamaKelas());
            pstmt.setInt(2, idProdi);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error menambah kelas: " + e.getMessage());
        }
    }
    
    // Method helper untuk mendapatkan ID Prodi dari nama
    public int getProdiIdByName(String namaProdi) {
        String query = "SELECT id_prodi FROM prodi WHERE nama_prodi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaProdi);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_prodi");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID prodi: " + e.getMessage());
        }
        return -1; // Kembalikan -1 jika tidak ditemukan
    }
    
    // --- Method untuk mengisi ComboBox Prodi ---
    public List<String> getAllProdiNames() {
        List<String> listProdi = new ArrayList<>();
        String query = "SELECT nama_prodi FROM prodi ORDER BY nama_prodi";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listProdi.add(rs.getString("nama_prodi"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data prodi: " + e.getMessage());
        }
        return listProdi;
    }
}
