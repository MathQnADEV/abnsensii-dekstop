/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class DashboardModel {

    private int totalMahasiswa;
    private int totalDosen;
    private int totalKelas;
    private double tingkatKehadiranBulanIni;

    public DashboardModel(int totalMahasiswa, int totalDosen, int totalKelas, double tingkatKehadiranBulanIni) {
        this.totalMahasiswa = totalMahasiswa;
        this.totalDosen = totalDosen;
        this.totalKelas = totalKelas;
        this.tingkatKehadiranBulanIni = tingkatKehadiranBulanIni;
    }

    public int getTotalMahasiswa() {
        return totalMahasiswa;
    }

    public void setTotalMahasiswa(int totalMahasiswa) {
        this.totalMahasiswa = totalMahasiswa;
    }

    public int getTotalDosen() {
        return totalDosen;
    }

    public void setTotalDosen(int totalDosen) {
        this.totalDosen = totalDosen;
    }

    public int getTotalKelas() {
        return totalKelas;
    }

    public void setTotalKelas(int totalKelas) {
        this.totalKelas = totalKelas;
    }

    public double getTingkatKehadiranBulanIni() {
        return tingkatKehadiranBulanIni;
    }

    public void setTingkatKehadiranBulanIni(double tingkatKehadiranBulanIni) {
        this.tingkatKehadiranBulanIni = tingkatKehadiranBulanIni;
    }
}
