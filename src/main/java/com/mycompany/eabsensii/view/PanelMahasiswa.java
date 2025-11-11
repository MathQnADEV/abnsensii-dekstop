/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.eabsensii.view;

import com.mycompany.eabsensii.EventBus;
import com.mycompany.eabsensii.dao.MahasiswaDAO;
import com.mycompany.eabsensii.model.MahasiswaModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author shaqy
 */
public class PanelMahasiswa extends javax.swing.JPanel implements EventBus.DataChangeListener {

    private final MahasiswaDAO mahasiswaDAO;
    private List<MahasiswaModel> listMahasiswaData;
    private boolean isFirstTime = true;
    private List<MahasiswaModel> masterListMahasiswaData;

    public PanelMahasiswa() {
        initComponents();
        mahasiswaDAO = new MahasiswaDAO();
        listMahasiswaData = new ArrayList<>();
        masterListMahasiswaData = new ArrayList<>();
        loadKelasToComboBox();
        loadKelasToFilterComboBox();
        EventBus.subscribe(this);

        tblMahasiswa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedDataToForm();
            }
        });

        cmbFilterKelas.addActionListener(evt -> filterDataByKelas());

        txtCariNamaMahasiswa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterDataByNama();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterDataByNama();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterDataByNama();
            }
        });
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);

        if (aFlag) {
            loadDataToTable();
        }
    }

    private void loadKelasToComboBox() {
        List<String> listKelasFormatted = mahasiswaDAO.getAllKelasFormatted();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(listKelasFormatted.toArray(String[]::new));
        cmbKelas.setModel(model);
    }

    private void loadDataToTable() {
        listMahasiswaData = mahasiswaDAO.getAllMahasiswa();
        masterListMahasiswaData = new ArrayList<>(listMahasiswaData);
        refreshTable();
    }

    private void clearForm() {
        txtNamaMahasiswa.setText("");
        txtNim.setText("");
        cmbKelas.setSelectedIndex(-1);
    }

    @Override
    public void onDataChanged() {
        loadDataToTable();
        loadKelasToComboBox();
        loadKelasToFilterComboBox();

        txtCariNamaMahasiswa.setText("");
        cmbFilterKelas.setSelectedIndex(0);
    }

    private void loadSelectedDataToForm() {
        int selectedRow = tblMahasiswa.getSelectedRow();
        if (selectedRow != -1) {
            MahasiswaModel selectedMhs = listMahasiswaData.get(selectedRow);

            txtNamaMahasiswa.setText(selectedMhs.getNamaMahasiswa());
            txtNim.setText(selectedMhs.getNim());

            cmbKelas.setSelectedItem(selectedMhs.getFormattedKelas());
        }
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) tblMahasiswa.getModel();
        model.setRowCount(0);

        int no = 1;
        for (MahasiswaModel mhs : listMahasiswaData) {
            Object[] rowData = {
                no++ + ".",
                mhs.getNamaMahasiswa(),
                mhs.getNim(),
                mhs.getFormattedKelas()
            };
            model.addRow(rowData);
        }
    }

    private void filterDataByKelas() {
        String selectedKelas = cmbFilterKelas.getSelectedItem().toString();

        if ("Semua Kelas".equals(selectedKelas)) {
            loadDataToTable();
        } else {
            int idKelas = mahasiswaDAO.getKelasIdByFormattedString(selectedKelas);
            if (idKelas != -1) {
                listMahasiswaData = mahasiswaDAO.getMahasiswaByKelas(idKelas);
                masterListMahasiswaData = new ArrayList<>(listMahasiswaData);
            }
        }
        filterDataByNama();
    }

    private void loadKelasToFilterComboBox() {
        List<String> listKelasFormatted = mahasiswaDAO.getAllKelasFormatted();

        listKelasFormatted.add(0, "Semua Kelas");

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(listKelasFormatted.toArray(String[]::new));
        cmbFilterKelas.setModel(model);

        cmbFilterKelas.setSelectedIndex(0);
    }

    private void filterDataByNama() {
        // 1. Ambil teks pencarian
        String searchText = txtCariNamaMahasiswa.getText().trim().toLowerCase();

        // 2. Jika kotak pencarian kosong, tampilkan semua data dari master list
        if (searchText.isEmpty()) {
            listMahasiswaData = new ArrayList<>(masterListMahasiswaData);
        } else {
            // 3. Jika ada teks, buat list baru untuk hasil yang terfilter
            List<MahasiswaModel> filteredList = new ArrayList<>();
            // 4. Iterasi
            for (MahasiswaModel mhs : masterListMahasiswaData) {
                // 5. Periksa apakah nama mahasiswa mengandung (case-insensitive)
                if (mhs.getNamaMahasiswa().toLowerCase().contains(searchText)) {
                    filteredList.add(mhs);
                }
            }
            // 6. Ganti list data dengan list hasil filter
            listMahasiswaData = filteredList;
        }

        // 7. Perbarui tampilan tabel
        refreshTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMahasiswa = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblNamaMahasiswa = new javax.swing.JLabel();
        txtNamaMahasiswa = new javax.swing.JTextField();
        lblNim = new javax.swing.JLabel();
        txtNim = new javax.swing.JTextField();
        lblKelas = new javax.swing.JLabel();
        cmbKelas = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        lblCariNamaMahasiswa = new javax.swing.JLabel();
        txtCariNamaMahasiswa = new javax.swing.JTextField();
        lblFilterKelas = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMahasiswa = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        cmbFilterKelas = new javax.swing.JComboBox<>();

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Manajemen Data Mahasiswa");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblNamaMahasiswa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNamaMahasiswa.setText("Nama Mahasiswa:");

        lblNim.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNim.setText("NIM: ");

        lblKelas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKelas.setText("Kelas:");

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

        lblCariNamaMahasiswa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCariNamaMahasiswa.setText("Cari Nama Mahasiswa:");

        lblFilterKelas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFilterKelas.setText("Filter Kelas: ");

        tblMahasiswa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nama Mahasiswa", "NIM", "Kelas"
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
        jScrollPane1.setViewportView(tblMahasiswa);
        if (tblMahasiswa.getColumnModel().getColumnCount() > 0) {
            tblMahasiswa.getColumnModel().getColumn(0).setMinWidth(40);
            tblMahasiswa.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblMahasiswa.getColumnModel().getColumn(0).setMaxWidth(40);
            tblMahasiswa.getColumnModel().getColumn(1).setMinWidth(300);
            tblMahasiswa.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblMahasiswa.getColumnModel().getColumn(1).setMaxWidth(300);
        }

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout panelMahasiswaLayout = new javax.swing.GroupLayout(panelMahasiswa);
        panelMahasiswa.setLayout(panelMahasiswaLayout);
        panelMahasiswaLayout.setHorizontalGroup(
            panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMahasiswaLayout.createSequentialGroup()
                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMahasiswaLayout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(jLabel5))
                    .addGroup(panelMahasiswaLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMahasiswaLayout.createSequentialGroup()
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMahasiswaLayout.createSequentialGroup()
                                        .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblNamaMahasiswa)
                                            .addComponent(lblNim)
                                            .addComponent(lblKelas))
                                        .addGap(18, 18, 18)
                                        .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtNim, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbKelas, 0, 255, Short.MAX_VALUE)
                                            .addComponent(txtNamaMahasiswa, javax.swing.GroupLayout.Alignment.LEADING)))
                                    .addGroup(panelMahasiswaLayout.createSequentialGroup()
                                        .addComponent(btnTambah)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnUbah)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnHapus)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnBersihkan)))
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCariNamaMahasiswa)
                                    .addComponent(lblFilterKelas))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCariNamaMahasiswa)
                                    .addComponent(cmbFilterKelas, 0, 165, Short.MAX_VALUE))))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        panelMahasiswaLayout.setVerticalGroup(
            panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMahasiswaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMahasiswaLayout.createSequentialGroup()
                        .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMahasiswaLayout.createSequentialGroup()
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblNamaMahasiswa)
                                    .addComponent(txtNamaMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblNim)
                                    .addComponent(txtNim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblKelas)
                                    .addComponent(cmbKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMahasiswaLayout.createSequentialGroup()
                                .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblFilterKelas)
                                    .addComponent(cmbFilterKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)))
                        .addGroup(panelMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambah)
                            .addComponent(btnUbah)
                            .addComponent(btnHapus)
                            .addComponent(btnBersihkan)
                            .addComponent(lblCariNamaMahasiswa)
                            .addComponent(txtCariNamaMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 812, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // 1. validasi
        String namaMhs = txtNamaMahasiswa.getText().trim();
        String nim = txtNim.getText().trim();
        String selectedKelas = cmbKelas.getSelectedItem().toString();

        if (namaMhs.isEmpty() || nim.isEmpty() || selectedKelas == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Dapatkan id_kelas dari combo box
        int idKelas = mahasiswaDAO.getKelasIdByFormattedString(selectedKelas);
        if (idKelas == -1) {
            JOptionPane.showMessageDialog(this, "Kelas yang dipilih tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Buat objek model
        MahasiswaModel newMhs = new MahasiswaModel(nim, namaMhs, idKelas);

        // 4. Panggil DAO untuk menambah data
        boolean isSuccess = mahasiswaDAO.addMahasiswa(newMhs);

        // 5. Tampilkan pesan dan refresh
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Data mahasiswa berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadDataToTable();
            EventBus.notifyDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah data. Kemungkinan NIM sudah terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        // 1. Ambil baris yang di highlight tabel
        int selectedRow = tblMahasiswa.getSelectedRow();

        // 2. Validasi
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data mahasiswa yang ingin diubah terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Ambil data dari form
        String namaMhs = txtNamaMahasiswa.getText().trim();
        String nim = txtNim.getText().trim();
        Object selectedKelasObj = cmbKelas.getSelectedItem();

        // 4. Validasi input form
        if (namaMhs.isEmpty() || nim.isEmpty() || selectedKelasObj == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 5. Dapatkan id_kelas dari combo box
        String selectedKelas = selectedKelasObj.toString();
        int idKelas = mahasiswaDAO.getKelasIdByFormattedString(selectedKelas);
        if (idKelas == -1) {
            JOptionPane.showMessageDialog(this, "Kelas yang dipilih tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6. Ambil ID mahasiswa asli dari list
        MahasiswaModel originalMhs = listMahasiswaData.get(selectedRow);
        int idMahasiswa = originalMhs.getIdMahasiswa();

        // 7. Buat objek model dengan data baru
        MahasiswaModel updatedMhs = new MahasiswaModel();
        updatedMhs.setIdMahasiswa(idMahasiswa);
        updatedMhs.setNim(nim);
        updatedMhs.setNamaMahasiswa(namaMhs);
        updatedMhs.setIdKelas(idKelas);

        // 8. Tampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin mengubah data mahasiswa ini?",
                "Konfirmasi Ubah",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // 9. Confirmation
        if (confirm == JOptionPane.YES_OPTION) {
            boolean isSuccess = mahasiswaDAO.updateMahasiswa(updatedMhs);

            // 10. Tampilkan pesan dan refresh
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Data mahasiswa berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadDataToTable();
                EventBus.notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data. Kemungkinan NIM sudah digunakan mahasiswa lain.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // 1. Ambil baris yang di Highlight di tabel
        int selectedRow = tblMahasiswa.getSelectedRow();

        // 2. Validasi
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data mahasiswa yang ingin dihapus terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Ambil data mahasiswa dari list yang dipilih
        MahasiswaModel selectedMhs = listMahasiswaData.get(selectedRow);
        int idMahasiswa = selectedMhs.getIdMahasiswa();
        String namaMhs = selectedMhs.getNamaMahasiswa();
        String nimMhs = selectedMhs.getNim();

        // 4. Tampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus mahasiswa berikut?\n"
                + "Nama: " + namaMhs + "\n"
                + "NIM: " + nimMhs + "\n\n"
                + "Tindakan ini tidak dapat dibatalkan!",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // 5. Confirmation
        if (confirm == JOptionPane.YES_OPTION) {
            boolean isSuccess = mahasiswaDAO.deleteMahasiswa(idMahasiswa);

            if (isSuccess) {
                JOptionPane.showMessageDialog(this,
                        "Data mahasiswa berhasil dihapus.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // 6. Refresh tampilan
                clearForm();
                loadDataToTable();

                EventBus.notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data mahasiswa. Silakan coba lagi.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBersihkanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbFilterKelas;
    private javax.swing.JComboBox<String> cmbKelas;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCariNamaMahasiswa;
    private javax.swing.JLabel lblFilterKelas;
    private javax.swing.JLabel lblKelas;
    private javax.swing.JLabel lblNamaMahasiswa;
    private javax.swing.JLabel lblNim;
    private javax.swing.JPanel panelMahasiswa;
    private javax.swing.JTable tblMahasiswa;
    private javax.swing.JTextField txtCariNamaMahasiswa;
    private javax.swing.JTextField txtNamaMahasiswa;
    private javax.swing.JTextField txtNim;
    // End of variables declaration//GEN-END:variables
}
