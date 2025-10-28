/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class KelasModel {

    private int idKelas;
    private String namaKelas;
    private String namaProdi; // Nama Prodi dari tabel 'prodi'
    private String tahun;     // Tahun dari tabel 'prodi'

    public KelasModel(int idKelas, String namaKelas, String namaProdi, String tahun) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.namaProdi = namaProdi;
        this.tahun = tahun;
    }
    
    public KelasModel(String namaKelas, String namaProdi, String tahun) {
        this.namaKelas = namaKelas;
        this.namaProdi = namaProdi;
        this.tahun = tahun;
    }

    public int getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(int idKelas) {
        this.idKelas = idKelas;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getNamaProdi() {
        return namaProdi;
    }

    public void setNamaProdi(String namaProdi) {
        this.namaProdi = namaProdi;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }
}
