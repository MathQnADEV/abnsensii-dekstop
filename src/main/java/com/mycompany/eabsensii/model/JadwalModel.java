package com.mycompany.eabsensii.model;

public class JadwalModel {

    private int id_jadwal;
    private int id_kelas;
    private int id_mk;
    private int id_dosen;
    private String hari;
    private String jam_mulai;
    private String jam_selesai;
    private String namaMatakuliah;
    private String namaDosen;
    private String namaKelasFormatted;

    public JadwalModel() {
    }

    public JadwalModel(int id_kelas, int id_mk, int id_dosen, String hari, String jam_mulai, String jam_selesai) {
        this.id_kelas = id_kelas;
        this.id_mk = id_mk;
        this.id_dosen = id_dosen;
        this.hari = hari;
        this.jam_mulai = jam_mulai;
        this.jam_selesai = jam_selesai;
    }

    public JadwalModel(int id_jadwal, int id_kelas, int id_mk, int id_dosen, String hari, String jam_mulai, String jam_selesai) {
        this.id_jadwal = id_jadwal;
        this.id_kelas = id_kelas;
        this.id_mk = id_mk;
        this.id_dosen = id_dosen;
        this.hari = hari;
        this.jam_mulai = jam_mulai;
        this.jam_selesai = jam_selesai;
    }

    public int getId_jadwal() {
        return id_jadwal;
    }

    public void setId_jadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public int getId_kelas() {
        return id_kelas;
    }

    public void setId_kelas(int id_kelas) {
        this.id_kelas = id_kelas;
    }

    public int getId_mk() {
        return id_mk;
    }

    public void setId_mk(int id_mk) {
        this.id_mk = id_mk;
    }

    public int getId_dosen() {
        return id_dosen;
    }

    public void setId_dosen(int id_dosen) {
        this.id_dosen = id_dosen;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getJam_mulai() {
        return jam_mulai;
    }

    public void setJam_mulai(String jam_mulai) {
        this.jam_mulai = jam_mulai;
    }

    public String getJam_selesai() {
        return jam_selesai;
    }

    public void setJam_selesai(String jam_selesai) {
        this.jam_selesai = jam_selesai;
    }

    public String getNamaMatakuliah() {
        return namaMatakuliah;
    }

    public void setNamaMatakuliah(String namaMatakuliah) {
        this.namaMatakuliah = namaMatakuliah;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getNamaKelasFormatted() {
        return namaKelasFormatted;
    }

    public void setNamaKelasFormatted(String namaKelasFormatted) {
        this.namaKelasFormatted = namaKelasFormatted;
    }
}
