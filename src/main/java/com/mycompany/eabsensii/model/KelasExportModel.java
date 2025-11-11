/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class KelasExportModel {

    private String namaKelas;
    private String namaProdi;
    private String tahun;
    private int totalMahasiswa;

    public KelasExportModel(String namaKelas, String namaProdi, String tahun, int totalMahasiswa) {
        this.namaKelas = namaKelas;
        this.namaProdi = namaProdi;
        this.tahun = tahun;
        this.totalMahasiswa = totalMahasiswa;
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

    public int getTotalMahasiswa() {
        return totalMahasiswa;
    }

    public void setTotalMahasiswa(int totalMahasiswa) {
        this.totalMahasiswa = totalMahasiswa;
    }
}
