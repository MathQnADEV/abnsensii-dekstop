/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class DosenModel {
    private int idDosen;
    private String nidn;
    private String namaDosen;

    public DosenModel() {
    }

    public DosenModel(String nidn, String namaDosen) {
        this.nidn = nidn;
        this.namaDosen = namaDosen;
    }

    public int getIdDosen() { return idDosen; }
    public void setIdDosen(int idDosen) { this.idDosen = idDosen; }
    
    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }
    
    public String getNamaDosen() { return namaDosen; }
    public void setNamaDosen(String namaDosen) { this.namaDosen = namaDosen; }
}
