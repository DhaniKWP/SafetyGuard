package view;

import dao.KaryawanDAO;
import dao.LaporanInsidenDAO;
import dao.TindakanPerbaikanDAO;
import model.Karyawan;
import model.LaporanInsiden;
import model.Pengguna;
import model.TindakanPerbaikan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelCapa extends PanelBase {

    private static final Color ACCENT = new Color(245, 158, 11); // Oranye

    private Pengguna loggedInUser;
    private DefaultTableModel modelCapa;
    private List<TindakanPerbaikan> listCapa;
    private List<Karyawan>          listKaryawan;
    private List<LaporanInsiden>    listInsiden;

    public PanelCapa(Pengguna user) {
        super(ACCENT,
              "Tindakan CAPA",
              "Corrective Action & Preventive Action — tindak lanjut insiden keselamatan");
        this.loggedInUser = user;
        initTableModel();
        initFilterBar();
        initActionBar();
        fetchDataCapa();
    }

    private void initTableModel() {
        modelCapa = new DefaultTableModel(new String[]{
            "No", "Ref. Insiden", "Detail Tindakan", "Penanggung Jawab", "Deadline", "Tgl Selesai", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        mainTable.setModel(modelCapa);
        int[] widths = {40, 160, 220, 160, 90, 90, 100};
        for (int i = 0; i < widths.length; i++) mainTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        mainTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && mainTable.getSelectedRow() != -1) showFormEditCapa();
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelCapa);
        mainTable.setRowSorter(sorter);
        mainTable.getColumnModel().getColumn(6).setCellRenderer(new PanelInsiden.StatusBadgeRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    switch (val) {
                        case "Selesai"       -> { lbl.setBackground(new Color(16, 185, 129, 40)); lbl.setForeground(new Color(52, 211, 153)); }
                        case "Dalam Proses"  -> { lbl.setBackground(new Color(59, 130, 246, 40)); lbl.setForeground(new Color(147, 197, 253)); }
                        case "Batal"         -> { lbl.setBackground(new Color(239, 68, 68, 40));  lbl.setForeground(new Color(252, 165, 165)); }
                        default              -> { lbl.setBackground(new Color(245, 158, 11, 40)); lbl.setForeground(new Color(251, 191, 36)); }
                    }
                }
                return lbl;
            }
        });
    }

    private void initFilterBar() {
        JTextField txtSearch = styledSearch("Cari tindakan, penanggung jawab...");
        JComboBox<String> cbStatus = styledCombo(new String[]{"Semua Status", "Belum Mulai", "Dalam Proses", "Selesai", "Batal"});

        filterBar.add(filterLabel("Cari:"));
        filterBar.add(txtSearch);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(filterLabel("Status:"));
        filterBar.add(cbStatus);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
            (javax.swing.table.TableRowSorter<DefaultTableModel>) mainTable.getRowSorter();

        Runnable applyFilter = () -> {
            String txt = txtSearch.getText().trim();
            String st  = cbStatus.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>();
            if (!txt.isEmpty()) filters.add(javax.swing.RowFilter.regexFilter("(?i)" + txt));
            if (!st.equals("Semua Status")) filters.add(javax.swing.RowFilter.regexFilter("^" + st + "$", 6));
            sorter.setRowFilter(filters.isEmpty() ? null : javax.swing.RowFilter.andFilter(filters));
        };
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        cbStatus.addActionListener(e -> applyFilter.run());
    }

    private void initActionBar() {
        JButton btnRefresh = actionButton("Refresh", new Color(51, 65, 85), new Color(30, 41, 59));
        JButton btnTambah  = actionButton("Tambah CAPA", ACCENT_BLUE, ACCENT_CYAN);
        JButton btnHapus   = actionButton("Hapus Terpilih", ACCENT_RED, new Color(185, 28, 28));

        if (loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            btnTambah.setEnabled(false);
            btnTambah.setToolTipText("Hanya Staff HSE/Admin yang bisa menambah CAPA.");
        }
        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapus.setEnabled(false);
            btnHapus.setToolTipText("Hanya Admin yang dapat menghapus data.");
        }

        actionBar.add(btnRefresh);
        actionBar.add(btnTambah);
        actionBar.add(btnHapus);

        btnRefresh.addActionListener(e -> fetchDataCapa());
        btnTambah.addActionListener(e -> showFormTambahCapa());
        btnHapus.addActionListener(e -> hapusCapaTerpilih());
    }

    private void fetchDataCapa() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();
            if (listInsiden  == null) listInsiden  = new LaporanInsidenDAO().getAll();
            listCapa = new TindakanPerbaikanDAO().getAll();
            modelCapa.setRowCount(0);
            if (listCapa != null) {
                int no = 1;
                for (TindakanPerbaikan capa : listCapa) {
                    String refInsiden = capa.getIdInsiden();
                    if (listInsiden != null) for (LaporanInsiden ins : listInsiden)
                        if (capa.getIdInsiden() != null && capa.getIdInsiden().equals(ins.getIdInsiden())) {
                            refInsiden = "[" + ins.getTanggalKejadian() + "] " + ins.getKategoriInsiden() + " - " + ins.getLokasiKejadian(); break;
                        }
                    String pj = capa.getIdPenanggungJawab();
                    if (listKaryawan != null) for (Karyawan k : listKaryawan)
                        if (capa.getIdPenanggungJawab() != null && capa.getIdPenanggungJawab().equals(k.getIdKaryawan())) {
                            pj = k.getNamaLengkap() + " (" + k.getJabatan() + ")"; break;
                        }
                    modelCapa.addRow(new Object[]{
                        no++, refInsiden, capa.getTindakanDetail(), pj,
                        capa.getDeadline(), capa.getTanggalSelesai() == null ? "-" : capa.getTanggalSelesai(),
                        capa.getStatusTindakan()
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { setCursor(Cursor.getDefaultCursor()); }
    }

    private void showFormTambahCapa() {
        if (listInsiden  == null) listInsiden  = new LaporanInsidenDAO().getAll();
        if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();

        JComboBox<String> cbInsiden = new JComboBox<>();
        if (listInsiden != null) for (LaporanInsiden ins : listInsiden)
            cbInsiden.addItem(ins.getTanggalKejadian() + " | " + ins.getLokasiKejadian() + " (" + ins.getKategoriInsiden() + ")");

        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (listKaryawan != null) for (Karyawan k : listKaryawan) cbKaryawan.addItem(k);

        JTextArea txtDetail = new JTextArea(4, 20);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        JTextField txtDeadline = new JTextField(); txtDeadline.setEditable(false);
        JButton btnPilih = new JButton("Pilih Tanggal");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtDeadline.setText(p);
        });
        JPanel pDeadline = new JPanel(new BorderLayout(6, 0));
        pDeadline.setOpaque(false);
        pDeadline.add(txtDeadline, BorderLayout.CENTER);
        pDeadline.add(btnPilih, BorderLayout.EAST);

        JPanel formPanel = createGridForm(
            new String[]{"Insiden Referensi", "Detail Tindakan", "Penanggung Jawab", "Deadline Penyelesaian"},
            new JComponent[]{cbInsiden, new JScrollPane(txtDetail), cbKaryawan, pDeadline}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Tambah Tindakan CAPA", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            TindakanPerbaikan capa = new TindakanPerbaikan();
            int idx = cbInsiden.getSelectedIndex();
            if (idx >= 0 && listInsiden != null) capa.setIdInsiden(listInsiden.get(idx).getIdInsiden());
            capa.setTindakanDetail(txtDetail.getText().trim());
            Karyawan k = (Karyawan) cbKaryawan.getSelectedItem();
            if (k != null) capa.setIdPenanggungJawab(k.getIdKaryawan());
            capa.setDeadline(txtDeadline.getText().trim());
            capa.setStatusTindakan("Belum Mulai");
            if (new TindakanPerbaikanDAO().insert(capa)) {
                JOptionPane.showMessageDialog(this, "Tindakan CAPA berhasil ditambahkan!");
                fetchDataCapa();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditCapa() {
        int sel = mainTable.getSelectedRow();
        if (sel < 0) return;
        int modelRow = mainTable.convertRowIndexToModel(sel);
        TindakanPerbaikan capa = listCapa.get(modelRow);
        if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();

        JTextArea txtDetail = new JTextArea(4, 20);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setText(capa.getTindakanDetail());

        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (listKaryawan != null) for (Karyawan k : listKaryawan) {
            cbKaryawan.addItem(k);
            if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) cbKaryawan.setSelectedItem(k);
        }

        JTextField txtDeadline = new JTextField(capa.getDeadline() != null ? capa.getDeadline() : "");
        txtDeadline.setEditable(false);
        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtDeadline.setText(p);
        });
        JPanel pDeadline = new JPanel(new BorderLayout(6, 0));
        pDeadline.setOpaque(false);
        pDeadline.add(txtDeadline, BorderLayout.CENTER);
        pDeadline.add(btnPilih, BorderLayout.EAST);

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Belum Mulai","Dalam Proses","Selesai","Batal"});
        cbStatus.setSelectedItem(capa.getStatusTindakan());

        JPanel formPanel = createGridForm(
            new String[]{"Detail Tindakan", "Penanggung Jawab", "Deadline Penyelesaian", "Status Tindakan"},
            new JComponent[]{new JScrollPane(txtDetail), cbKaryawan, pDeadline, cbStatus}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Edit Tindakan CAPA", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            capa.setTindakanDetail(txtDetail.getText().trim());
            Karyawan k = (Karyawan) cbKaryawan.getSelectedItem();
            if (k != null) capa.setIdPenanggungJawab(k.getIdKaryawan());
            capa.setDeadline(txtDeadline.getText().trim());
            String statusBaru = (String) cbStatus.getSelectedItem();
            capa.setStatusTindakan(statusBaru);
            if ("Selesai".equals(statusBaru) && (capa.getTanggalSelesai() == null || capa.getTanggalSelesai().isBlank()))
                capa.setTanggalSelesai(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            else if (!"Selesai".equals(statusBaru))
                capa.setTanggalSelesai(null);

            if (new TindakanPerbaikanDAO().update(capa)) {
                JOptionPane.showMessageDialog(this, "Data CAPA berhasil diperbarui!");
                fetchDataCapa();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusCapaTerpilih() {
        int[] rows = mainTable.getSelectedRows();
        if (rows.length == 0) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus " + rows.length + " data CAPA?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            TindakanPerbaikanDAO dao = new TindakanPerbaikanDAO();
            boolean ok = true;
            for (int i = rows.length - 1; i >= 0; i--)
                if (!dao.delete(listCapa.get(mainTable.convertRowIndexToModel(rows[i])).getIdTindakan())) ok = false;
            fetchDataCapa();
            JOptionPane.showMessageDialog(this, ok ? "Data berhasil dihapus." : "Beberapa data gagal dihapus.");
        }
    }
}
