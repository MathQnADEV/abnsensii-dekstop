/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.eabsensii.view;

import com.mycompany.eabsensii.EventBus;
import com.mycompany.eabsensii.dao.AbsensiDAO;
import com.mycompany.eabsensii.model.AbsensiModel; // PASTIKAN IMPORT INI ADA
import com.mycompany.eabsensii.model.JadwalItem;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author shaqy
 */
public class PanelAbsensi extends javax.swing.JPanel implements EventBus.DataChangeListener {

    private AbsensiDAO absensiDAO;
    private List<AbsensiModel> listAbsensiData;
    private boolean isEditing = false;
    private int idAbsensiSedangDiedit = 0;

    public PanelAbsensi() {
        initComponents();
        absensiDAO = new AbsensiDAO();
        listAbsensiData = new ArrayList<>();

        loadDataToComboBoxes();
        tampilkanDataKeTabel();
        spnTanggal.setEditor(new javax.swing.JSpinner.DateEditor(spnTanggal, "dd-MM-yyyy"));
        spnFilterTanggal.setEditor(new javax.swing.JSpinner.DateEditor(spnFilterTanggal, "dd-MM-yyyy"));

        EventBus.subscribe((EventBus.DataChangeListener) this); 

        tblAbsensi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel(tblAbsensi.getSelectedRow());
            }
        });

        spnFilterTanggal.addChangeListener(e -> {
            tampilkanDataKeTabel();
        });
    }

    private void loadDataToComboBoxes() {
        // Isi ComboBox Jadwal (untuk input)
        List<JadwalItem> listJadwal = absensiDAO.getAllJadwalFormatted();
        cmbJadwal.removeAllItems();
        cmbJadwal.addItem(new JadwalItem(-1, "-- Pilih Jadwal --"));
        for (JadwalItem item : listJadwal) {
            cmbJadwal.addItem(item);
        }

        // Isi ComboBox Filter Jadwal
        cmbFilterJadwal.removeAllItems();
        cmbFilterJadwal.addItem(new JadwalItem(-1, "-- Semua Jadwal --")); // Opsi untuk menampilkan semua
        for (JadwalItem item : listJadwal) {
            cmbFilterJadwal.addItem(item);
        }

        // Isi ComboBox Status
        List<String> listStatus = absensiDAO.getAllStatus();
        cmbStatus.removeAllItems();
        cmbStatus.addItem("-- Pilih Status --");
        for (String status : listStatus) {
            cmbStatus.addItem(status);
        }

        cmbMahasiswa.removeAllItems();
        cmbMahasiswa.addItem("-- Pilih Mahasiswa --");
    }

    private void clearForm() {
        cmbJadwal.setSelectedIndex(0);
        cmbMahasiswa.removeAllItems();
        cmbMahasiswa.addItem("-- Pilih Mahasiswa --");
        cmbStatus.setSelectedIndex(0);
        txtAreaKeterangan.setText("");
        spnTanggal.setValue(new java.util.Date());
    }

    private void tampilkanDataKeTabel() {
        DefaultTableModel model = (DefaultTableModel) tblAbsensi.getModel();
        model.setRowCount(0);
        
        // Ambil kriteria filter dari form filter
        JadwalItem selectedFilterJadwalItem = (JadwalItem) cmbFilterJadwal.getSelectedItem();
        int idFilterJadwal = (selectedFilterJadwalItem != null) ? selectedFilterJadwalItem.getIdJadwal() : -1;
        String tanggalFilter = new SimpleDateFormat("yyyy-MM-dd").format(spnFilterTanggal.getValue());
        listAbsensiData = absensiDAO.getAbsensiByFilter(idFilterJadwal, tanggalFilter);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        int no = 1;
        for (AbsensiModel absensi : listAbsensiData) {
            String jamMasukFormatted = "";
            if (absensi.getJam_masuk() != null) {
                jamMasukFormatted = timeFormat.format(absensi.getJam_masuk());
            }
            Object[] rowData = {
                no++,
                absensi.getNamaMahasiswaFormatted(),
                absensi.getNamaJadwalFormatted(),
                absensi.getStatus(),
                jamMasukFormatted,
                absensi.getKeterangan()
            };
            model.addRow(rowData);
        }
    }

    public void onDataChanged() {
        // Refresh data di ComboBox dan Tabel
        loadDataToComboBoxes();
        tampilkanDataKeTabel();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // Berhenti berlangganan saat panel dihapus dari layar
        EventBus.unsubscribe((EventBus.DataChangeListener) this);
    }

    private void resetToAddMode() {
        isEditing = false;
        idAbsensiSedangDiedit = 0;
        btnSimpan.setText("Simpan");
        btnHapus.setEnabled(false);
        clearForm();
    }

    private void isiFormDariTabel(int selectedRow) {
        // Jika tidak ada baris yang dipilih, reset form ke mode tambah
        if (selectedRow == -1) {
            resetToAddMode();
            return;
        }

        // Ambil data model dari baris yang dipilih
        int modelRow = tblAbsensi.convertRowIndexToModel(selectedRow);
        AbsensiModel absensiYangDiedit = listAbsensiData.get(modelRow);

        // Isi Combo Jadwal
        for (int i = 0; i < cmbJadwal.getItemCount(); i++) {
            JadwalItem item = cmbJadwal.getItemAt(i);
            if (item.getIdJadwal() == absensiYangDiedit.getId_jadwal()) {
                cmbJadwal.setSelectedItem(item);
                break;
            }
        }

        // Isi Combo Mahasiswa (otomatis terisi saat combo jadwal berubah)
        SwingUtilities.invokeLater(() -> {
            cmbMahasiswa.setSelectedItem(absensiYangDiedit.getNamaMahasiswaFormatted());
        });

        // Isi Tanggal
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date tanggal = dateFormat.parse(absensiYangDiedit.getTanggal_absensi());
            spnTanggal.setValue(tanggal);
        } catch (ParseException e) {
            spnTanggal.setValue(new java.util.Date());
        }

        // Isi Status dan Keterangan
        cmbStatus.setSelectedItem(absensiYangDiedit.getStatus());
        txtAreaKeterangan.setText(absensiYangDiedit.getKeterangan());

        // Aktifkan mode edit
        isEditing = true;
        idAbsensiSedangDiedit = absensiYangDiedit.getId_absensi();
        btnSimpan.setText("Update");
        btnHapus.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelAbsensi = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblJadwal = new javax.swing.JLabel();
        cmbJadwal = new javax.swing.JComboBox<>();
        lblMahasiswa = new javax.swing.JLabel();
        cmbMahasiswa = new javax.swing.JComboBox<>();
        lblTanggal = new javax.swing.JLabel();
        spnTanggal = new javax.swing.JSpinner();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblKeterangan = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaKeterangan = new javax.swing.JTextArea();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAbsensi = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        lblFilterJadwal = new javax.swing.JLabel();
        cmbFilterJadwal = new javax.swing.JComboBox<>();
        lblFilterTanggal = new javax.swing.JLabel();
        spnFilterTanggal = new javax.swing.JSpinner();

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Absensi");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblJadwal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJadwal.setText("Jadwal:");

        cmbJadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJadwalActionPerformed(evt);
            }
        });

        lblMahasiswa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMahasiswa.setText("Mahasiswa:");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTanggal.setText("Tanggal:");

        spnTanggal.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        spnTanggal.setToolTipText("");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStatus.setText("Status:");

        lblKeterangan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKeterangan.setText("Keterangan:");

        txtAreaKeterangan.setColumns(20);
        txtAreaKeterangan.setRows(5);
        jScrollPane3.setViewportView(txtAreaKeterangan);

        btnSimpan.setBackground(new java.awt.Color(0, 153, 153));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(0, 153, 153));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnBersihkan.setBackground(new java.awt.Color(0, 153, 153));
        btnBersihkan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBersihkan.setForeground(new java.awt.Color(255, 255, 255));
        btnBersihkan.setText("Bersihkan");
        btnBersihkan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        tblAbsensi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nama Mahasiswa", "Jadwal", "Status", "Jam Masuk", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblAbsensi);
        if (tblAbsensi.getColumnModel().getColumnCount() > 0) {
            tblAbsensi.getColumnModel().getColumn(0).setMinWidth(40);
            tblAbsensi.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblAbsensi.getColumnModel().getColumn(0).setMaxWidth(40);
            tblAbsensi.getColumnModel().getColumn(1).setMinWidth(150);
            tblAbsensi.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblAbsensi.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        lblFilterJadwal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFilterJadwal.setText("Filter Jadwal:");

        cmbFilterJadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterJadwalActionPerformed(evt);
            }
        });

        lblFilterTanggal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFilterTanggal.setText("Filter Tanggal:");

        spnFilterTanggal.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        spnFilterTanggal.setToolTipText("");

        javax.swing.GroupLayout panelAbsensiLayout = new javax.swing.GroupLayout(panelAbsensi);
        panelAbsensi.setLayout(panelAbsensiLayout);
        panelAbsensiLayout.setHorizontalGroup(
            panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAbsensiLayout.createSequentialGroup()
                .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelAbsensiLayout.createSequentialGroup()
                                .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMahasiswa)
                                    .addComponent(lblJadwal)
                                    .addComponent(lblTanggal)
                                    .addComponent(lblStatus))
                                .addGap(18, 18, 18)
                                .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cmbJadwal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbMahasiswa, 0, 253, Short.MAX_VALUE)
                                            .addComponent(spnTanggal))
                                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelAbsensiLayout.createSequentialGroup()
                                                .addGap(50, 50, 50)
                                                .addComponent(jSeparator1))
                                            .addGroup(panelAbsensiLayout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(lblKeterangan)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 33, Short.MAX_VALUE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAbsensiLayout.createSequentialGroup()
                                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblFilterTanggal)
                                            .addGroup(panelAbsensiLayout.createSequentialGroup()
                                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblFilterJadwal)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(spnFilterTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                                            .addComponent(cmbFilterJadwal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(40, 40, 40))))
                            .addGroup(panelAbsensiLayout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)
                                .addGap(18, 18, 18)
                                .addComponent(btnBersihkan))))
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addGap(364, 364, 364)
                        .addComponent(jLabel5)))
                .addGap(30, 30, 30))
        );
        panelAbsensiLayout.setVerticalGroup(
            panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAbsensiLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblJadwal)
                            .addComponent(lblKeterangan))
                        .addGap(18, 18, 18)
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMahasiswa))
                        .addGap(18, 18, 18)
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTanggal)
                            .addComponent(spnTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFilterJadwal)
                            .addComponent(cmbFilterJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFilterTanggal)
                            .addComponent(spnFilterTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelAbsensiLayout.createSequentialGroup()
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblStatus)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addGroup(panelAbsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBersihkan)
                            .addComponent(btnHapus)
                            .addComponent(btnSimpan))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelAbsensi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelAbsensi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // --- 1. Validasi Input ---
        if (cmbJadwal.getSelectedIndex() == 0 || cmbMahasiswa.getSelectedIndex() == 0 || cmbStatus.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Harap pilih Jadwal, Mahasiswa, dan Status!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Ambil Data Dari Form ---
        JadwalItem selectedJadwalItem = (JadwalItem) cmbJadwal.getSelectedItem();
        int idJadwal = selectedJadwalItem.getIdJadwal();
        int idMahasiswa = absensiDAO.getMahasiswaIdByFormatted(cmbMahasiswa.getSelectedItem().toString());
        String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(spnTanggal.getValue());
        Timestamp jamMasuk = new Timestamp(System.currentTimeMillis()); // Ambil waktu saat ini
        String status = cmbStatus.getSelectedItem().toString();
        String keterangan = txtAreaKeterangan.getText();

        boolean berhasil;

        // --- 3. Cek Mode ---
        if (isEditing) {
            // Mode Update
            AbsensiModel absensiUpdate = new AbsensiModel();
            absensiUpdate.setId_absensi(idAbsensiSedangDiedit);
            absensiUpdate.setId_jadwal(idJadwal);
            absensiUpdate.setId_mahasiswa(idMahasiswa);
            absensiUpdate.setTanggal_absensi(tanggal);
            absensiUpdate.setJam_masuk(jamMasuk);
            absensiUpdate.setStatus(status);
            absensiUpdate.setKeterangan(keterangan);

            berhasil = absensiDAO.updateAbsensi(absensiUpdate);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data absensi berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Mode Tambah
            AbsensiModel absensiBaru = new AbsensiModel(idJadwal, idMahasiswa, tanggal, jamMasuk, status, keterangan);
            berhasil = absensiDAO.addAbsensi(absensiBaru);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data absensi berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data. Data mungkin sudah ada atau terjadi kesalahan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // --- 4. refresh and reset ---
        if (berhasil) {
            cmbFilterJadwal.setSelectedItem(cmbJadwal.getSelectedItem());
            spnFilterTanggal.setValue(spnTanggal.getValue());
            tampilkanDataKeTabel();
            resetToAddMode();
            EventBus.notifyDataChanged();
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // 1. Ambil baris yang dihighlight
        int selectedRow = tblAbsensi.getSelectedRow();

        // 2. Validasi
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Konfirmasi penghapusan
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            // 4. Ambil ID absensi dari model
            int modelRow = tblAbsensi.convertRowIndexToModel(selectedRow);
            int idAbsensi = listAbsensiData.get(modelRow).getId_absensi();

            // 5. Panggil DAO
            boolean berhasil = absensiDAO.deleteAbsensi(idAbsensi);

            // 6. Tampilkan pesan hasil dan refresh tabel
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data absensi berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                tampilkanDataKeTabel();
                resetToAddMode();
                EventBus.notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        resetToAddMode();
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void cmbJadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJadwalActionPerformed
        Object selectedItem = cmbJadwal.getSelectedItem();

        if (selectedItem instanceof JadwalItem selectedJadwalItem) {
            int idJadwal = selectedJadwalItem.getIdJadwal();

            if (idJadwal == -1) {
                cmbMahasiswa.removeAllItems();
                cmbMahasiswa.addItem("-- Pilih Mahasiswa --");
                return;
            }

            List<String> listMahasiswa = absensiDAO.getMahasiswaByJadwal(idJadwal);
            cmbMahasiswa.removeAllItems();
            cmbMahasiswa.addItem("-- Pilih Mahasiswa --");
            for (String mhs : listMahasiswa) {
                cmbMahasiswa.addItem(mhs);
            }
        }
    }//GEN-LAST:event_cmbJadwalActionPerformed

    private void cmbFilterJadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterJadwalActionPerformed
        tampilkanDataKeTabel();
    }//GEN-LAST:event_cmbFilterJadwalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<JadwalItem> cmbFilterJadwal;
    private javax.swing.JComboBox<JadwalItem> cmbJadwal;
    private javax.swing.JComboBox<String> cmbMahasiswa;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFilterJadwal;
    private javax.swing.JLabel lblFilterTanggal;
    private javax.swing.JLabel lblJadwal;
    private javax.swing.JLabel lblKeterangan;
    private javax.swing.JLabel lblMahasiswa;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JPanel panelAbsensi;
    private javax.swing.JSpinner spnFilterTanggal;
    private javax.swing.JSpinner spnTanggal;
    private javax.swing.JTable tblAbsensi;
    private javax.swing.JTextArea txtAreaKeterangan;
    // End of variables declaration//GEN-END:variables
}
