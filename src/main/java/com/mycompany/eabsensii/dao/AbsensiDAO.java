/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.dao;

import com.mycompany.eabsensii.DatabaseConnection;
import com.mycompany.eabsensii.model.AbsensiModel;
import com.mycompany.eabsensii.model.JadwalItem;
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
public class AbsensiDAO {

    // --- METHOD TAMBAH ---
    public boolean addAbsensi(AbsensiModel absensi) {
        // Cek apakah data absensi sudah ada (untuk mencegah duplikat)
        if (isAbsensiExists(absensi.getId_jadwal(), absensi.getId_mahasiswa(), absensi.getTanggal_absensi())) {
            System.err.println("Gagal menambah: Data absensi untuk mahasiswa ini pada jadwal dan tanggal yang sama sudah ada.");
            return false;
        }

        String query = "INSERT INTO absensi (id_jadwal, id_mahasiswa, tanggal_absensi, jam_masuk, status, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, absensi.getId_jadwal());
            pstmt.setInt(2, absensi.getId_mahasiswa());
            pstmt.setDate(3, java.sql.Date.valueOf(absensi.getTanggal_absensi()));
            pstmt.setTimestamp(4, absensi.getJam_masuk());
            pstmt.setString(5, absensi.getStatus());
            pstmt.setString(6, absensi.getKeterangan());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error menambah absensi: " + e.getMessage());
            return false;
        }
    }

    // --- METHOD UBAH ---
    public boolean updateAbsensi(AbsensiModel absensi) {
        String query = "UPDATE absensi SET id_jadwal = ?, id_mahasiswa = ?, tanggal_absensi = ?, jam_masuk = ?, status = ?, keterangan = ? WHERE id_absensi = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, absensi.getId_jadwal());
            pstmt.setInt(2, absensi.getId_mahasiswa());
            pstmt.setDate(3, java.sql.Date.valueOf(absensi.getTanggal_absensi()));
            pstmt.setTimestamp(4, absensi.getJam_masuk());
            pstmt.setString(5, absensi.getStatus());
            pstmt.setString(6, absensi.getKeterangan());
            pstmt.setInt(7, absensi.getId_absensi());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error mengupdate absensi: " + e.getMessage());
            return false;
        }
    }

    // --- METHOD HAPUS ---
    public boolean deleteAbsensi(int idAbsensi) {
        String query = "DELETE FROM absensi WHERE id_absensi = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idAbsensi);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error menghapus absensi: " + e.getMessage());
            return false;
        }
    }

    public List<AbsensiModel> getAllAbsensi() {
        List<AbsensiModel> listAbsensi = new ArrayList<>();
        // tambah a.id_jadwal dan a.id_mahasiswa ---
        String query = "SELECT a.id_absensi, a.id_jadwal, a.id_mahasiswa, a.tanggal_absensi, a.jam_masuk, a.status, a.keterangan, "
                + "m.nim, m.nama_mahasiswa, "
                + "CONCAT(mk.nama_mk, ' - ', j.hari, ', ', j.jam_mulai) AS jadwal_info "
                + "FROM absensi a "
                + "JOIN mahasiswa m ON a.id_mahasiswa = m.id_mahasiswa "
                + "JOIN jadwal j ON a.id_jadwal = j.id_jadwal "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "ORDER BY a.tanggal_absensi DESC, m.nama_mahasiswa";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AbsensiModel absensi = new AbsensiModel();
                absensi.setId_absensi(rs.getInt("id_absensi"));
                absensi.setId_jadwal(rs.getInt("id_jadwal"));
                absensi.setId_mahasiswa(rs.getInt("id_mahasiswa"));
                // ----------------------------------------------------

                absensi.setTanggal_absensi(rs.getDate("tanggal_absensi").toString());
                absensi.setJam_masuk(rs.getTimestamp("jam_masuk"));
                absensi.setStatus(rs.getString("status"));
                absensi.setKeterangan(rs.getString("keterangan"));
                absensi.setNamaMahasiswaFormatted(rs.getString("nim") + " - " + rs.getString("nama_mahasiswa"));
                absensi.setNamaJadwalFormatted(rs.getString("jadwal_info"));
                listAbsensi.add(absensi);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data absensi: " + e.getMessage());
        }
        return listAbsensi;
    }

    // --- method helper combobox ---
    public List<JadwalItem> getAllJadwalFormatted() {
        List<JadwalItem> listJadwal = new ArrayList<>();
        String query = "SELECT j.id_jadwal, mk.nama_mk, j.hari, j.jam_mulai, k.nama_kelas, p.tahun "
                + "FROM jadwal j "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "JOIN kelas k ON j.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "ORDER BY j.hari, j.jam_mulai";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int idJadwal = rs.getInt("id_jadwal");
                String displayText = rs.getString("nama_mk") + " - " + rs.getString("hari") + ", " + rs.getTime("jam_mulai").toString().substring(0, 5) + " - " + rs.getString("nama_kelas");
                listJadwal.add(new JadwalItem(idJadwal, displayText));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data jadwal: " + e.getMessage());
        }
        return listJadwal;
    }

    public int getJadwalIdByFormatted(String formattedJadwal) {
        String[] parts = formattedJadwal.split(" - ");
        if (parts.length >= 2) {
            String namaMk = parts[0];
            String hariWaktu = parts[1];
            String[] hariWaktuParts = hariWaktu.split(", ");
            if (hariWaktuParts.length >= 2) {
                String hari = hariWaktuParts[0];
                String jam = hariWaktuParts[1];
                String query = "SELECT j.id_jadwal FROM jadwal j "
                        + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                        + "WHERE mk.nama_mk = ? AND j.hari = ? AND j.jam_mulai = ?";
                try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, namaMk);
                    pstmt.setString(2, hari);
                    pstmt.setString(3, jam + ":00");
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("id_jadwal");
                    }
                } catch (SQLException e) {
                    System.err.println("Error mencari ID jadwal: " + e.getMessage());
                }
            }
        }
        return -1;
    }

    public List<String> getMahasiswaByJadwal(int idJadwal) {
        List<String> listMahasiswa = new ArrayList<>();
        // Query untuk mengambil mahasiswa berdasarkan kelas dari jadwal yang dipilih
        String query = "SELECT m.nim, m.nama_mahasiswa FROM mahasiswa m "
                + "JOIN kelas k ON m.id_kelas = k.id_kelas "
                + "JOIN jadwal j ON k.id_kelas = j.id_kelas "
                + "WHERE j.id_jadwal = ? "
                + "ORDER BY m.nama_mahasiswa";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idJadwal);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listMahasiswa.add(rs.getString("nim") + " - " + rs.getString("nama_mahasiswa"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data mahasiswa per jadwal: " + e.getMessage());
        }
        return listMahasiswa;
    }

    public int getMahasiswaIdByFormatted(String formattedMahasiswa) {
        String[] parts = formattedMahasiswa.split(" - ");
        if (parts.length >= 2) {
            String nim = parts[0];
            String query = "SELECT id_mahasiswa FROM mahasiswa WHERE nim = ?";
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nim);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id_mahasiswa");
                }
            } catch (SQLException e) {
                System.err.println("Error mencari ID mahasiswa: " + e.getMessage());
            }
        }
        return -1;
    }

    public List<String> getAllStatus() {
        List<String> listStatus = new ArrayList<>();
        // Query ini mengambil semua pilihan dari tipe data ENUM
        String query = "SHOW COLUMNS FROM absensi WHERE Field = 'status'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String enumValues = rs.getString("Type");
                enumValues = enumValues.substring(enumValues.indexOf("(") + 1, enumValues.indexOf(")"));
                String[] values = enumValues.split(",");
                for (String value : values) {
                    listStatus.add(value.replace("'", ""));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data status: " + e.getMessage());
        }
        return listStatus;
    }

    // --- Method Helper Validasion ---
    private boolean isAbsensiExists(int idJadwal, int idMahasiswa, String tanggal) {
        String query = "SELECT COUNT(*) FROM absensi WHERE id_jadwal = ? AND id_mahasiswa = ? AND tanggal_absensi = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idJadwal);
            pstmt.setInt(2, idMahasiswa);
            pstmt.setDate(3, java.sql.Date.valueOf(tanggal));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error mengecek absensi: " + e.getMessage());
        }
        return false;
    }

    public List<AbsensiModel> getAbsensiByFilter(int idJadwal, String tanggal) {
        List<AbsensiModel> listAbsensi = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT a.id_absensi, a.id_jadwal, a.id_mahasiswa, a.tanggal_absensi, a.jam_masuk, a.status, a.keterangan, "
                + "m.nim, m.nama_mahasiswa, "
                + "CONCAT(mk.nama_mk, ' - ', j.hari, ', ', j.jam_mulai) AS jadwal_info "
                + "FROM absensi a "
                + "JOIN mahasiswa m ON a.id_mahasiswa = m.id_mahasiswa "
                + "JOIN jadwal j ON a.id_jadwal = j.id_jadwal "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "WHERE 1=1"
        );

        if (idJadwal != -1) {
            queryBuilder.append(" AND a.id_jadwal = ?");
        }
        queryBuilder.append(" AND a.tanggal_absensi = ?");
        queryBuilder.append(" ORDER BY a.tanggal_absensi DESC, m.nama_mahasiswa");

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

            int paramIndex = 1;
            if (idJadwal != -1) {
                pstmt.setInt(paramIndex++, idJadwal);
            }
            pstmt.setDate(paramIndex, java.sql.Date.valueOf(tanggal));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AbsensiModel absensi = new AbsensiModel();
                    absensi.setId_absensi(rs.getInt("id_absensi"));
                    absensi.setId_jadwal(rs.getInt("id_jadwal"));
                    absensi.setId_mahasiswa(rs.getInt("id_mahasiswa"));
                    absensi.setTanggal_absensi(rs.getDate("tanggal_absensi").toString());
                    absensi.setJam_masuk(rs.getTimestamp("jam_masuk"));
                    absensi.setStatus(rs.getString("status"));
                    absensi.setKeterangan(rs.getString("keterangan"));
                    absensi.setNamaMahasiswaFormatted(rs.getString("nim") + " - " + rs.getString("nama_mahasiswa"));
                    absensi.setNamaJadwalFormatted(rs.getString("jadwal_info"));
                    listAbsensi.add(absensi);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data absensi terfilter: " + e.getMessage());
        }
        return listAbsensi;
    }

    public Map<String, List<AbsensiModel>> getAllAbsensiGroupedByClass() {
        Map<String, List<AbsensiModel>> dataPerKelas = new HashMap<>();

        // Query untuk mengambil semua data absensi beserta nama kelas dan tahunnya
        String query = "SELECT a.id_absensi, a.id_jadwal, a.id_mahasiswa, a.tanggal_absensi, a.jam_masuk, a.status, a.keterangan, "
                + "m.nim, m.nama_mahasiswa, "
                + "CONCAT(mk.nama_mk, ' - ', j.hari, ', ', j.jam_mulai) AS jadwal_info, "
                + "k.nama_kelas, p.tahun "
                + "FROM absensi a "
                + "JOIN mahasiswa m ON a.id_mahasiswa = m.id_mahasiswa "
                + "JOIN jadwal j ON a.id_jadwal = j.id_jadwal "
                + "JOIN kelas k ON j.id_kelas = k.id_kelas "
                + "JOIN prodi p ON k.id_prodi = p.id_prodi "
                + "JOIN mata_kuliah mk ON j.id_mk = mk.id_mk "
                + "ORDER BY k.nama_kelas, p.tahun, mk.nama_mk, m.nama_mahasiswa";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AbsensiModel absensi = new AbsensiModel();
                absensi.setId_absensi(rs.getInt("id_absensi"));
                absensi.setId_jadwal(rs.getInt("id_jadwal"));
                absensi.setId_mahasiswa(rs.getInt("id_mahasiswa"));
                absensi.setTanggal_absensi(rs.getDate("tanggal_absensi").toString());
                absensi.setJam_masuk(rs.getTimestamp("jam_masuk"));
                absensi.setStatus(rs.getString("status"));
                absensi.setKeterangan(rs.getString("keterangan"));
                absensi.setNamaMahasiswaFormatted(rs.getString("nim") + " - " + rs.getString("nama_mahasiswa"));
                absensi.setNamaJadwalFormatted(rs.getString("jadwal_info"));

                // key map (nama kelas dan tahun)
                String namaKelas = rs.getString("nama_kelas") + " - " + rs.getString("tahun");

                // Jika nama kelas belum ada di map, make new list
                if (!dataPerKelas.containsKey(namaKelas)) {
                    dataPerKelas.put(namaKelas, new ArrayList<>());
                }

                // Tambahkan absensi ke list yang sesuai dengan kelasnya
                dataPerKelas.get(namaKelas).add(absensi);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data absensi per kelas: " + e.getMessage());
        }
        return dataPerKelas;
    }
}
