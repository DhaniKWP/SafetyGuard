package view;

import dao.TindakanPerbaikanDAO;
import dao.KaryawanDAO;
import dao.LaporanInsidenDAO;
import model.TindakanPerbaikan;
import model.Karyawan;
import model.LaporanInsiden;
import model.Pengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelCapa extends JPanel {
    private Pengguna loggedInUser;
    private JTable tblCapa;
    private DefaultTableModel modelCapa;
    private JButton btnFetchCapa, btnTambahCapa;
    private List<TindakanPerbaikan> listCapa;
    private List<Karyawan> listKaryawan;
    private List<LaporanInsiden> listInsiden;

    public PanelCapa(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        initUI();
        fetchDataCapa();
    }

    private void initUI() {
        modelCapa = new DefaultTableModel(new String[]{
            "No", "Ref. Insiden", "Detail Tindakan", "Penanggung Jawab", "Deadline", "Tanggal Selesai", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCapa = new JTable(modelCapa);
        tblCapa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tblCapa.getSelectedRow() != -1) {
                    showFormEditCapa();
                }
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterCapa = new javax.swing.table.TableRowSorter<>(modelCapa);
        tblCapa.setRowSorter(sorterCapa);

        JPanel filterPanelCapa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanelCapa.add(new JLabel("Cari:"));
        JTextField txtSearchCapa = new JTextField(15);
        filterPanelCapa.add(txtSearchCapa);
        filterPanelCapa.add(new JLabel(" Status:"));
        JComboBox<String> cbFilterStatusCapa = new JComboBox<>(new String[]{
            "Semua", "Belum Mulai", "Dalam Proses", "Selesai", "Batal"
        });
        filterPanelCapa.add(cbFilterStatusCapa);
        add(filterPanelCapa, BorderLayout.NORTH);

        Runnable filterCapa = () -> {
            String text = txtSearchCapa.getText().trim();
            String status = cbFilterStatusCapa.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>(2);
            if (!text.isEmpty()) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
            }
            if (!status.equals("Semua")) {
                filters.add(javax.swing.RowFilter.regexFilter("^" + status + "$", 6)); // 6 is index of Status column
            }
            sorterCapa.setRowFilter(javax.swing.RowFilter.andFilter(filters));
        };

        txtSearchCapa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterCapa.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterCapa.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterCapa.run(); }
        });
        cbFilterStatusCapa.addActionListener(e -> filterCapa.run());

        add(new JScrollPane(tblCapa), BorderLayout.CENTER);

        JPanel btnPanelCapa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetchCapa = new JButton("Refresh Data");
        btnTambahCapa = new JButton("Tambah Tindakan CAPA");
        JButton btnHapusCapa = new JButton("Hapus Terpilih");
        btnPanelCapa.add(btnFetchCapa);
        btnPanelCapa.add(btnTambahCapa);
        btnPanelCapa.add(btnHapusCapa);

        if (loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            btnTambahCapa.setEnabled(false);
            btnTambahCapa.setToolTipText("Hanya Staff HSE/Admin yang bisa menambah CAPA.");
        }
        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapusCapa.setEnabled(false);
            btnHapusCapa.setToolTipText("Hanya Admin yang memiliki hak untuk menghapus data CAPA.");
        }

        add(btnPanelCapa, BorderLayout.SOUTH);
        btnFetchCapa.addActionListener(e -> fetchDataCapa());
        btnTambahCapa.addActionListener(e -> showFormTambahCapa());
        btnHapusCapa.addActionListener(e -> hapusCapaTerpilih());
    }

    private void fetchDataCapa() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) {
                listKaryawan = new KaryawanDAO().getAll();
            }
            if (listInsiden == null) {
                listInsiden = new LaporanInsidenDAO().getAll();
            }

            TindakanPerbaikanDAO dao = new TindakanPerbaikanDAO();
            listCapa = dao.getAll();
            modelCapa.setRowCount(0);
            if (listCapa != null && !listCapa.isEmpty()) {
                int no = 1;
                for (TindakanPerbaikan capa : listCapa) {

                    String infoInsiden = capa.getIdInsiden();
                    if (listInsiden != null && capa.getIdInsiden() != null) {
                        for (LaporanInsiden ins : listInsiden) {
                            if (capa.getIdInsiden().equals(ins.getIdInsiden())) {
                                infoInsiden = "[" + ins.getTanggalKejadian() + "] " + ins.getKategoriInsiden() + " - " + ins.getLokasiKejadian();
                                break;
                            }
                        }
                    }

                    String infoPj = capa.getIdPenanggungJawab();
                    if (listKaryawan != null && capa.getIdPenanggungJawab() != null) {
                        for (Karyawan k : listKaryawan) {
                            if (capa.getIdPenanggungJawab().equals(k.getIdKaryawan())) {
                                infoPj = k.getNamaLengkap() + " (" + k.getJabatan() + " - " + k.getDepartemen() + ")";
                                break;
                            }
                        }
                    }

                    modelCapa.addRow(new Object[]{
                        no++,
                        infoInsiden,
                        capa.getTindakanDetail(),
                        infoPj,
                        capa.getDeadline(),
                        capa.getTanggalSelesai() == null ? "-" : capa.getTanggalSelesai(),
                        capa.getStatusTindakan()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showFormTambahCapa() {
        if (listInsiden == null) listInsiden = new LaporanInsidenDAO().getAll();
        JComboBox<String> cbInsiden = new JComboBox<>();
        if (listInsiden != null) {
            for (LaporanInsiden ins : listInsiden) {
                String display = ins.getTanggalKejadian() + " | " + ins.getLokasiKejadian() + " (" + ins.getKategoriInsiden() + ")";
                cbInsiden.addItem(display);
            }
        }

        if (this.listKaryawan == null) {
            this.listKaryawan = new KaryawanDAO().getAll();
        }
        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (this.listKaryawan != null) {
            for (Karyawan k : this.listKaryawan) {
                cbKaryawan.addItem(k);
            }
        }

        JTextArea txtDetail = new JTextArea(4, 20);

        JPanel panelDeadline = new JPanel(new BorderLayout());
        JTextField txtDeadline = new JTextField();
        txtDeadline.setEditable(false);
        JButton btnPilihDeadline = new JButton("Pilih Tanggal");
        btnPilihDeadline.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtDeadline.setText(picked);
            }
        });
        panelDeadline.add(txtDeadline, BorderLayout.CENTER);
        panelDeadline.add(btnPilihDeadline, BorderLayout.EAST);

        Object[] message = {
            "Pilih Insiden (Referensi):", cbInsiden,
            "Detail Tindakan:", new JScrollPane(txtDetail),
            "Karyawan Penanggung Jawab:", cbKaryawan,
            "Deadline:", panelDeadline
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Tambah Tindakan CAPA", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            TindakanPerbaikan capa = new TindakanPerbaikan();

            int selectedIndex = cbInsiden.getSelectedIndex();
            if (selectedIndex >= 0 && listInsiden != null) {
                capa.setIdInsiden(listInsiden.get(selectedIndex).getIdInsiden());
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih Insiden referensi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            capa.setTindakanDetail(txtDetail.getText().trim());

            Karyawan selectedKaryawan = (Karyawan) cbKaryawan.getSelectedItem();
            if (selectedKaryawan != null) {
                capa.setIdPenanggungJawab(selectedKaryawan.getIdKaryawan());
            }

            capa.setDeadline(txtDeadline.getText().trim());
            capa.setStatusTindakan("Belum Mulai");

            TindakanPerbaikanDAO dao = new TindakanPerbaikanDAO();
            if (dao.insert(capa)) {
                JOptionPane.showMessageDialog(this, "Tindakan CAPA berhasil ditambahkan!");
                fetchDataCapa();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data CAPA.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditCapa() {
        int selectedRow = tblCapa.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris CAPA yang ingin diedit.");
            return;
        }

        int modelRow = tblCapa.convertRowIndexToModel(selectedRow);
        TindakanPerbaikan capa = listCapa.get(modelRow);

        JTextArea txtDetail = new JTextArea(4, 20);
        txtDetail.setText(capa.getTindakanDetail());

        if (this.listKaryawan == null) {
            this.listKaryawan = new KaryawanDAO().getAll();
        }
        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        int indexKaryawan = -1;
        if (this.listKaryawan != null) {
            for (int i = 0; i < listKaryawan.size(); i++) {
                Karyawan k = listKaryawan.get(i);
                cbKaryawan.addItem(k);
                if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) {
                    indexKaryawan = i;
                }
            }
        }
        if (indexKaryawan != -1) cbKaryawan.setSelectedIndex(indexKaryawan);

        JPanel panelDeadline = new JPanel(new BorderLayout());
        JTextField txtDeadline = new JTextField(capa.getDeadline() != null ? capa.getDeadline() : "");
        txtDeadline.setEditable(false);
        JButton btnPilihDeadline = new JButton("Pilih Tanggal");
        btnPilihDeadline.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtDeadline.setText(picked);
            }
        });
        panelDeadline.add(txtDeadline, BorderLayout.CENTER);
        panelDeadline.add(btnPilihDeadline, BorderLayout.EAST);

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Belum Mulai", "Dalam Proses", "Selesai", "Batal"});
        cbStatus.setSelectedItem(capa.getStatusTindakan());

        Object[] message = {
            "Detail Tindakan:", new JScrollPane(txtDetail),
            "Penanggung Jawab:", cbKaryawan,
            "Deadline:", panelDeadline,
            "Status Tindakan:", cbStatus
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Tindakan CAPA", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            capa.setTindakanDetail(txtDetail.getText().trim());

            Karyawan selectedKaryawan = (Karyawan) cbKaryawan.getSelectedItem();
            if (selectedKaryawan != null) {
                capa.setIdPenanggungJawab(selectedKaryawan.getIdKaryawan());
            }

            capa.setDeadline(txtDeadline.getText().trim());
            capa.setStatusTindakan((String) cbStatus.getSelectedItem());

            if (capa.getStatusTindakan().equals("Selesai")) {
                capa.setTanggalSelesai(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            } else {
                capa.setTanggalSelesai(null);
            }

            TindakanPerbaikanDAO dao = new TindakanPerbaikanDAO();
            if (dao.update(capa)) {
                JOptionPane.showMessageDialog(this, "Data CAPA berhasil diperbarui!");
                fetchDataCapa();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusCapaTerpilih() {
        int[] selectedRows = tblCapa.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu data CAPA untuk dihapus!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus " + selectedRows.length + " data CAPA terpilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            TindakanPerbaikanDAO dao = new TindakanPerbaikanDAO();
            boolean success = true;
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = tblCapa.convertRowIndexToModel(selectedRows[i]);
                String id = listCapa.get(row).getIdTindakan();
                if (!dao.delete(id)) {
                    success = false;
                }
            }
            fetchDataCapa();
            if (success) {
                JOptionPane.showMessageDialog(this, "Data CAPA berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus beberapa data CAPA.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
