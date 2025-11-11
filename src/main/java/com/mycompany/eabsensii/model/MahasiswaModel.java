/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class MahasiswaModel {

    private int idMahasiswa;
    private String nim;
    private String namaMahasiswa;
    private int idKelas;
    //    Field Helper
    private String formattedKelas;

    public MahasiswaModel() {
    }

    public MahasiswaModel(String nim, String namaMahasiswa, int idKelas) {
        this.nim = nim;
        this.namaMahasiswa = namaMahasiswa;
        this.idKelas = idKelas;
    }

    public int getIdMahasiswa() {
        return idMahasiswa;
    }

    public void setIdMahasiswa(int idMahasiswa) {
        this.idMahasiswa = idMahasiswa;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public int getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(int idKelas) {
        this.idKelas = idKelas;
    }

    public String getFormattedKelas() {
        return formattedKelas;
    }

    public void setFormattedKelas(String formattedKelas) {
        this.formattedKelas = formattedKelas;
    }
}
