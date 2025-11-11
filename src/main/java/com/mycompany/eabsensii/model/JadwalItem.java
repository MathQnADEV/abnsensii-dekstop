/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eabsensii.model;

/**
 *
 * @author shaqy
 */
public class JadwalItem {
    private final int idJadwal;
    private final String displayText;

    public JadwalItem(int idJadwal, String displayText) {
        this.idJadwal = idJadwal;
        this.displayText = displayText;
    }

    public int getIdJadwal() {
        return idJadwal;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
