/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.model.MataKuliahModel;
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
public class MataKuliahDAO {

    // Method untuk menambah mata kuliah baru
    public boolean addMataKuliah(MataKuliahModel mk) {
        // Cek apakah kode atau nama mata kuliah sudah ada
        if (isMataKuliahExists(mk.getKodeMk(), mk.getNamaMk())) {
            System.err.println("Gagal menambah: Kode atau Nama mata kuliah sudah ada.");
            return false;
        }

        String query = "INSERT INTO mata_kuliah (kode_mk, nama_mk, sks) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, mk.getKodeMk());
            pstmt.setString(2, mk.getNamaMk());
            pstmt.setInt(3, mk.getSks());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error menambah mata kuliah: " + e.getMessage());
            return false;
        }
    }

    // Method helper untuk cek duplikasi kode
    private boolean isMataKuliahExists(String kodeMk, String namaMk) {
        String query = "SELECT COUNT(*) FROM mata_kuliah WHERE kode_mk = ? OR nama_mk = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, kodeMk);
            pstmt.setString(2, namaMk);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek duplikasi mata kuliah: " + e.getMessage());
        }
        return false;
    }

    // Method untuk mengambil semua data (nanti dipakai untuk menampilkan di tabel)
    public List<MataKuliahModel> getAllMataKuliah() {
        List<MataKuliahModel> listMk = new ArrayList<>();
        String query = "SELECT id_mk, kode_mk, nama_mk, sks FROM mata_kuliah ORDER BY nama_mk";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MataKuliahModel mk = new MataKuliahModel();
                mk.setIdMk(rs.getInt("id_mk"));
                mk.setKodeMk(rs.getString("kode_mk"));
                mk.setNamaMk(rs.getString("nama_mk"));
                mk.setSks(rs.getInt("sks"));
                listMk.add(mk);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mata kuliah: " + e.getMessage());
        }
        return listMk;
    }

    public void deleteMataKuliah(int idMk) {
        String query = "DELETE FROM mata_kuliah WHERE id_mk = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idMk);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error menghapus mata kuliah: " + e.getMessage());
        }
    }

    public boolean updateMataKuliah(MataKuliahModel mk) {
        // Cek apakah kode atau nama baru sudah ada
        if (isMataKuliahExistsForUpdate(mk.getKodeMk(), mk.getNamaMk(), mk.getIdMk())) {
            System.err.println("Gagal mengupdate: Kode atau Nama mata kuliah sudah digunakan oleh data lain.");
            return false;
        }

        String query = "UPDATE mata_kuliah SET kode_mk = ?, nama_mk = ?, sks = ? WHERE id_mk = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, mk.getKodeMk());
            pstmt.setString(2, mk.getNamaMk());
            pstmt.setInt(3, mk.getSks());
            pstmt.setInt(4, mk.getIdMk());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error mengupdate mata kuliah: " + e.getMessage());
            return false;
        }
    }

    private boolean isMataKuliahExistsForUpdate(String kodeMk, String namaMk, int currentIdMk) {
        String query = "SELECT COUNT(*) FROM mata_kuliah WHERE (kode_mk = ? OR nama_mk = ?) AND id_mk != ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, kodeMk);
            pstmt.setString(2, namaMk);
            pstmt.setInt(3, currentIdMk);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek duplikasi mata kuliah untuk update: " + e.getMessage());
        }
        return false;
    }
}
