/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.model.MahasiswaModel;
import com.mycompany.eabsensii.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shaqy
 */
public class MahasiswaDAO {

    // Method untuk menambah mahasiswa baru
    public boolean addMahasiswa(MahasiswaModel mahasiswa) {
        // Cek apakah NIM sudah ada
        if (isNimExists(mahasiswa.getNim())) {
            System.err.println("Gagal menambah: NIM sudah terdaftar.");
            return false;
        }

        String query = "INSERT INTO mahasiswa (nim, nama_mahasiswa, id_kelas) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, mahasiswa.getNim());
            pstmt.setString(2, mahasiswa.getNamaMahasiswa());
            pstmt.setInt(3, mahasiswa.getIdKelas());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error menambah mahasiswa: " + e.getMessage());
            return false;
        }
    }

    // Method helper untuk cek duplikasi NIM
    private boolean isNimExists(String nim) {
        String query = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek NIM: " + e.getMessage());
        }
        return false;
    }

    // --- Method untuk ComboBox ---
    public List<String> getAllKelasFormatted() {
        List<String> listKelasFormatted = new ArrayList<>();
        String query = "SELECT k.id_kelas, k.nama_kelas, p.tahun "
                + "FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY p.nama_prodi, p.tahun, k.nama_kelas";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String formatted = rs.getString("nama_kelas") + " - " + rs.getString("tahun");
                listKelasFormatted.add(formatted);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data kelas untuk combo: " + e.getMessage());
        }
        return listKelasFormatted;
    }

    // Method untuk mendapatkan id_kelas dari string yang sudah diformat
    public int getKelasIdByFormattedString(String formattedKelas) {
        String query = "SELECT k.id_kelas "
                + "FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "WHERE CONCAT(k.nama_kelas, ' - ', p.tahun) = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, formattedKelas);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_kelas");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID kelas: " + e.getMessage());
        }
        return -1;
    }

    // Method untuk mengambil semua data mahasiswa (nanti dipakai untuk menampilkan di tabel)
    public List<MahasiswaModel> getAllMahasiswa() {
        List<MahasiswaModel> listMahasiswa = new ArrayList<>();
        String query = "SELECT m.id_mahasiswa, m.nim, m.nama_mahasiswa, m.id_kelas, k.nama_kelas, p.tahun "
                + "FROM mahasiswa m JOIN kelas k ON m.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY m.nama_mahasiswa";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MahasiswaModel mhs = new MahasiswaModel();
                mhs.setIdMahasiswa(rs.getInt("id_mahasiswa"));
                mhs.setNim(rs.getString("nim"));
                mhs.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
                mhs.setIdKelas(rs.getInt("id_kelas"));
                mhs.setFormattedKelas(rs.getString("nama_kelas") + " - " + rs.getString("tahun"));
                listMahasiswa.add(mhs);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mahasiswa: " + e.getMessage());
        }
        return listMahasiswa;
    }

    public boolean deleteMahasiswa(int idMahasiswa) {
        String query = "DELETE FROM mahasiswa WHERE id_mahasiswa = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idMahasiswa);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error menghapus mahasiswa: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMahasiswa(MahasiswaModel mahasiswa) {
        // Cek apakah NIM baru sudah digunakan oleh mahasiswa lain
        if (isNimExistsForUpdate(mahasiswa.getNim(), mahasiswa.getIdMahasiswa())) {
            System.err.println("Gagal mengupdate: NIM sudah terdaftar pada mahasiswa lain.");
            return false;
        }

        String query = "UPDATE mahasiswa SET nim = ?, nama_mahasiswa = ?, id_kelas = ? WHERE id_mahasiswa = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, mahasiswa.getNim());
            pstmt.setString(2, mahasiswa.getNamaMahasiswa());
            pstmt.setInt(3, mahasiswa.getIdKelas());
            pstmt.setInt(4, mahasiswa.getIdMahasiswa());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; 

        } catch (SQLException e) {
            System.err.println("Error mengupdate mahasiswa: " + e.getMessage());
            return false;
        }
    }

    private boolean isNimExistsForUpdate(String nim, int currentIdMahasiswa) {
        String query = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ? AND id_mahasiswa != ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nim);
            pstmt.setInt(2, currentIdMahasiswa);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek NIM untuk update: " + e.getMessage());
        }
        return false;
    }

    public List<MahasiswaModel> getMahasiswaByKelas(int idKelas) {
        List<MahasiswaModel> filteredList = new ArrayList<>();
        String query = "SELECT m.id_mahasiswa, m.nim, m.nama_mahasiswa, m.id_kelas, k.nama_kelas, p.tahun "
                + "FROM mahasiswa m JOIN kelas k ON m.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "WHERE m.id_kelas = ? "
                + "ORDER BY m.nama_mahasiswa";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idKelas);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MahasiswaModel mhs = new MahasiswaModel();
                mhs.setIdMahasiswa(rs.getInt("id_mahasiswa"));
                mhs.setNim(rs.getString("nim"));
                mhs.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
                mhs.setIdKelas(rs.getInt("id_kelas"));
                mhs.setFormattedKelas(rs.getString("nama_kelas") + " - " + rs.getString("tahun"));
                filteredList.add(mhs);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mahasiswa per kelas: " + e.getMessage());
        }
        return filteredList;
    }

    public Map<String, List<MahasiswaModel>> getAllMahasiswaGroupedByClass() {
        Map<String, List<MahasiswaModel>> dataPerKelas = new HashMap<>();
        String query = "SELECT m.id_mahasiswa, m.nim, m.nama_mahasiswa, m.id_kelas, "
                + "CONCAT(k.nama_kelas, ' - ', p.tahun) AS nama_kelas_formatted "
                + "FROM mahasiswa m "
                + "JOIN kelas k ON m.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY k.nama_kelas, p.tahun, m.nama_mahasiswa";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MahasiswaModel mhs = new MahasiswaModel();
                mhs.setIdMahasiswa(rs.getInt("id_mahasiswa"));
                mhs.setNim(rs.getString("nim"));
                mhs.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
                mhs.setIdKelas(rs.getInt("id_kelas"));
                mhs.setFormattedKelas(rs.getString("nama_kelas_formatted"));

                String namaKelas = mhs.getFormattedKelas();

                // Jika nama kelas belum ada di map, buat list baru
                if (!dataPerKelas.containsKey(namaKelas)) {
                    dataPerKelas.put(namaKelas, new ArrayList<>());
                }

                // Tambahkan mahasiswa ke list yang sesuai dengan kelasnya
                dataPerKelas.get(namaKelas).add(mhs);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mahasiswa per kelas: " + e.getMessage());
        }
        return dataPerKelas;
    }
}
