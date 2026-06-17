package view;

import dao.DistribusiAPDDAO;
import dao.KaryawanDAO;
import model.DistribusiAPD;
import model.Karyawan;
import model.Pengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelApd extends PanelBase {

    private static final Color ACCENT = new Color(168, 85, 247); // Ungu

    private Pengguna loggedInUser;
    private DefaultTableModel modelApd;
    private List<DistribusiAPD> listApd;
    private List<Karyawan> listKaryawan;

    public PanelApd(Pengguna user) {
        super(ACCENT,
              "Distribusi APD",
              "Rekap penyaluran Alat Pelindung Diri kepada seluruh karyawan");
        this.loggedInUser = user;
        initTableModel();
        initFilterBar();
        initActionBar();
        fetchDataApd();
    }

    private void initTableModel() {
        modelApd = new DefaultTableModel(new String[]{
            "No", "Nama Penerima", "Jenis APD", "Tanggal Distribusi", "Jumlah", "Kondisi APD"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        mainTable.setModel(modelApd);
        int[] widths = {40, 200, 160, 120, 70, 100};
        for (int i = 0; i < widths.length; i++) mainTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        mainTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && mainTable.getSelectedRow() != -1) showFormEditApd();
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelApd);
        mainTable.setRowSorter(sorter);

        // Renderer kondisi APD
        mainTable.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setText("  " + (v == null ? "" : v.toString()) + "  ");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    switch (val) {
                        case "Baru"        -> { lbl.setBackground(new Color(16, 185, 129, 40)); lbl.setForeground(new Color(52, 211, 153)); }
                        case "Bekas Layak" -> { lbl.setBackground(new Color(245, 158, 11, 40)); lbl.setForeground(new Color(251, 191, 36)); }
                        case "Rusak"       -> { lbl.setBackground(new Color(239, 68, 68, 40));  lbl.setForeground(new Color(252, 165, 165)); }
                        default            -> { lbl.setBackground(new Color(30, 41, 59)); lbl.setForeground(new Color(148, 163, 184)); }
                    }
                }
                return lbl;
            }
        });
    }

    private void initFilterBar() {
        JTextField txtSearch = styledSearch("Cari nama penerima, jenis APD...");
        JComboBox<String> cbJenis = styledCombo(new String[]{
            "Semua Jenis", "Helm Keselamatan", "Sepatu Safety", "Kacamata Pelindung",
            "Sarung Tangan", "Rompi Reflektif", "Masker/Respirator"
        });
        JComboBox<String> cbKondisi = styledCombo(new String[]{"Semua Kondisi", "Baru", "Bekas Layak", "Rusak"});

        filterBar.add(filterLabel("Cari:"));
        filterBar.add(txtSearch);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(filterLabel("Jenis APD:"));
        filterBar.add(cbJenis);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(filterLabel("Kondisi:"));
        filterBar.add(cbKondisi);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
            (javax.swing.table.TableRowSorter<DefaultTableModel>) mainTable.getRowSorter();

        Runnable applyFilter = () -> {
            String txt    = txtSearch.getText().trim();
            String jenis  = cbJenis.getSelectedItem().toString();
            String kondisi= cbKondisi.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>();
            if (!txt.isEmpty()) filters.add(javax.swing.RowFilter.regexFilter("(?i)" + txt));
            if (!jenis.equals("Semua Jenis")) filters.add(javax.swing.RowFilter.regexFilter("^" + jenis + "$", 2));
            if (!kondisi.equals("Semua Kondisi")) filters.add(javax.swing.RowFilter.regexFilter("^" + kondisi + "$", 5));
            sorter.setRowFilter(filters.isEmpty() ? null : javax.swing.RowFilter.andFilter(filters));
        };
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        cbJenis.addActionListener(e -> applyFilter.run());
        cbKondisi.addActionListener(e -> applyFilter.run());
    }

    private void initActionBar() {
        JButton btnRefresh = actionButton("Refresh", new Color(51, 65, 85), new Color(30, 41, 59));
        JButton btnTambah  = actionButton("Tambah Distribusi", ACCENT_BLUE, ACCENT_CYAN);
        JButton btnHapus   = actionButton("Hapus Terpilih", ACCENT_RED, new Color(185, 28, 28));

        if (loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            btnTambah.setEnabled(false); btnTambah.setToolTipText("Hanya Staff HSE/Admin yang bisa distribusi APD.");
        }
        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapus.setEnabled(false); btnHapus.setToolTipText("Hanya Admin yang dapat menghapus data.");
        }

        actionBar.add(btnRefresh); actionBar.add(btnTambah); actionBar.add(btnHapus);
        btnRefresh.addActionListener(e -> fetchDataApd());
        btnTambah.addActionListener(e -> showFormTambahApd());
        btnHapus.addActionListener(e -> hapusApdTerpilih());
    }

    private void fetchDataApd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();
            listApd = new DistribusiAPDDAO().getAll();
            modelApd.setRowCount(0);
            if (listApd != null) {
                int no = 1;
                for (DistribusiAPD apd : listApd) {
                    String penerima = apd.getIdKaryawan();
                    if (listKaryawan != null) for (Karyawan k : listKaryawan)
                        if (apd.getIdKaryawan() != null && apd.getIdKaryawan().equals(k.getIdKaryawan())) {
                            penerima = k.getNamaLengkap() + " (" + k.getJabatan() + " - " + k.getDepartemen() + ")"; break;
                        }
                    modelApd.addRow(new Object[]{
                        no++, penerima, apd.getJenisApd(), apd.getTanggalPembagian(), apd.getJumlah(), apd.getKondisiApd()
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { setCursor(Cursor.getDefaultCursor()); }
    }

    private void showFormTambahApd() {
        if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();
        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (listKaryawan != null) for (Karyawan k : listKaryawan) cbKaryawan.addItem(k);

        JComboBox<String> cbJenis = new JComboBox<>(new String[]{
            "Helm Keselamatan","Sepatu Safety","Kacamata Pelindung","Sarung Tangan","Rompi Reflektif","Masker/Respirator"
        });
        JTextField txtTanggal = new JTextField(); txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih Tanggal");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout());
        pTanggal.add(txtTanggal, BorderLayout.CENTER); pTanggal.add(btnPilih, BorderLayout.EAST);

        JSpinner spinJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JComboBox<String> cbKondisi = new JComboBox<>(new String[]{"Baru","Bekas Layak","Rusak"});

        JPanel formPanel = createGridForm(
            new String[]{"Karyawan Penerima", "Jenis APD", "Tanggal Distribusi", "Jumlah (pcs)", "Kondisi APD"},
            new JComponent[]{cbKaryawan, cbJenis, pTanggal, spinJumlah, cbKondisi}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Distribusi APD Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            DistribusiAPD apd = new DistribusiAPD();
            Karyawan k = (Karyawan) cbKaryawan.getSelectedItem();
            if (k != null) apd.setIdKaryawan(k.getIdKaryawan());
            apd.setJenisApd((String) cbJenis.getSelectedItem());
            apd.setTanggalPembagian(txtTanggal.getText().trim());
            apd.setJumlah((Integer) spinJumlah.getValue());
            apd.setKondisiApd((String) cbKondisi.getSelectedItem());
            if (new DistribusiAPDDAO().insert(apd)) {
                JOptionPane.showMessageDialog(this, "Distribusi APD berhasil dicatat!");
                fetchDataApd();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditApd() {
        int sel = mainTable.getSelectedRow();
        if (sel < 0) return;
        int modelRow = mainTable.convertRowIndexToModel(sel);
        DistribusiAPD apd = listApd.get(modelRow);
        if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();

        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (listKaryawan != null) for (Karyawan k : listKaryawan) {
            cbKaryawan.addItem(k);
            if (k.getIdKaryawan().equals(apd.getIdKaryawan())) cbKaryawan.setSelectedItem(k);
        }
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{
            "Helm Keselamatan","Sepatu Safety","Kacamata Pelindung","Sarung Tangan","Rompi Reflektif","Masker/Respirator"
        });
        cbJenis.setSelectedItem(apd.getJenisApd());

        JTextField txtTanggal = new JTextField(apd.getTanggalPembagian()); txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout());
        pTanggal.add(txtTanggal, BorderLayout.CENTER); pTanggal.add(btnPilih, BorderLayout.EAST);

        JSpinner spinJumlah = new JSpinner(new SpinnerNumberModel((int)apd.getJumlah(), 1, 100, 1));
        JComboBox<String> cbKondisi = new JComboBox<>(new String[]{"Baru","Bekas Layak","Rusak"});
        cbKondisi.setSelectedItem(apd.getKondisiApd());

        JPanel formPanel = createGridForm(
            new String[]{"Karyawan Penerima", "Jenis APD", "Tanggal Distribusi", "Jumlah (pcs)", "Kondisi APD"},
            new JComponent[]{cbKaryawan, cbJenis, pTanggal, spinJumlah, cbKondisi}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Edit Distribusi APD", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            Karyawan k = (Karyawan) cbKaryawan.getSelectedItem();
            if (k != null) apd.setIdKaryawan(k.getIdKaryawan());
            apd.setJenisApd((String) cbJenis.getSelectedItem());
            apd.setTanggalPembagian(txtTanggal.getText().trim());
            apd.setJumlah((Integer) spinJumlah.getValue());
            apd.setKondisiApd((String) cbKondisi.getSelectedItem());
            if (new DistribusiAPDDAO().update(apd)) {
                JOptionPane.showMessageDialog(this, "Data APD berhasil diperbarui!");
                fetchDataApd();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusApdTerpilih() {
        int[] rows = mainTable.getSelectedRows();
        if (rows.length == 0) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus " + rows.length + " data Distribusi APD?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DistribusiAPDDAO dao = new DistribusiAPDDAO();
            boolean ok = true;
            for (int i = rows.length - 1; i >= 0; i--)
                if (!dao.delete(listApd.get(mainTable.convertRowIndexToModel(rows[i])).getIdDistribusi())) ok = false;
            fetchDataApd();
            JOptionPane.showMessageDialog(this, ok ? "Data berhasil dihapus." : "Beberapa data gagal dihapus.");
        }
    }
}
