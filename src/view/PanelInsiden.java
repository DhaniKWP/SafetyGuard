package view;

import dao.LaporanInsidenDAO;
import model.LaporanInsiden;
import model.Pengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelInsiden extends PanelBase {

    private static final Color ACCENT = new Color(239, 68, 68); // Merah

    private Pengguna loggedInUser;
    private DefaultTableModel modelInsiden;
    private List<LaporanInsiden> listInsiden;

    public PanelInsiden(Pengguna user) {
        super(ACCENT,
              "Laporan Insiden K3",
              "Data kejadian insiden keselamatan kerja di lingkungan pabrik");
        this.loggedInUser = user;
        initTableModel();
        initFilterBar();
        initActionBar();
        fetchDataInsiden();
    }

    private void initTableModel() {
        modelInsiden = new DefaultTableModel(new String[]{
            "No", "Tanggal Kejadian", "Lokasi", "Kategori", "Deskripsi", "Status Investigasi"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        mainTable.setModel(modelInsiden);

        // Lebar kolom
        int[] widths = {45, 110, 130, 120, 260, 130};
        for (int i = 0; i < widths.length; i++) {
            mainTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Double-click untuk edit
        mainTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && mainTable.getSelectedRow() != -1) showFormEditInsiden();
            }
        });

        // Row sorter
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelInsiden);
        mainTable.setRowSorter(sorter);

        // Custom renderer untuk kolom Status dengan warna
        mainTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
    }

    private void initFilterBar() {
        JTextField txtSearch = styledSearch("Cari lokasi, deskripsi...");
        JComboBox<String> cbKategori = styledCombo(new String[]{
            "Semua Kategori", "Near Miss", "Minor Injury", "Major Injury", "Property Damage"
        });

        filterBar.add(filterLabel("Cari:"));
        filterBar.add(txtSearch);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(filterLabel("Kategori:"));
        filterBar.add(cbKategori);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
            (javax.swing.table.TableRowSorter<DefaultTableModel>) mainTable.getRowSorter();

        Runnable applyFilter = () -> {
            String txt = txtSearch.getText().trim();
            String kat = cbKategori.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
            if (!txt.isEmpty()) filters.add(javax.swing.RowFilter.regexFilter("(?i)" + txt));
            if (!kat.equals("Semua Kategori")) filters.add(javax.swing.RowFilter.regexFilter("^" + kat + "$", 3));
            sorter.setRowFilter(filters.isEmpty() ? null : javax.swing.RowFilter.andFilter(filters));
        };

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        cbKategori.addActionListener(e -> applyFilter.run());
    }

    private void initActionBar() {
        JButton btnRefresh = actionButton("Refresh", new Color(51, 65, 85), new Color(30, 41, 59));
        JButton btnTambah  = actionButton("Tambah Insiden", ACCENT_BLUE, ACCENT_CYAN);
        JButton btnHapus   = actionButton("Hapus Terpilih", ACCENT_RED, new Color(185, 28, 28));

        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapus.setEnabled(false);
            btnHapus.setToolTipText("Hanya Admin yang dapat menghapus data.");
        }

        actionBar.add(btnRefresh);
        actionBar.add(btnTambah);
        actionBar.add(btnHapus);

        btnRefresh.addActionListener(e -> fetchDataInsiden());
        btnTambah.addActionListener(e -> showFormTambahInsiden());
        btnHapus.addActionListener(e -> hapusInsidenTerpilih());
    }

    private void fetchDataInsiden() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            listInsiden = new LaporanInsidenDAO().getAll();
            modelInsiden.setRowCount(0);
            if (listInsiden != null) {
                int no = 1;
                for (LaporanInsiden ins : listInsiden) {
                    modelInsiden.addRow(new Object[]{
                        no++, ins.getTanggalKejadian(), ins.getLokasiKejadian(),
                        ins.getKategoriInsiden(), ins.getDeskripsiKejadian(), ins.getStatusInvestigasi()
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
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{"Near Miss","Minor Injury","Major Injury","Property Damage"});
        JTextArea txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        JTextField txtTanggal = new JTextField(java.time.LocalDate.now().toString());
        txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih...");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout(6, 0));
        pTanggal.setOpaque(false);
        pTanggal.add(txtTanggal, BorderLayout.CENTER);
        pTanggal.add(btnPilih, BorderLayout.EAST);

        JPanel formPanel = createGridForm(
            new String[]{"Tanggal Kejadian", "Lokasi", "Kategori Insiden", "Deskripsi Detail"},
            new JComponent[]{pTanggal, txtLokasi, cbKategori, new JScrollPane(txtDeskripsi)}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Tambah Laporan Insiden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            LaporanInsiden ins = new LaporanInsiden();
            ins.setTanggalKejadian(txtTanggal.getText().trim());
            ins.setLokasiKejadian(txtLokasi.getText().trim());
            ins.setKategoriInsiden((String) cbKategori.getSelectedItem());
            ins.setDeskripsiKejadian(txtDeskripsi.getText().trim());
            ins.setIdPelapor(loggedInUser.getIdKaryawan());
            ins.setStatusInvestigasi("Baru");
            if (new LaporanInsidenDAO().insert(ins)) {
                JOptionPane.showMessageDialog(this, "Insiden berhasil dilaporkan!");
                fetchDataInsiden();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditInsiden() {
        int selRow = mainTable.getSelectedRow();
        if (selRow == -1) return;
        int modelRow = mainTable.convertRowIndexToModel(selRow);
        LaporanInsiden ins = listInsiden.get(modelRow);

        JTextField txtLokasi = new JTextField(ins.getLokasiKejadian());
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{"Near Miss","Minor Injury","Major Injury","Property Damage"});
        cbKategori.setSelectedItem(ins.getKategoriInsiden());
        JTextArea txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setText(ins.getDeskripsiKejadian());
        JTextField txtTanggal = new JTextField(ins.getTanggalKejadian());
        txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih...");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout(6, 0));
        pTanggal.setOpaque(false);
        pTanggal.add(txtTanggal, BorderLayout.CENTER);
        pTanggal.add(btnPilih, BorderLayout.EAST);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Baru","Sedang Diinvestigasi","Selesai"});
        cbStatus.setSelectedItem(ins.getStatusInvestigasi());

        JPanel formPanel = createGridForm(
            new String[]{"Tanggal Kejadian", "Lokasi", "Kategori Insiden", "Deskripsi Detail", "Status Investigasi"},
            new JComponent[]{pTanggal, txtLokasi, cbKategori, new JScrollPane(txtDeskripsi), cbStatus}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Edit Laporan Insiden", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            ins.setTanggalKejadian(txtTanggal.getText().trim());
            ins.setLokasiKejadian(txtLokasi.getText().trim());
            ins.setKategoriInsiden((String) cbKategori.getSelectedItem());
            ins.setDeskripsiKejadian(txtDeskripsi.getText().trim());
            ins.setStatusInvestigasi((String) cbStatus.getSelectedItem());
            if (new LaporanInsidenDAO().update(ins)) {
                JOptionPane.showMessageDialog(this, "Data Insiden berhasil diperbarui!");
                fetchDataInsiden();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void hapusInsidenTerpilih() {
        int[] rows = mainTable.getSelectedRows();
        if (rows.length == 0) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus " + rows.length + " data insiden?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            boolean ok = true;
            for (int i = rows.length - 1; i >= 0; i--) {
                if (!dao.delete(listInsiden.get(mainTable.convertRowIndexToModel(rows[i])).getIdInsiden())) ok = false;
            }
            fetchDataInsiden();
            JOptionPane.showMessageDialog(this, ok ? "Data berhasil dihapus." : "Beberapa data gagal dihapus.");
        }
    }

    // Renderer badge warna untuk kolom Status
    static class StatusBadgeRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String val = v == null ? "" : v.toString();
            lbl.setText("  " + val + "  ");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setOpaque(true);
            if (!sel) {
                switch (val) {
                    case "Selesai"              -> { lbl.setBackground(new Color(16, 185, 129, 40)); lbl.setForeground(new Color(52, 211, 153)); }
                    case "Sedang Diinvestigasi" -> { lbl.setBackground(new Color(245, 158, 11, 40)); lbl.setForeground(new Color(251, 191, 36)); }
                    default                     -> { lbl.setBackground(new Color(239, 68, 68, 40));  lbl.setForeground(new Color(252, 165, 165)); }
                }
            }
            return lbl;
        }
    }
}
