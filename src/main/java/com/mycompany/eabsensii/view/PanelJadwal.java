/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.eabsensii.view;

import com.mycompany.eabsensii.EventBus;
import com.mycompany.eabsensii.dao.JadwalDAO;
import com.mycompany.eabsensii.model.JadwalModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;

/**
 *
 * @author shaqy
 */
public class PanelJadwal extends javax.swing.JPanel implements EventBus.DataChangeListener {

    private JadwalDAO jadwalDAO;
    private List<JadwalModel> listJadwalData;

    public PanelJadwal() {
        initComponents();
        jadwalDAO = new JadwalDAO();
        // Panggil method untuk mengisi data ke ComboBox saat form dibuka
        loadDataToComboBoxes();
        tampilkanDataKeTabel();
        EventBus.subscribe(this);

        tblJadwal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFormData();
            }
        });
    }

    private void loadDataToComboBoxes() {
        // --- Isi ComboBox Kelas ---
        List<String> listKelas = jadwalDAO.getAllKelasFormatted();
        cmbKelas.removeAllItems();
        cmbKelas.addItem("-- Pilih Kelas --");
        for (String kelas : listKelas) {
            cmbKelas.addItem(kelas);
        }

        // --- Isi ComboBox Mata Kuliah ---
        List<String> listMataKuliah = jadwalDAO.getAllMataKuliah();
        cmbMataKuliah.removeAllItems();
        cmbMataKuliah.addItem("-- Pilih Mata Kuliah --");
        for (String mk : listMataKuliah) {
            cmbMataKuliah.addItem(mk);
        }

        // --- Isi ComboBox Dosen ---
        List<String> listDosen = jadwalDAO.getAllDosen();
        cmbDosen.removeAllItems();
        cmbDosen.addItem("-- Pilih Dosen --");
        for (String dosen : listDosen) {
            cmbDosen.addItem(dosen);
        }

        // --- Isi ComboBox Hari ---
        List<String> listHari = jadwalDAO.getAllHari();
        cmbHari.removeAllItems();
        cmbHari.addItem("-- Pilih Hari --");
        for (String hari : listHari) {
            cmbHari.addItem(hari);
        }

        // --- Isi ComboBox Filter Kelas (meskipun belum digunakan) ---
        List<String> listFilterKelas = jadwalDAO.getAllKelasFormatted();
        cmbFilterKelas.removeAllItems();
        cmbFilterKelas.addItem("-- Semua Kelas --");
        for (String kelas : listFilterKelas) {
            cmbFilterKelas.addItem(kelas);
        }
    }

    private void clearForm() {
        cmbKelas.setSelectedIndex(0);
        cmbMataKuliah.setSelectedIndex(0);
        cmbDosen.setSelectedIndex(0);
        cmbHari.setSelectedIndex(0);
        // Set spinner ke waktu default (saat ini)
        spnJamMulai.setValue(new java.util.Date());
        spnJamSelesai.setValue(new java.util.Date());
    }

    private void tampilkanDataKeTabel() {
        DefaultTableModel model = (DefaultTableModel) tblJadwal.getModel();
        model.setRowCount(0);
        listJadwalData = jadwalDAO.getAllJadwal();

        int no = 1;
        for (JadwalModel jadwal : listJadwalData) {
            Object[] rowData = {
                no++,
                jadwal.getNamaMatakuliah(),
                jadwal.getHari(),
                jadwal.getJam_mulai(),
                jadwal.getJam_selesai(),
                jadwal.getNamaDosen(),
                jadwal.getNamaKelasFormatted()
            };
            model.addRow(rowData);
        }
    }

    private void filterTableByKelas(String formattedKelas) {
        // 1. Dapatkan ID kelas dari string yang di Highlight
        int idKelasFilter = jadwalDAO.getKelasIdByNama(formattedKelas);
        if (idKelasFilter == -1) {
            return;
        }

        // 2. Buat model tabel baru untuk hasil filter
        DefaultTableModel filteredModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"No.", "Mata Kuliah", "Hari", "Jam Mulai", "Jam Selesai", "Dosen", "Kelas"}
        );

        // 3. Looping data utama dan tambahkan yang cocok ke model baru
        int no = 1;
        for (JadwalModel jadwal : listJadwalData) {
            if (jadwal.getId_kelas() == idKelasFilter) {
                Object[] rowData = {
                    no++,
                    jadwal.getNamaMatakuliah(),
                    jadwal.getHari(),
                    jadwal.getJam_mulai(),
                    jadwal.getJam_selesai(),
                    jadwal.getNamaDosen(),
                    jadwal.getNamaKelasFormatted()
                };
                filteredModel.addRow(rowData);
            }
        }

        // 4. Terapkan model baru ke tabel
        tblJadwal.setModel(filteredModel);
    }

    @Override
    public void onDataChanged() {
        loadDataToComboBoxes();
        refreshTable();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        EventBus.unsubscribe(this);
    }

    private void loadFormData() {
        int selectedRow = tblJadwal.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil objek JadwalModel lengkap dari list
            JadwalModel selectedJadwal = listJadwalData.get(selectedRow);

            // Isi ComboBox menggunakan nama yang sudah ada di model
            cmbKelas.setSelectedItem(selectedJadwal.getNamaKelasFormatted());
            cmbMataKuliah.setSelectedItem(selectedJadwal.getNamaMatakuliah());
            cmbDosen.setSelectedItem(selectedJadwal.getNamaDosen());
            cmbHari.setSelectedItem(selectedJadwal.getHari());
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                java.util.Date jamMulaiDate = timeFormat.parse(selectedJadwal.getJam_mulai());
                java.util.Date jamSelesaiDate = timeFormat.parse(selectedJadwal.getJam_selesai());
                spnJamMulai.setValue(jamMulaiDate);
                spnJamSelesai.setValue(jamSelesaiDate);
            } catch (ParseException e) {
                System.err.println("Error parsing time for form: " + e.getMessage());
            }
        }
    }

    private void refreshTable() {
        String selectedFilter = cmbFilterKelas.getSelectedItem().toString();
        if (selectedFilter.equals("-- Semua Kelas --")) {
            tampilkanDataKeTabel();
        } else {
            filterTableByKelas(selectedFilter);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelJadwal = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblKelas = new javax.swing.JLabel();
        cmbKelas = new javax.swing.JComboBox<>();
        lblMataKuliah = new javax.swing.JLabel();
        cmbMataKuliah = new javax.swing.JComboBox<>();
        lblDosen = new javax.swing.JLabel();
        cmbDosen = new javax.swing.JComboBox<>();
        lblHari = new javax.swing.JLabel();
        cmbHari = new javax.swing.JComboBox<>();
        lblJamMulai = new javax.swing.JLabel();
        spnJamMulai = new javax.swing.JSpinner();
        lblJamSelesai = new javax.swing.JLabel();
        spnJamSelesai = new javax.swing.JSpinner();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        lblFilterKelas = new javax.swing.JLabel();
        cmbFilterKelas = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJadwal = new javax.swing.JTable();

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Manajemen Data Jadwal Perkuliahan");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblKelas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKelas.setText("Kelas:");

        lblMataKuliah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMataKuliah.setText("Mata Kuliah:");

        cmbMataKuliah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMataKuliahActionPerformed(evt);
            }
        });

        lblDosen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDosen.setText("Dosen:");

        lblHari.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHari.setText("Hari:");

        lblJamMulai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJamMulai.setText("Jam Mulai:");

        spnJamMulai.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        spnJamMulai.setToolTipText("");
        spnJamMulai.setEditor(new javax.swing.JSpinner.DateEditor(spnJamMulai, "HH:mm"));

        lblJamSelesai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJamSelesai.setText("Jam Selesai:");

        spnJamSelesai.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR_OF_DAY));
        spnJamSelesai.setEditor(new javax.swing.JSpinner.DateEditor(spnJamSelesai, "HH:mm"));

        btnTambah.setBackground(new java.awt.Color(0, 153, 153));
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnUbah.setBackground(new java.awt.Color(0, 153, 153));
        btnUbah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUbah.setForeground(new java.awt.Color(255, 255, 255));
        btnUbah.setText("Ubah");
        btnUbah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
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

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblFilterKelas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFilterKelas.setText("Filter Kelas:");

        cmbFilterKelas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterKelasActionPerformed(evt);
            }
        });

        tblJadwal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Mata Kuliah", "Hari", "Jam Mulai", "Jam Selesai", "Dosen", "Kelas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblJadwal);
        if (tblJadwal.getColumnModel().getColumnCount() > 0) {
            tblJadwal.getColumnModel().getColumn(0).setMinWidth(40);
            tblJadwal.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblJadwal.getColumnModel().getColumn(0).setMaxWidth(40);
            tblJadwal.getColumnModel().getColumn(2).setMinWidth(60);
            tblJadwal.getColumnModel().getColumn(2).setPreferredWidth(60);
            tblJadwal.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        javax.swing.GroupLayout panelJadwalLayout = new javax.swing.GroupLayout(panelJadwal);
        panelJadwal.setLayout(panelJadwalLayout);
        panelJadwalLayout.setHorizontalGroup(
            panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelJadwalLayout.createSequentialGroup()
                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelJadwalLayout.createSequentialGroup()
                            .addGap(228, 228, 228)
                            .addComponent(jLabel5))
                        .addGroup(panelJadwalLayout.createSequentialGroup()
                            .addGap(36, 36, 36)
                            .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelJadwalLayout.createSequentialGroup()
                                    .addComponent(btnTambah)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnUbah)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnHapus)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnBersihkan))
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelJadwalLayout.createSequentialGroup()
                                        .addComponent(lblKelas)
                                        .addGap(64, 64, 64)
                                        .addComponent(cmbKelas, 0, 253, Short.MAX_VALUE))
                                    .addGroup(panelJadwalLayout.createSequentialGroup()
                                        .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblMataKuliah)
                                            .addComponent(lblDosen)
                                            .addComponent(lblHari)
                                            .addComponent(lblJamMulai)
                                            .addComponent(lblJamSelesai))
                                        .addGap(18, 18, 18)
                                        .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(spnJamMulai, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                            .addComponent(cmbHari, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbDosen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbMataKuliah, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(spnJamSelesai)))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(lblFilterKelas)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbFilterKelas, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panelJadwalLayout.setVerticalGroup(
            panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelJadwalLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5)
                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelJadwalLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelJadwalLayout.createSequentialGroup()
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblKelas)
                                    .addComponent(cmbKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblMataKuliah)
                                    .addComponent(cmbMataKuliah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDosen)
                                    .addComponent(cmbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblHari)
                                    .addComponent(cmbHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblJamMulai)
                                    .addComponent(spnJamMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblJamSelesai)
                                    .addComponent(spnJamSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnTambah)
                                    .addComponent(btnUbah)
                                    .addComponent(btnHapus)
                                    .addComponent(btnBersihkan)))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelJadwalLayout.createSequentialGroup()
                        .addGap(222, 222, 222)
                        .addGroup(panelJadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFilterKelas)
                            .addComponent(cmbFilterKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbMataKuliahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMataKuliahActionPerformed
        // 1. Ambil nama mata kuliah yang dipilih
        if (cmbMataKuliah.getSelectedItem() == null || cmbMataKuliah.getSelectedItem().toString().equals("-- Pilih Mata Kuliah --")) {
            return;
        }
        
        String selectedMataKuliah = cmbMataKuliah.getSelectedItem().toString();

        // 2. Ambil data SKS dari DAO
        int sks = jadwalDAO.getSksByNama(selectedMataKuliah);

        // 3. Jika SKS valid (lebih dari 0)
        if (sks > 0) {
            java.util.Date jamMulaiDate = (java.util.Date) spnJamMulai.getValue();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(jamMulaiDate);
            int totalMenit = sks * 50;
            calendar.add(java.util.Calendar.MINUTE, totalMenit);
            java.util.Date jamSelesaiDate = calendar.getTime();

            // 4. Set otomatis spinner jam selesai dengan hasil
            spnJamSelesai.setValue(jamSelesaiDate);
        }
    }//GEN-LAST:event_cmbMataKuliahActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // --- 1. Validasi ---
        if (cmbKelas.getSelectedIndex() == 0 || cmbMataKuliah.getSelectedIndex() == 0
                || cmbDosen.getSelectedIndex() == 0 || cmbHari.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Harap pilih semua data dengan benar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Ambil Data Dari Form ---
        String namaKelas = cmbKelas.getSelectedItem().toString();
        String namaMataKuliah = cmbMataKuliah.getSelectedItem().toString();
        String namaDosen = cmbDosen.getSelectedItem().toString();
        String hari = cmbHari.getSelectedItem().toString();

        // --- 3. Konversi Data ---
        int idKelas = jadwalDAO.getKelasIdByNama(namaKelas);
        int idMataKuliah = jadwalDAO.getMataKuliahIdByNama(namaMataKuliah);
        int idDosen = jadwalDAO.getDosenIdByNama(namaDosen);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String jamMulai = timeFormat.format(spnJamMulai.getValue());
        String jamSelesai = timeFormat.format(spnJamSelesai.getValue());

        // --- 4. Objek Model ---
        JadwalModel jadwalBaru = new JadwalModel(idKelas, idMataKuliah, idDosen, hari, jamMulai, jamSelesai);

        // --- 5. Call DAO for cache data ---
        boolean berhasil = jadwalDAO.addJadwal(jadwalBaru);

        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data jadwal berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
            EventBus.notifyDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data. Jadwal mungkin bentrok atau ada kesalahan lain.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        // --- 1. Validasi ---
        int selectedRow = tblJadwal.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data jadwal yang ingin diubah terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Ambil ID jadwal dari list ---
        JadwalModel selectedJadwal = listJadwalData.get(selectedRow);
        int idJadwal = selectedJadwal.getId_jadwal();

        // --- 3. Ambil data baru dari form ---
        if (cmbKelas.getSelectedIndex() == 0 || cmbMataKuliah.getSelectedIndex() == 0
                || cmbDosen.getSelectedIndex() == 0 || cmbHari.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Harap pilih semua data dengan benar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaKelas = cmbKelas.getSelectedItem().toString();
        String namaMataKuliah = cmbMataKuliah.getSelectedItem().toString();
        String namaDosen = cmbDosen.getSelectedItem().toString();
        String hari = cmbHari.getSelectedItem().toString();

        int idKelas = jadwalDAO.getKelasIdByNama(namaKelas);
        int idMataKuliah = jadwalDAO.getMataKuliahIdByNama(namaMataKuliah);
        int idDosen = jadwalDAO.getDosenIdByNama(namaDosen);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String jamMulai = timeFormat.format(spnJamMulai.getValue());
        String jamSelesai = timeFormat.format(spnJamSelesai.getValue());

        // --- 4. Buat objek model dengan ID jadwal yang akan diubah ---
        JadwalModel jadwalDiubah = new JadwalModel(idJadwal, idKelas, idMataKuliah, idDosen, hari, jamMulai, jamSelesai);

        // --- 5. Panggil DAO untuk mengupdate data ---
        boolean berhasil = jadwalDAO.updateJadwal(jadwalDiubah);

        // --- 6. Tampilkan pesan hasil dan refresh ---
        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data jadwal berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
            EventBus.notifyDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data. Jadwal mungkin bentrok.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // --- 1. Validasi---
        int selectedRow = tblJadwal.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Harap pilih data jadwal yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Ambil Objeck Lengkap ---
        JadwalModel selectedJadwal = listJadwalData.get(selectedRow);

        // --- 3. Tampilkan Dialog Confirm ---
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus jadwal '" + selectedJadwal.getNamaMatakuliah() + "' untuk kelas '" + selectedJadwal.getNamaKelasFormatted() + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // --- 4. Ambil id_jadwal dari objek ---
        int idJadwal = selectedJadwal.getId_jadwal();

        // --- 5. Call DAO ---
        boolean berhasil = jadwalDAO.deleteJadwal(idJadwal);

        // --- 6. show message and refresh ---
        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data jadwal berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            EventBus.notifyDataChanged();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data jadwal. Periksa koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void cmbFilterKelasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterKelasActionPerformed

        Object selectedItem = cmbFilterKelas.getSelectedItem();

        // Validasi
        if (selectedItem == null) {
            return;
        }

        String selectedFilter = selectedItem.toString();
        if (selectedFilter.equals("-- Semua Kelas --")) {
            tampilkanDataKeTabel();
        } else {
            filterTableByKelas(selectedFilter);
        }
    }//GEN-LAST:event_cmbFilterKelasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbDosen;
    private javax.swing.JComboBox<String> cmbFilterKelas;
    private javax.swing.JComboBox<String> cmbHari;
    private javax.swing.JComboBox<String> cmbKelas;
    private javax.swing.JComboBox<String> cmbMataKuliah;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblDosen;
    private javax.swing.JLabel lblFilterKelas;
    private javax.swing.JLabel lblHari;
    private javax.swing.JLabel lblJamMulai;
    private javax.swing.JLabel lblJamSelesai;
    private javax.swing.JLabel lblKelas;
    private javax.swing.JLabel lblMataKuliah;
    private javax.swing.JPanel panelJadwal;
    private javax.swing.JSpinner spnJamMulai;
    private javax.swing.JSpinner spnJamSelesai;
    private javax.swing.JTable tblJadwal;
    // End of variables declaration//GEN-END:variables
}
