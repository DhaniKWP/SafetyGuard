package view;

import dao.LaporanInsidenDAO;
import model.LaporanInsiden;
import model.Pengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelInsiden extends JPanel {
    private Pengguna loggedInUser;
    private JTable tblInsiden;
    private DefaultTableModel modelInsiden;
    private JButton btnFetchInsiden, btnTambahInsiden, btnHapusInsiden;
    private List<LaporanInsiden> listInsiden;

    public PanelInsiden(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        initUI();
        fetchDataInsiden();
    }

    private void initUI() {
        modelInsiden = new DefaultTableModel(new String[]{
            "No", "Tanggal", "Lokasi", "Kategori", "Deskripsi", "Status Investigasi"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInsiden = new JTable(modelInsiden);
        tblInsiden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tblInsiden.getSelectedRow() != -1) {
                    showFormEditInsiden();
                }
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterInsiden = new javax.swing.table.TableRowSorter<>(modelInsiden);
        tblInsiden.setRowSorter(sorterInsiden);

        JPanel filterPanelInsiden = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanelInsiden.add(new JLabel("Cari:"));
        JTextField txtSearchInsiden = new JTextField(15);
        filterPanelInsiden.add(txtSearchInsiden);
        filterPanelInsiden.add(new JLabel(" Kategori:"));
        JComboBox<String> cbFilterKategoriInsiden = new JComboBox<>(new String[]{
            "Semua", "Near Miss", "Minor Injury", "Major Injury", "Property Damage"
        });
        filterPanelInsiden.add(cbFilterKategoriInsiden);
        add(filterPanelInsiden, BorderLayout.NORTH);

        Runnable filterInsiden = () -> {
            String text = txtSearchInsiden.getText().trim();
            String kategori = cbFilterKategoriInsiden.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>(2);
            if (!text.isEmpty()) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
            }
            if (!kategori.equals("Semua")) {
                filters.add(javax.swing.RowFilter.regexFilter("^" + kategori + "$", 3)); // 3 is index of Kategori
            }
            sorterInsiden.setRowFilter(javax.swing.RowFilter.andFilter(filters));
        };

        txtSearchInsiden.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterInsiden.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterInsiden.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterInsiden.run(); }
        });
        cbFilterKategoriInsiden.addActionListener(e -> filterInsiden.run());

        JScrollPane scrollPane = new JScrollPane(tblInsiden);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetchInsiden = new JButton("Refresh Data");
        btnTambahInsiden = new JButton("Tambah Insiden Baru");
        btnHapusInsiden = new JButton("Hapus Terpilih");
        buttonPanel.add(btnFetchInsiden);
        buttonPanel.add(btnTambahInsiden);
        buttonPanel.add(btnHapusInsiden);
        add(buttonPanel, BorderLayout.SOUTH);

        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapusInsiden.setEnabled(false);
            btnHapusInsiden.setToolTipText("Hanya Admin yang memiliki hak untuk menghapus data.");
        }

        btnFetchInsiden.addActionListener(e -> fetchDataInsiden());
        btnTambahInsiden.addActionListener(e -> showFormTambahInsiden());
        btnHapusInsiden.addActionListener(e -> hapusInsidenTerpilih());
    }

    private void fetchDataInsiden() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            listInsiden = dao.getAll();
            modelInsiden.setRowCount(0);
            if (listInsiden != null && !listInsiden.isEmpty()) {
                int noUrut = 1;
                for (LaporanInsiden ins : listInsiden) {
                    modelInsiden.addRow(new Object[]{
                        noUrut++,
                        ins.getTanggalKejadian(),
                        ins.getLokasiKejadian(),
                        ins.getKategoriInsiden(),
                        ins.getDeskripsiKejadian(),
                        ins.getStatusInvestigasi()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showFormTambahInsiden() {
        JTextField txtLokasi = new JTextField();
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{
            "Near Miss", "Minor Injury", "Major Injury", "Property Damage"
        });
        JTextArea txtDeskripsi = new JTextArea(4, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDeskripsi);
        JTextField txtTanggal = new JTextField("2026-06-16");
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih...");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (!picked.isEmpty()) txtTanggal.setText(picked);
        });
        JPanel panelTanggal = new JPanel(new BorderLayout());
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        Object[] message = {
            "Tanggal Kejadian:", panelTanggal,
            "Lokasi Kejadian:", txtLokasi,
            "Kategori Insiden:", cbKategori,
            "Deskripsi Kejadian:", scrollDesc
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Tambah Laporan Insiden", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            LaporanInsiden insiden = new LaporanInsiden();
            insiden.setTanggalKejadian(txtTanggal.getText().trim());
            insiden.setLokasiKejadian(txtLokasi.getText().trim());
            insiden.setKategoriInsiden((String) cbKategori.getSelectedItem());
            insiden.setDeskripsiKejadian(txtDeskripsi.getText().trim());
            insiden.setIdPelapor(loggedInUser.getIdKaryawan());
            insiden.setStatusInvestigasi("Baru");
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            if (dao.insert(insiden)) {
                JOptionPane.showMessageDialog(this, "Insiden berhasil dilaporkan!");
                fetchDataInsiden();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditInsiden() {
        int selectedRow = tblInsiden.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris insiden yang ingin diedit.");
            return;
        }

        int modelRow = tblInsiden.convertRowIndexToModel(selectedRow);
        LaporanInsiden insiden = listInsiden.get(modelRow);

        JTextField txtLokasi = new JTextField(insiden.getLokasiKejadian());
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{
            "Near Miss", "Minor Injury", "Major Injury", "Property Damage"
        });
        cbKategori.setSelectedItem(insiden.getKategoriInsiden());

        JTextArea txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setText(insiden.getDeskripsiKejadian());

        JTextField txtTanggal = new JTextField(insiden.getTanggalKejadian());
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih...");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (!picked.isEmpty()) txtTanggal.setText(picked);
        });
        JPanel panelTanggal = new JPanel(new BorderLayout());
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Baru", "Sedang Diinvestigasi", "Selesai"});
        cbStatus.setSelectedItem(insiden.getStatusInvestigasi());

        Object[] message = {
            "Tanggal Kejadian:", panelTanggal,
            "Lokasi Kejadian:", txtLokasi,
            "Kategori Insiden:", cbKategori,
            "Deskripsi Kejadian:", new JScrollPane(txtDeskripsi),
            "Status Investigasi:", cbStatus
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Laporan Insiden", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            insiden.setTanggalKejadian(txtTanggal.getText().trim());
            insiden.setLokasiKejadian(txtLokasi.getText().trim());
            insiden.setKategoriInsiden((String) cbKategori.getSelectedItem());
            insiden.setDeskripsiKejadian(txtDeskripsi.getText().trim());
            insiden.setStatusInvestigasi((String) cbStatus.getSelectedItem());

            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            if (dao.update(insiden)) {
                JOptionPane.showMessageDialog(this, "Data Insiden berhasil diperbarui!");
                fetchDataInsiden();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusInsidenTerpilih() {
        int[] selectedRows = tblInsiden.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu data insiden untuk dihapus!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus " + selectedRows.length + " data insiden terpilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            boolean success = true;
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = tblInsiden.convertRowIndexToModel(selectedRows[i]);
                String id = listInsiden.get(row).getIdInsiden();
                if (!dao.delete(id)) {
                    success = false;
                }
            }
            fetchDataInsiden();
            if (success) {
                JOptionPane.showMessageDialog(this, "Data Insiden berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus beberapa data Insiden.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
