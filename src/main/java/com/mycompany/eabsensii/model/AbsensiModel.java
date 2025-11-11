/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

import java.sql.Timestamp;

/**
 *
 * @author shaqy
 */
public class AbsensiModel {
    
    // Field tabel di database
    private int id_absensi;
    private int id_jadwal;
    private int id_mahasiswa;
    private String tanggal_absensi;
    private Timestamp jam_masuk;      
    private String status;           
    private String keterangan;
    
    // Field helper
    private String namaJadwalFormatted; 
    private String namaMahasiswaFormatted; 

    public AbsensiModel() {
    }

    public AbsensiModel(int id_jadwal, int id_mahasiswa, String tanggal_absensi, Timestamp jam_masuk, String status, String keterangan) {
        this.id_jadwal = id_jadwal;
        this.id_mahasiswa = id_mahasiswa;
        this.tanggal_absensi = tanggal_absensi;
        this.jam_masuk = jam_masuk;
        this.status = status;
        this.keterangan = keterangan;
    }
    
    public AbsensiModel(int id_absensi, int id_jadwal, int id_mahasiswa, String tanggal_absensi, Timestamp jam_masuk, String status, String keterangan, String namaJadwalFormatted, String namaMahasiswaFormatted) {
        this.id_absensi = id_absensi;
        this.id_jadwal = id_jadwal;
        this.id_mahasiswa = id_mahasiswa;
        this.tanggal_absensi = tanggal_absensi;
        this.jam_masuk = jam_masuk;
        this.status = status;
        this.keterangan = keterangan;
        this.namaJadwalFormatted = namaJadwalFormatted;
        this.namaMahasiswaFormatted = namaMahasiswaFormatted;
    }

    public int getId_absensi() {
        return id_absensi;
    }

    public void setId_absensi(int id_absensi) {
        this.id_absensi = id_absensi;
    }

    public int getId_jadwal() {
        return id_jadwal;
    }

    public void setId_jadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public int getId_mahasiswa() {
        return id_mahasiswa;
    }

    public void setId_mahasiswa(int id_mahasiswa) {
        this.id_mahasiswa = id_mahasiswa;
    }

    public String getTanggal_absensi() {
        return tanggal_absensi;
    }

    public void setTanggal_absensi(String tanggal_absensi) {
        this.tanggal_absensi = tanggal_absensi;
    }

    public Timestamp getJam_masuk() {
        return jam_masuk;
    }

    public void setJam_masuk(Timestamp jam_masuk) {
        this.jam_masuk = jam_masuk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaJadwalFormatted() {
        return namaJadwalFormatted;
    }

    public void setNamaJadwalFormatted(String namaJadwalFormatted) {
        this.namaJadwalFormatted = namaJadwalFormatted;
    }

    public String getNamaMahasiswaFormatted() {
        return namaMahasiswaFormatted;
    }

    public void setNamaMahasiswaFormatted(String namaMahasiswaFormatted) {
        this.namaMahasiswaFormatted = namaMahasiswaFormatted;
    }
}