/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.DatabaseConnection;
import com.mycompany.eabsensii.model.JadwalModel;
import com.mycompany.eabsensii.model.MataKuliahModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shaqy
 */
public class JadwalDAO {

    // --- Method Tambah ---
    public boolean addJadwal(JadwalModel jadwal) {
        if (isJadwalConflict(jadwal)) {
            System.err.println("Gagal menambah: Jadwal bentrok dengan jadwal yang sudah ada (dosen atau kelas sibuk).");
            return false;
        }

        String query = "INSERT INTO jadwal (id_kelas, id_mk, id_dosen, hari, jam_mulai, jam_selesai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, jadwal.getId_kelas());
            pstmt.setInt(2, jadwal.getId_mk());
            pstmt.setInt(3, jadwal.getId_dosen());
            pstmt.setString(4, jadwal.getHari());
            pstmt.setTime(5, Time.valueOf(jadwal.getJam_mulai()));
            pstmt.setTime(6, Time.valueOf(jadwal.getJam_selesai()));
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error menambah jadwal: " + e.getMessage());
            return false;
        }
    }

    // --- Method Liat Semua ---
    public List<JadwalModel> getAllJadwal() {
        List<JadwalModel> listJadwal = new ArrayList<>();
        String query = "SELECT j.id_jadwal, j.id_kelas, j.hari, j.jam_mulai, j.jam_selesai, "
                + "mk.nama_mk, d.nama_dosen, k.nama_kelas, p.tahun "
                + "FROM jadwal j "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "JOIN dosen d ON j.id_dosen = d.id_dosen "
                + "JOIN kelas k ON j.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY j.hari, j.jam_mulai";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                JadwalModel jadwal = new JadwalModel();
                jadwal.setId_jadwal(rs.getInt("id_jadwal"));
                jadwal.setId_kelas(rs.getInt("id_kelas"));
                jadwal.setHari(rs.getString("hari"));
                jadwal.setJam_mulai(rs.getTime("jam_mulai").toString());
                jadwal.setJam_selesai(rs.getTime("jam_selesai").toString());
                jadwal.setNamaMatakuliah(rs.getString("nama_mk"));
                jadwal.setNamaDosen(rs.getString("nama_dosen"));
                jadwal.setNamaKelasFormatted(rs.getString("nama_kelas") + " - " + rs.getString("tahun"));
                listJadwal.add(jadwal);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data jadwal: " + e.getMessage());
        }
        return listJadwal;
    }

    // --- Method Ubah ---
    public boolean updateJadwal(JadwalModel jadwal) {
        // Cek bentrok dengan jadwal LAIN (kecuali dirinya sendiri)
        if (isJadwalConflictForUpdate(jadwal)) {
            System.err.println("Gagal mengupdate: Jadwal bentrok dengan jadwal yang sudah ada.");
            return false;
        }

        String query = "UPDATE jadwal SET id_kelas = ?, id_mk = ?, id_dosen = ?, hari = ?, jam_mulai = ?, jam_selesai = ? WHERE id_jadwal = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, jadwal.getId_kelas());
            pstmt.setInt(2, jadwal.getId_mk());
            pstmt.setInt(3, jadwal.getId_dosen());
            pstmt.setString(4, jadwal.getHari());
            pstmt.setTime(5, Time.valueOf(jadwal.getJam_mulai()));
            pstmt.setTime(6, Time.valueOf(jadwal.getJam_selesai()));
            pstmt.setInt(7, jadwal.getId_jadwal());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; 

        } catch (SQLException e) {
            System.err.println("Error mengupdate jadwal: " + e.getMessage());
            return false;
        }
    }

    // --- Method Hapus ---
    public boolean deleteJadwal(int idJadwal) {
        String query = "DELETE FROM jadwal WHERE id_jadwal = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idJadwal);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error menghapus jadwal: " + e.getMessage());
            return false;
        }
    }

    // --- Method Helper Combobox ---
    public List<String> getAllKelasFormatted() {
        List<String> listKelas = new ArrayList<>();
        String query = "SELECT k.nama_kelas, p.tahun FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi ORDER BY p.tahun DESC, k.nama_kelas";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listKelas.add(rs.getString("nama_kelas") + " - " + rs.getString("tahun"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data kelas: " + e.getMessage());
        }
        return listKelas;
    }

    public int getKelasIdByNama(String formattedKelas) {
        String[] parts = formattedKelas.split(" - ");
        if (parts.length != 2) {
            return -1;
        }
        String namaKelas = parts[0];
        String tahun = parts[1];
        String query = "SELECT k.id_kelas FROM kelas k JOIN prodi p ON k.id_prodi = p.id_prodi WHERE k.nama_kelas = ? AND p.tahun = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaKelas);
            pstmt.setString(2, tahun);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_kelas");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID kelas: " + e.getMessage());
        }
        return -1;
    }

    public List<String> getAllMataKuliah() {
        List<String> listMK = new ArrayList<>();
        String query = "SELECT nama_mk FROM mata_kuliah ORDER BY nama_mk";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listMK.add(rs.getString("nama_mk"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data matakuliah: " + e.getMessage());
        }
        return listMK;
    }

    public int getMataKuliahIdByNama(String namaMK) {
        String query = "SELECT id_mk FROM mata_kuliah WHERE nama_mk = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaMK);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_mk");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID matakuliah: " + e.getMessage());
        }
        return -1;
    }

    public List<String> getAllDosen() {
        List<String> listDosen = new ArrayList<>();
        String query = "SELECT nama_dosen FROM dosen ORDER BY nama_dosen";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listDosen.add(rs.getString("nama_dosen"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data dosen: " + e.getMessage());
        }
        return listDosen;
    }

    public int getDosenIdByNama(String namaDosen) {
        String query = "SELECT id_dosen FROM dosen WHERE nama_dosen = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaDosen);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_dosen");
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ID dosen: " + e.getMessage());
        }
        return -1;
    }

    public List<String> getAllHari() {
        List<String> listHari = new ArrayList<>();
        String query = "SHOW COLUMNS FROM jadwal WHERE Field = 'hari'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String enumValues = rs.getString("Type");
                enumValues = enumValues.substring(enumValues.indexOf("(") + 1, enumValues.indexOf(")"));
                String[] values = enumValues.split(",");
                for (String value : values) {
                    listHari.add(value.replace("'", ""));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data hari: " + e.getMessage());
        }
        return listHari;
    }

    public int getSksByNama(String namaMK) {
        String query = "SELECT sks FROM mata_kuliah WHERE nama_mk = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, namaMK);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("sks");
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil SKS matakuliah: " + e.getMessage());
        }
        return -1;
    }

    // --- Method Helper Validasi Bentrok ---
    private boolean isJadwalConflict(JadwalModel jadwal) {
        String query = "SELECT COUNT(*) FROM jadwal WHERE hari = ? AND "
                + "((jam_mulai <= ? AND jam_selesai > ?) OR (jam_mulai < ? AND jam_selesai >= ?)) AND "
                + "(id_dosen = ? OR id_kelas = ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            Time mulai = Time.valueOf(jadwal.getJam_mulai());
            Time selesai = Time.valueOf(jadwal.getJam_selesai());

            pstmt.setString(1, jadwal.getHari());
            pstmt.setTime(2, mulai);
            pstmt.setTime(3, mulai);
            pstmt.setTime(4, selesai);
            pstmt.setTime(5, selesai);
            pstmt.setInt(6, jadwal.getId_dosen());
            pstmt.setInt(7, jadwal.getId_kelas());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Jika count > 0, berarti ada bentrok
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek bentrok jadwal: " + e.getMessage());
        }
        return false;
    }

    private boolean isJadwalConflictForUpdate(JadwalModel jadwal) {
        String query = "SELECT COUNT(*) FROM jadwal WHERE hari = ? AND id_jadwal != ? AND "
                + "((jam_mulai <= ? AND jam_selesai > ?) OR (jam_mulai < ? AND jam_selesai >= ?)) AND "
                + "(id_dosen = ? OR id_kelas = ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            Time mulai = Time.valueOf(jadwal.getJam_mulai());
            Time selesai = Time.valueOf(jadwal.getJam_selesai());

            pstmt.setString(1, jadwal.getHari());
            pstmt.setInt(2, jadwal.getId_jadwal());
            pstmt.setTime(3, mulai);
            pstmt.setTime(4, mulai);
            pstmt.setTime(5, selesai);
            pstmt.setTime(6, selesai);
            pstmt.setInt(7, jadwal.getId_dosen());
            pstmt.setInt(8, jadwal.getId_kelas());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek bentrok jadwal untuk update: " + e.getMessage());
        }
        return false;
    }

    public Map<String, List<MataKuliahModel>> getAllMataKuliahGroupedByProdi() {
        Map<String, List<MataKuliahModel>> dataPerProdi = new HashMap<>();

        // Query yang diperbaiki untuk mengikuti jalur hubungan tabel
        String query = "SELECT DISTINCT mk.nama_mk, mk.kode_mk, mk.sks, p.nama_prodi "
                + "FROM mata_kuliah mk "
                + "JOIN jadwal j ON mk.id_mk = j.id_mk "
                + "JOIN kelas k ON j.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY p.nama_prodi, mk.nama_mk";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MataKuliahModel mk = new MataKuliahModel();
                mk.setNamaMk(rs.getString("nama_mk"));
                mk.setKodeMk(rs.getString("kode_mk"));
                mk.setSks(rs.getInt("sks"));

                String namaProdi = rs.getString("nama_prodi");

                // Jika nama prodi belum ada di map, buat list baru
                if (!dataPerProdi.containsKey(namaProdi)) {
                    dataPerProdi.put(namaProdi, new ArrayList<>());
                }

                // Tambahkan mata kuliah ke list yang sesuai dengan program studinya
                dataPerProdi.get(namaProdi).add(mk);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mata kuliah per prodi: " + e.getMessage());
        }
        return dataPerProdi;
    }

    public Map<String, List<JadwalModel>> getAllJadwalGroupedByClass() {
        Map<String, List<JadwalModel>> dataPerKelas = new HashMap<>();

        // Query untuk mengambil semua data jadwal lengkap
        String query = "SELECT j.id_jadwal, j.id_kelas, j.hari, j.jam_mulai, j.jam_selesai, "
                + "mk.nama_mk, d.nama_dosen, k.nama_kelas, p.tahun "
                + "FROM jadwal j "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "JOIN dosen d ON j.id_dosen = d.id_dosen "
                + "JOIN kelas k ON j.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY k.nama_kelas, p.tahun, j.hari, j.jam_mulai";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                JadwalModel jadwal = new JadwalModel();
                jadwal.setId_jadwal(rs.getInt("id_jadwal"));
                jadwal.setId_kelas(rs.getInt("id_kelas"));
                jadwal.setHari(rs.getString("hari"));
                jadwal.setJam_mulai(rs.getTime("jam_mulai").toString());
                jadwal.setJam_selesai(rs.getTime("jam_selesai").toString());
                jadwal.setNamaMatakuliah(rs.getString("nama_mk"));
                jadwal.setNamaDosen(rs.getString("nama_dosen"));

                // Buat key untuk map (nama kelas dan tahun)
                String namaKelasFormatted = rs.getString("nama_kelas") + " - " + rs.getString("tahun");
                jadwal.setNamaKelasFormatted(namaKelasFormatted);

                // Jika nama kelas belum ada di map, buat list baru
                if (!dataPerKelas.containsKey(namaKelasFormatted)) {
                    dataPerKelas.put(namaKelasFormatted, new ArrayList<>());
                }

                // Tambahkan jadwal ke list yang sesuai dengan kelasnya
                dataPerKelas.get(namaKelasFormatted).add(jadwal);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data jadwal per kelas: " + e.getMessage());
        }
        return dataPerKelas;
    }
}
