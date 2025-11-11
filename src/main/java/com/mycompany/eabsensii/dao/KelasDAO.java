/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.model.KelasModel;
import com.mycompany.eabsensii.DatabaseConnection;
import com.mycompany.eabsensii.model.KelasExportModel;
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

    // --- Method Get All ---
    public List<KelasModel> getAllKelas() {
        List<KelasModel> listKelas = new ArrayList<>();
        String query = "SELECT k.id_kelas, k.nama_kelas, p.nama_prodi, p.tahun "
                + "FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY "
                + "    SUBSTRING(k.nama_kelas, 1, LENGTH(k.nama_kelas) - 2) ASC, "
                + "    p.tahun ASC, "
                + "    RIGHT(k.nama_kelas, 1) ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

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

    public List<String> getAllProdiNames() {
        List<String> listProdi = new ArrayList<>();
        String query = "SELECT DISTINCT nama_prodi FROM prodi ORDER BY nama_prodi";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listProdi.add(rs.getString("nama_prodi"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data prodi: " + e.getMessage());
        }
        return listProdi;
    }

    // --- Method Tambah ---
    public boolean addKelas(KelasModel kelas) {
        if (isKelasNameExists(kelas.getNamaKelas(), kelas.getNamaProdi(), kelas.getTahun())) {
            System.err.println("Gagal menambah: Nama kelas sudah ada untuk prodi dan tahun tersebut.");
            return false;
        }

        int idProdi = getProdiIdByNameAndYear(kelas.getNamaProdi(), kelas.getTahun());
        if (idProdi == -1) {
            System.out.println("Prodi dan tahun baru, membuat data prodi...");
            idProdi = addProdi(kelas.getNamaProdi(), kelas.getTahun());
            if (idProdi == -1) {
                System.err.println("Gagal membuat data prodi baru.");
                return false;
            }
        }

        String query = "INSERT INTO kelas (nama_kelas, id_prodi) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, kelas.getNamaKelas());
            pstmt.setInt(2, idProdi);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error menambah kelas: " + e.getMessage());
            return false;
        }
    }

    // --- Method Hapus ---
    public void deleteKelas(int idKelas) {
        String query = "DELETE FROM kelas WHERE id_kelas = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idKelas);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error menghapus kelas: " + e.getMessage());
        }
    }

    // --- Method Helper Generate Next Class ---
    public String generateNextClassName(String namaProdi, String tahun) {
        String lastClassName = null;
        String query = "SELECT k.nama_kelas "
                + "FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "WHERE p.nama_prodi = ? AND p.tahun = ? "
                + "ORDER BY k.nama_kelas DESC "
                + "LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaProdi);
            pstmt.setString(2, tahun);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lastClassName = rs.getString("nama_kelas");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari nama kelas terakhir: " + e.getMessage());
            return null;
        }
        if (lastClassName == null) {
            return "A";
        }
        try {
            String prefix = lastClassName.substring(0, lastClassName.length() - 1);
            char lastChar = lastClassName.charAt(lastClassName.length() - 1);
            char nextChar = (char) (lastChar + 1);
            return prefix + nextChar;
        } catch (StringIndexOutOfBoundsException e) {
            return "A";
        }
    }

    private boolean isKelasNameExists(String namaKelas, String namaProdi, String tahun) {
        String query = "SELECT COUNT(*) FROM kelas k "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "WHERE LOWER(k.nama_kelas) = LOWER(?) AND LOWER(p.nama_prodi) = LOWER(?) AND p.tahun = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaKelas);
            pstmt.setString(2, namaProdi);
            pstmt.setString(3, tahun);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek nama kelas: " + e.getMessage());
        }
        return false;
    }

    // Method helper untuk pengecekan duplikat saat update
    private boolean isKelasNameExistsForUpdate(String namaKelas, String namaProdi, String tahun, int currentIdKelas) {
        String query = "SELECT COUNT(*) FROM kelas k "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "WHERE LOWER(k.nama_kelas) = LOWER(?) AND LOWER(p.nama_prodi) = LOWER(?) AND p.tahun = ? AND k.id_kelas != ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaKelas);
            pstmt.setString(2, namaProdi);
            pstmt.setString(3, tahun);
            pstmt.setInt(4, currentIdKelas);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek nama kelas untuk update: " + e.getMessage());
        }
        return false;
    }

    private int getProdiIdByNameAndYear(String namaProdi, String tahun) {
        String query = "SELECT id_prodi FROM prodi WHERE LOWER(nama_prodi) = LOWER(?) AND tahun = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaProdi);
            pstmt.setString(2, tahun);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_prodi");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID prodi: " + e.getMessage());
        }
        return -1;
    }

    private int addProdi(String namaProdi, String tahun) {
        String query = "INSERT INTO prodi (nama_prodi, tahun) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, namaProdi);
            pstmt.setString(2, tahun);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menambah prodi: " + e.getMessage());
        }
        return -1;
    }

    public List<KelasExportModel> getAllKelasWithStudentCount() {
        List<KelasExportModel> listData = new ArrayList<>();

        // Query yang benar, mengambil tahun dari tabel 'prodi'
        String query = "SELECT "
                + "k.nama_kelas, "
                + "p.nama_prodi, "
                + "p.tahun, "
                + "COUNT(m.id_mahasiswa) AS total_mahasiswa "
                + "FROM kelas k "
                + "LEFT JOIN mahasiswa m ON k.id_kelas = m.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "GROUP BY k.id_kelas, k.nama_kelas, p.nama_prodi, p.tahun "
                + "ORDER BY p.nama_prodi, k.nama_kelas";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String namaKelas = rs.getString("nama_kelas");
                String namaProdi = rs.getString("nama_prodi");
                String tahun = rs.getString("tahun");
                int totalMahasiswa = rs.getInt("total_mahasiswa");

                listData.add(new KelasExportModel(namaKelas, namaProdi, tahun, totalMahasiswa));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data kelas untuk export: " + e.getMessage());
        }
        return listData;
    }
}
