/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.eabsensii.view;

import com.mycompany.eabsensii.EventBus;
import com.mycompany.eabsensii.dao.KelasDAO;
import com.mycompany.eabsensii.model.KelasModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author shaqy
 */
public class PanelKelas extends javax.swing.JPanel implements EventBus.DataChangeListener {

    private final KelasDAO kelasDAO;
    private boolean isFirstTime = true;
    private List<KelasModel> listKelasData;
    
    public PanelKelas() {
        initComponents();
        kelasDAO = new KelasDAO();
        listKelasData = new ArrayList<>();
        loadProdiToComboBox();
        cmbProdi.addActionListener(e -> suggestNextClassName());
        txtTahunProdi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                suggestNextClassName();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                suggestNextClassName();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                suggestNextClassName();
            }
        });
        tblKelas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFormData();
            }
        });

        txtCariNamaKelas.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
    }

    private void loadProdiToComboBox() {
        List<String> listProdi = kelasDAO.getAllProdiNames();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(listProdi.toArray(new String[0]));
        cmbProdi.setModel(model);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && isFirstTime) {
            loadDataToTable();
            isFirstTime = false;
        }
    }

    // Mengambil data dari DAO dan tampil ke JTable
    private void loadDataToTable() {
        listKelasData = kelasDAO.getAllKelas();

        DefaultTableModel model = (DefaultTableModel) tblKelas.getModel();
        model.setRowCount(0);

        int no = 1;
        for (KelasModel kelas : listKelasData) { // Gunakan variabel kelas
            Object[] rowData = {
                no++ + ".",
                kelas.getNamaKelas(),
                kelas.getNamaProdi(),
                kelas.getTahun()
            };
            model.addRow(rowData);
        }
    }

    private void clearForm() {
        txtNamaKelas.setText("");
        txtTahunProdi.setText("");
        cmbProdi.setSelectedIndex(-1);
    }

    private void suggestNextClassName() {
        if (cmbProdi.getSelectedItem() == null || txtTahunProdi.getText().trim().isEmpty()) {
            return;
        }

        String prodi = cmbProdi.getSelectedItem().toString();
        String tahun = txtTahunProdi.getText().trim();

        String suggestedName = kelasDAO.generateNextClassName(prodi, tahun);
        if (suggestedName != null) {
            txtNamaKelas.setText(suggestedName);
        }
    }

    private void loadFormData() {
        int selectedRow = tblKelas.getSelectedRow();
        if (selectedRow != -1) {
            KelasModel selectedKelas = listKelasData.get(selectedRow);

            txtNamaKelas.setText(selectedKelas.getNamaKelas());
            txtTahunProdi.setText(selectedKelas.getTahun());
            cmbProdi.setSelectedItem(selectedKelas.getNamaProdi());
        }
    }

    private void filterTable() {
        String searchText = txtCariNamaKelas.getText().trim().toLowerCase();
        DefaultTableModel filteredModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"No", "Nama Kelas", "Program Studi", "Tahun"}
        );

        int no = 1;
        for (KelasModel kelas : listKelasData) {
            if (kelas.getNamaKelas().toLowerCase().contains(searchText)) {
                Object[] rowData = {
                    no++ + ".",
                    kelas.getNamaKelas(),
                    kelas.getNamaProdi(),
                    kelas.getTahun()
                };
                filteredModel.addRow(rowData);
            }
        }
        tblKelas.setModel(filteredModel);
    }

    @Override
    public void onDataChanged() {
        loadDataToTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelKelas = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblProdi = new javax.swing.JLabel();
        cmbProdi = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtTahunProdi = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtNamaKelas = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersih = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKelas = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtCariNamaKelas = new javax.swing.JTextField();

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Manajemen Data Kelas");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblProdi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblProdi.setText("Program Studi:");

        cmbProdi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Teknik Komputer", "Pendidikan Teknik Informatika dan Komputer" }));
        cmbProdi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProdiActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Tahun:");

        txtTahunProdi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTahunProdiActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Nama Kelas:");

        txtNamaKelas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaKelasActionPerformed(evt);
            }
        });

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

        btnBersih.setBackground(new java.awt.Color(0, 153, 153));
        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBersih.setForeground(new java.awt.Color(255, 255, 255));
        btnBersih.setText("Bersihkan");
        btnBersih.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBersih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihActionPerformed(evt);
            }
        });

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(2000, 2000));

        tblKelas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No", "Nama Kelas", "Program Studi", "Tahun"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKelas.setToolTipText("");
        tblKelas.setColumnSelectionAllowed(true);
        tblKelas.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblKelas);
        tblKelas.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblKelas.getColumnModel().getColumnCount() > 0) {
            tblKelas.getColumnModel().getColumn(0).setResizable(false);
            tblKelas.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblKelas.getColumnModel().getColumn(1).setResizable(false);
            tblKelas.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblKelas.getColumnModel().getColumn(2).setResizable(false);
            tblKelas.getColumnModel().getColumn(2).setPreferredWidth(400);
            tblKelas.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Cari Nama Kelas:");

        txtCariNamaKelas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariNamaKelasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelKelasLayout = new javax.swing.GroupLayout(panelKelas);
        panelKelas.setLayout(panelKelasLayout);
        panelKelasLayout.setHorizontalGroup(
            panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKelasLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelKelasLayout.createSequentialGroup()
                        .addComponent(btnTambah)
                        .addGap(18, 18, 18)
                        .addComponent(btnHapus)
                        .addGap(18, 18, 18)
                        .addComponent(btnBersih)
                        .addGap(124, 124, 124)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCariNamaKelas, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelKelasLayout.createSequentialGroup()
                        .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProdi)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbProdi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTahunProdi)
                            .addComponent(txtNamaKelas))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKelasLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKelasLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(291, 291, 291))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelKelasLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))))
        );
        panelKelasLayout.setVerticalGroup(
            panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKelasLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProdi)
                    .addComponent(cmbProdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTahunProdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtNamaKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelKelasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnHapus)
                    .addComponent(btnBersih)
                    .addComponent(jLabel11)
                    .addComponent(txtCariNamaKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbProdiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProdiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbProdiActionPerformed

    private void txtTahunProdiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTahunProdiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTahunProdiActionPerformed

    private void txtNamaKelasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaKelasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaKelasActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // 1. Ambil data dari form
        String namaKelas = txtNamaKelas.getText().trim();
        String prodi = cmbProdi.getSelectedItem().toString();
        String tahun = txtTahunProdi.getText().trim();

        // 2. Validasi input form
        if (namaKelas.isEmpty() || prodi.isEmpty() || tahun.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Buat objek model
        KelasModel newKelas = new KelasModel(namaKelas, prodi, tahun);

        // 4. Panggil DAO
        boolean isSuccess = kelasDAO.addKelas(newKelas);

        // 5. Tampilkan pesan berdasarkan hasil
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Data kelas berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadDataToTable();
            EventBus.notifyDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah data. Kemungkinan nama kelas sudah ada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void txtCariNamaKelasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariNamaKelasActionPerformed
        filterTable();
    }//GEN-LAST:event_txtCariNamaKelasActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // 1. Ambil baris yang sedang di highlight
        int selectedRow = tblKelas.getSelectedRow();

        // 2. Validasi
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data kelas yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Ambil data kelas dari list
        KelasModel selectedKelas = listKelasData.get(selectedRow);
        int idKelas = selectedKelas.getIdKelas();
        String namaKelas = selectedKelas.getNamaKelas();

        // 4. Tampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus kelas '" + namaKelas + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // 5. Confirmation
        if (confirm == JOptionPane.YES_OPTION) {
            kelasDAO.deleteKelas(idKelas);

            JOptionPane.showMessageDialog(this, "Data kelas berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // 6. Refresh tampilan
            clearForm();
            loadDataToTable();
            EventBus.notifyDataChanged();

        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBersihActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersih;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbProdi;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblProdi;
    private javax.swing.JPanel panelKelas;
    private javax.swing.JTable tblKelas;
    private javax.swing.JTextField txtCariNamaKelas;
    private javax.swing.JTextField txtNamaKelas;
    private javax.swing.JTextField txtTahunProdi;
    // End of variables declaration//GEN-END:variables
}
