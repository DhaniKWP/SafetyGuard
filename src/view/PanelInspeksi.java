package view;

import dao.InspeksiKeselamatanDAO;
import dao.KaryawanDAO;
import model.InspeksiKeselamatan;
import model.Karyawan;
import model.Pengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelInspeksi extends PanelBase {

    private static final Color ACCENT = new Color(16, 185, 129);

    private Pengguna loggedInUser;
    private DefaultTableModel modelInspeksi;
    private List<InspeksiKeselamatan> listInspeksi;
    private List<Karyawan> listKaryawan;

    public PanelInspeksi(Pengguna user) {
        super(ACCENT,
              "Inspeksi Keselamatan",
              "Hasil inspeksi rutin area kerja beserta skor kepatuhan HSE");
        this.loggedInUser = user;
        initTableModel();
        initFilterBar();
        initActionBar();
        fetchDataInspeksi();
    }

    private void initTableModel() {
        modelInspeksi = new DefaultTableModel(new String[]{
            "No", "Tanggal", "Area Kerja", "Nama Inspektur", "Temuan Bahaya", "Skor", "Rekomendasi"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        mainTable.setModel(modelInspeksi);
        int[] widths = {40, 100, 130, 160, 200, 60, 200};
        for (int i = 0; i < widths.length; i++) mainTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        mainTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && mainTable.getSelectedRow() != -1) showFormEditInspeksi();
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelInspeksi);
        mainTable.setRowSorter(sorter);

        mainTable.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!sel) {
                    try {
                        int skor = Integer.parseInt(v.toString());
                        if (skor == 100)     { lbl.setForeground(new Color(52, 211, 153)); lbl.setBackground(new Color(16, 185, 129, 30)); }
                        else if (skor >= 60) { lbl.setForeground(new Color(251, 191, 36)); lbl.setBackground(new Color(245, 158, 11, 30)); }
                        else                 { lbl.setForeground(new Color(252, 165, 165)); lbl.setBackground(new Color(239, 68, 68, 30)); }
                        lbl.setOpaque(true);
                    } catch (NumberFormatException ex) { /* biarkan default */ }
                }
                return lbl;
            }
        });
    }

    private void initFilterBar() {
        JTextField txtSearch = styledSearch("Cari area kerja, inspektur...");
        JComboBox<String> cbSkor = styledCombo(new String[]{"Semua Skor", "Aman (Skor 100)", "Ada Temuan (Skor < 100)"});

        filterBar.add(filterLabel("Cari:"));
        filterBar.add(txtSearch);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(filterLabel("Skor:"));
        filterBar.add(cbSkor);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
            (javax.swing.table.TableRowSorter<DefaultTableModel>) mainTable.getRowSorter();

        Runnable applyFilter = () -> {
            String txt = txtSearch.getText().trim();
            String sk  = cbSkor.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>();
            if (!txt.isEmpty()) filters.add(javax.swing.RowFilter.regexFilter("(?i)" + txt));
            if (!sk.equals("Semua Skor")) {
                if (sk.equals("Aman (Skor 100)"))
                    filters.add(javax.swing.RowFilter.regexFilter("^100$", 5));
                else
                    filters.add(javax.swing.RowFilter.notFilter(javax.swing.RowFilter.regexFilter("^100$", 5)));
            }
            sorter.setRowFilter(filters.isEmpty() ? null : javax.swing.RowFilter.andFilter(filters));
        };
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        cbSkor.addActionListener(e -> applyFilter.run());
    }

    private void initActionBar() {
        JButton btnRefresh = actionButton("Refresh", new Color(51, 65, 85), new Color(30, 41, 59));
        JButton btnTambah  = actionButton("Tambah Inspeksi", ACCENT_BLUE, ACCENT_CYAN);
        JButton btnHapus   = actionButton("Hapus Terpilih", ACCENT_RED, new Color(185, 28, 28));

        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapus.setEnabled(false);
            btnHapus.setToolTipText("Hanya Admin yang dapat menghapus data.");
        }

        actionBar.add(btnRefresh);
        actionBar.add(btnTambah);
        actionBar.add(btnHapus);

        btnRefresh.addActionListener(e -> fetchDataInspeksi());
        btnTambah.addActionListener(e -> showFormTambahInspeksi());
        btnHapus.addActionListener(e -> hapusInspeksiTerpilih());
    }

    private void fetchDataInspeksi() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) listKaryawan = new KaryawanDAO().getAll();
            listInspeksi = new InspeksiKeselamatanDAO().getAll();
            modelInspeksi.setRowCount(0);
            if (listInspeksi != null) {
                int no = 1;
                for (InspeksiKeselamatan ins : listInspeksi) {
                    String inspektur = ins.getIdInspektur();
                    if (listKaryawan != null) for (Karyawan k : listKaryawan)
                        if (ins.getIdInspektur() != null && ins.getIdInspektur().equals(k.getIdKaryawan())) {
                            inspektur = k.getNamaLengkap() + " (" + k.getJabatan() + ")"; break;
                        }
                    modelInspeksi.addRow(new Object[]{
                        no++, ins.getTanggalInspeksi(), ins.getAreaInspeksi(), inspektur,
                        ins.getTemuanBahaya(), ins.getSkorKepatuhan(), ins.getRekomendasi()
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { setCursor(Cursor.getDefaultCursor()); }
    }

    private void showFormTambahInspeksi() {
        JTextField txtTanggal = new JTextField(); txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout());
        pTanggal.add(txtTanggal, BorderLayout.CENTER);
        pTanggal.add(btnPilih, BorderLayout.EAST);

        JTextField txtArea = new JTextField();
        JCheckBox chk1 = new JCheckBox("Jalur evakuasi bebas hambatan");
        JCheckBox chk2 = new JCheckBox("Alat Pemadam Api (APAR) tersedia dan layak");
        JCheckBox chk3 = new JCheckBox("Pekerja menggunakan APD standar");
        JCheckBox chk4 = new JCheckBox("Peralatan mesin memiliki pelindung");
        JCheckBox chk5 = new JCheckBox("Panel listrik tertutup dan aman");

        JPanel checklist = new JPanel();
        checklist.setOpaque(false);
        checklist.setLayout(new BoxLayout(checklist, BoxLayout.Y_AXIS));
        for (JCheckBox chk : new JCheckBox[]{chk1,chk2,chk3,chk4,chk5}) {
            chk.setOpaque(false);
            chk.setForeground(TEXT_WHITE);
            checklist.add(chk);
        }

        JTextArea txtRekom = new JTextArea(3, 20);
        txtRekom.setLineWrap(true);
        txtRekom.setWrapStyleWord(true);
        
        JPanel formPanel = createGridForm(
            new String[]{"Tanggal Inspeksi", "Area Inspeksi", "Checklist Keselamatan", "Rekomendasi"},
            new JComponent[]{pTanggal, txtArea, checklist, new JScrollPane(txtRekom)}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Tambah Inspeksi Keselamatan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            int skor = 0;
            StringBuilder temuan = new StringBuilder();
            if (chk1.isSelected()) skor += 20; else temuan.append("- Jalur evakuasi terhambat.\n");
            if (chk2.isSelected()) skor += 20; else temuan.append("- APAR tidak tersedia/tidak layak.\n");
            if (chk3.isSelected()) skor += 20; else temuan.append("- Pekerja tidak menggunakan APD.\n");
            if (chk4.isSelected()) skor += 20; else temuan.append("- Pelindung mesin tidak lengkap.\n");
            if (chk5.isSelected()) skor += 20; else temuan.append("- Panel listrik terbuka/berbahaya.\n");
            if (skor == 100) temuan.append("Tidak ada temuan bahaya kritis. Area aman.");

            InspeksiKeselamatan ins = new InspeksiKeselamatan();
            ins.setTanggalInspeksi(txtTanggal.getText().trim());
            ins.setAreaInspeksi(txtArea.getText().trim());
            ins.setIdInspektur(loggedInUser.getIdKaryawan());
            ins.setTemuanBahaya(temuan.toString().trim());
            ins.setSkorKepatuhan(skor);
            ins.setRekomendasi(txtRekom.getText().trim());

            if (new InspeksiKeselamatanDAO().insert(ins)) {
                JOptionPane.showMessageDialog(this, "Inspeksi berhasil dicatat!");
                fetchDataInspeksi();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditInspeksi() {
        int sel = mainTable.getSelectedRow();
        if (sel < 0) return;
        int modelRow = mainTable.convertRowIndexToModel(sel);
        InspeksiKeselamatan ins = listInspeksi.get(modelRow);

        JTextField txtTanggal = new JTextField(ins.getTanggalInspeksi()); txtTanggal.setEditable(false);
        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> {
            Window pw = SwingUtilities.getWindowAncestor(this);
            String p = new helper.DatePicker(pw instanceof JFrame ? (JFrame)pw : null).setPickedDate();
            if (p != null && !p.isEmpty()) txtTanggal.setText(p);
        });
        JPanel pTanggal = new JPanel(new BorderLayout());
        pTanggal.add(txtTanggal, BorderLayout.CENTER); pTanggal.add(btnPilih, BorderLayout.EAST);

        JTextField txtArea = new JTextField(ins.getAreaInspeksi());
        String temuanLama = ins.getTemuanBahaya() == null ? "" : ins.getTemuanBahaya();
        JCheckBox chk1 = new JCheckBox("Jalur evakuasi bebas hambatan", !temuanLama.contains("Jalur evakuasi terhambat"));
        JCheckBox chk2 = new JCheckBox("Alat Pemadam Api (APAR) tersedia dan layak", !temuanLama.contains("APAR tidak tersedia"));
        JCheckBox chk3 = new JCheckBox("Pekerja menggunakan APD standar", !temuanLama.contains("Pekerja tidak menggunakan APD"));
        JCheckBox chk4 = new JCheckBox("Peralatan mesin memiliki pelindung", !temuanLama.contains("Pelindung mesin tidak lengkap"));
        JCheckBox chk5 = new JCheckBox("Panel listrik tertutup dan aman", !temuanLama.contains("Panel listrik terbuka"));

        JPanel checklist = new JPanel();
        checklist.setOpaque(false);
        checklist.setLayout(new BoxLayout(checklist, BoxLayout.Y_AXIS));
        for (JCheckBox chk : new JCheckBox[]{chk1,chk2,chk3,chk4,chk5}) {
            chk.setOpaque(false);
            chk.setForeground(TEXT_WHITE);
            checklist.add(chk);
        }

        JTextArea txtRekom = new JTextArea(ins.getRekomendasi(), 3, 20);
        txtRekom.setLineWrap(true);
        txtRekom.setWrapStyleWord(true);

        JPanel formPanel = createGridForm(
            new String[]{"Tanggal Inspeksi", "Area Inspeksi", "Checklist Keselamatan", "Rekomendasi"},
            new JComponent[]{pTanggal, txtArea, checklist, new JScrollPane(txtRekom)}
        );

        int opt = JOptionPane.showConfirmDialog(this, formPanel, "Edit Inspeksi Keselamatan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            int skor = 0;
            StringBuilder temuanBaru = new StringBuilder();
            if (chk1.isSelected()) skor += 20; else temuanBaru.append("- Jalur evakuasi terhambat.\n");
            if (chk2.isSelected()) skor += 20; else temuanBaru.append("- APAR tidak tersedia/tidak layak.\n");
            if (chk3.isSelected()) skor += 20; else temuanBaru.append("- Pekerja tidak menggunakan APD.\n");
            if (chk4.isSelected()) skor += 20; else temuanBaru.append("- Pelindung mesin tidak lengkap.\n");
            if (chk5.isSelected()) skor += 20; else temuanBaru.append("- Panel listrik terbuka/berbahaya.\n");
            if (skor == 100) temuanBaru.append("Tidak ada temuan bahaya kritis. Area aman.");

            ins.setTanggalInspeksi(txtTanggal.getText().trim());
            ins.setAreaInspeksi(txtArea.getText().trim());
            ins.setTemuanBahaya(temuanBaru.toString().trim());
            ins.setSkorKepatuhan(skor);
            ins.setRekomendasi(txtRekom.getText().trim());

            if (new InspeksiKeselamatanDAO().update(ins)) {
                JOptionPane.showMessageDialog(this, "Data Inspeksi berhasil diperbarui!");
                fetchDataInspeksi();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusInspeksiTerpilih() {
        int[] rows = mainTable.getSelectedRows();
        if (rows.length == 0) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus " + rows.length + " data Inspeksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            InspeksiKeselamatanDAO dao = new InspeksiKeselamatanDAO();
            boolean ok = true;
            for (int i = rows.length - 1; i >= 0; i--)
                if (!dao.delete(listInspeksi.get(mainTable.convertRowIndexToModel(rows[i])).getIdInspeksi())) ok = false;
            fetchDataInspeksi();
            JOptionPane.showMessageDialog(this, ok ? "Data berhasil dihapus." : "Beberapa data gagal dihapus.");
        }
    }
}
