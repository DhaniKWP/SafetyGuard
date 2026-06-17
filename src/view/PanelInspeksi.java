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

public class PanelInspeksi extends JPanel {
    private Pengguna loggedInUser;
    private JTable tblInspeksi;
    private DefaultTableModel modelInspeksi;
    private JButton btnFetchInspeksi, btnTambahInspeksi;
    private List<InspeksiKeselamatan> listInspeksi;
    private List<Karyawan> listKaryawan;

    public PanelInspeksi(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        initUI();
        fetchDataInspeksi();
    }

    private void initUI() {
        modelInspeksi = new DefaultTableModel(new String[]{
            "No", "Tanggal", "Area Kerja", "Nama Inspektur", "Temuan Bahaya", "Skor", "Rekomendasi"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInspeksi = new JTable(modelInspeksi);
        tblInspeksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tblInspeksi.getSelectedRow() != -1) {
                    showFormEditInspeksi();
                }
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterInspeksi = new javax.swing.table.TableRowSorter<>(modelInspeksi);
        tblInspeksi.setRowSorter(sorterInspeksi);

        JPanel filterPanelInspeksi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanelInspeksi.add(new JLabel("Cari:"));
        JTextField txtSearchInspeksi = new JTextField(15);
        filterPanelInspeksi.add(txtSearchInspeksi);
        filterPanelInspeksi.add(new JLabel(" Skor:"));
        JComboBox<String> cbFilterSkorInspeksi = new JComboBox<>(new String[]{
            "Semua", "Aman (Skor 100)", "Ada Temuan (Skor < 100)"
        });
        filterPanelInspeksi.add(cbFilterSkorInspeksi);
        add(filterPanelInspeksi, BorderLayout.NORTH);

        Runnable filterInspeksi = () -> {
            String text = txtSearchInspeksi.getText().trim();
            String skorFilter = cbFilterSkorInspeksi.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>(2);
            if (!text.isEmpty()) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
            }
            if (!skorFilter.equals("Semua")) {
                if (skorFilter.equals("Aman (Skor 100)")) {
                    filters.add(javax.swing.RowFilter.regexFilter("^100$", 5)); // 5 is index of Skor
                } else {
                    filters.add(javax.swing.RowFilter.notFilter(javax.swing.RowFilter.regexFilter("^100$", 5)));
                }
            }
            sorterInspeksi.setRowFilter(javax.swing.RowFilter.andFilter(filters));
        };

        txtSearchInspeksi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterInspeksi.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterInspeksi.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterInspeksi.run(); }
        });
        cbFilterSkorInspeksi.addActionListener(e -> filterInspeksi.run());

        add(new JScrollPane(tblInspeksi), BorderLayout.CENTER);

        JPanel btnPanelInspeksi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetchInspeksi = new JButton("Refresh Data");
        btnTambahInspeksi = new JButton("Tambah Inspeksi");
        JButton btnHapusInspeksi = new JButton("Hapus Terpilih");
        btnPanelInspeksi.add(btnFetchInspeksi);
        btnPanelInspeksi.add(btnTambahInspeksi);
        btnPanelInspeksi.add(btnHapusInspeksi);

        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapusInspeksi.setEnabled(false);
            btnHapusInspeksi.setToolTipText("Hanya Admin yang bisa menghapus data Inspeksi.");
        }

        add(btnPanelInspeksi, BorderLayout.SOUTH);
        btnFetchInspeksi.addActionListener(e -> fetchDataInspeksi());
        btnTambahInspeksi.addActionListener(e -> showFormTambahInspeksi());
        btnHapusInspeksi.addActionListener(e -> hapusInspeksiTerpilih());
    }

    private void fetchDataInspeksi() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) {
                listKaryawan = new KaryawanDAO().getAll();
            }

            InspeksiKeselamatanDAO dao = new InspeksiKeselamatanDAO();
            listInspeksi = dao.getAll();
            modelInspeksi.setRowCount(0);
            if (listInspeksi != null && !listInspeksi.isEmpty()) {
                int no = 1;
                for (InspeksiKeselamatan ins : listInspeksi) {

                    String infoInspektur = ins.getIdInspektur();
                    if (listKaryawan != null && ins.getIdInspektur() != null) {
                        for (Karyawan k : listKaryawan) {
                            if (ins.getIdInspektur().equals(k.getIdKaryawan())) {
                                infoInspektur = k.getNamaLengkap() + " (" + k.getJabatan() + " - " + k.getDepartemen() + ")";
                                break;
                            }
                        }
                    }

                    modelInspeksi.addRow(new Object[]{
                        no++,
                        ins.getTanggalInspeksi(),
                        ins.getAreaInspeksi(),
                        infoInspektur,
                        ins.getTemuanBahaya(),
                        ins.getSkorKepatuhan(),
                        ins.getRekomendasi()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showFormTambahInspeksi() {
        JPanel panelTanggal = new JPanel(new BorderLayout());
        JTextField txtTanggal = new JTextField();
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtTanggal.setText(picked);
            }
        });
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        JTextField txtArea = new JTextField();

        JCheckBox chk1 = new JCheckBox("Jalur evakuasi bebas hambatan");
        JCheckBox chk2 = new JCheckBox("Alat Pemadam Api (APAR) tersedia dan layak");
        JCheckBox chk3 = new JCheckBox("Pekerja menggunakan APD standar");
        JCheckBox chk4 = new JCheckBox("Peralatan mesin memiliki pelindung");
        JCheckBox chk5 = new JCheckBox("Panel listrik tertutup dan aman");

        JPanel panelChecklist = new JPanel();
        panelChecklist.setLayout(new javax.swing.BoxLayout(panelChecklist, javax.swing.BoxLayout.Y_AXIS));
        panelChecklist.add(new JLabel("Centang jika kriteria terpenuhi:"));
        panelChecklist.add(chk1);
        panelChecklist.add(chk2);
        panelChecklist.add(chk3);
        panelChecklist.add(chk4);
        panelChecklist.add(chk5);

        JTextArea txtRekomendasi = new JTextArea(3, 20);

        Object[] message = {
            "Tanggal Inspeksi:", panelTanggal,
            "Area Inspeksi:", txtArea,
            "Checklist Keselamatan:", panelChecklist,
            "Rekomendasi Tambahan:", new JScrollPane(txtRekomendasi)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Tambah Inspeksi Keselamatan", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int skor = 0;
            StringBuilder temuan = new StringBuilder();

            if (chk1.isSelected()) skor += 20; else temuan.append("- Jalur evakuasi terhambat.\n");
            if (chk2.isSelected()) skor += 20; else temuan.append("- APAR tidak tersedia/tidak layak.\n");
            if (chk3.isSelected()) skor += 20; else temuan.append("- Pekerja tidak menggunakan APD.\n");
            if (chk4.isSelected()) skor += 20; else temuan.append("- Pelindung mesin tidak lengkap.\n");
            if (chk5.isSelected()) skor += 20; else temuan.append("- Panel listrik terbuka/berbahaya.\n");

            if (skor == 100) {
                temuan.append("Tidak ada temuan bahaya kritis. Area aman.");
            }

            InspeksiKeselamatan ins = new InspeksiKeselamatan();
            ins.setTanggalInspeksi(txtTanggal.getText().trim());
            ins.setAreaInspeksi(txtArea.getText().trim());
            ins.setIdInspektur(loggedInUser.getIdKaryawan());
            ins.setTemuanBahaya(temuan.toString().trim());
            ins.setSkorKepatuhan(skor);
            ins.setRekomendasi(txtRekomendasi.getText().trim());

            InspeksiKeselamatanDAO dao = new InspeksiKeselamatanDAO();
            if (dao.insert(ins)) {
                JOptionPane.showMessageDialog(this, "Inspeksi Keselamatan berhasil dicatat!");
                fetchDataInspeksi();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data inspeksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditInspeksi() {
        int selectedRow = tblInspeksi.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data Inspeksi yang ingin diedit!");
            return;
        }

        int modelRow = tblInspeksi.convertRowIndexToModel(selectedRow);
        InspeksiKeselamatan ins = listInspeksi.get(modelRow);

        JPanel panelTanggal = new JPanel(new BorderLayout());
        JTextField txtTanggal = new JTextField(ins.getTanggalInspeksi());
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtTanggal.setText(picked);
            }
        });
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        JTextField txtArea = new JTextField(ins.getAreaInspeksi());
        JTextArea txtTemuan = new JTextArea(4, 20);
        txtTemuan.setText(ins.getTemuanBahaya());
        JScrollPane scrollTemuan = new JScrollPane(txtTemuan);

        JTextField txtSkor = new JTextField(String.valueOf(ins.getSkorKepatuhan()));
        JTextArea txtRekomendasi = new JTextArea(3, 20);
        txtRekomendasi.setText(ins.getRekomendasi());

        Object[] message = {
            "Tanggal Inspeksi:", panelTanggal,
            "Area Inspeksi:", txtArea,
            "Temuan Bahaya:", scrollTemuan,
            "Skor Kepatuhan (0-100):", txtSkor,
            "Rekomendasi Tambahan:", new JScrollPane(txtRekomendasi)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Inspeksi Keselamatan", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            ins.setTanggalInspeksi(txtTanggal.getText().trim());
            ins.setAreaInspeksi(txtArea.getText().trim());
            ins.setTemuanBahaya(txtTemuan.getText().trim());

            try {
                int skor = Integer.parseInt(txtSkor.getText().trim());
                if (skor < 0 || skor > 100) {
                    JOptionPane.showMessageDialog(this, "Skor harus di antara 0 - 100!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ins.setSkorKepatuhan(skor);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Skor harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ins.setRekomendasi(txtRekomendasi.getText().trim());

            InspeksiKeselamatanDAO dao = new InspeksiKeselamatanDAO();
            if (dao.update(ins)) {
                JOptionPane.showMessageDialog(this, "Data Inspeksi berhasil diupdate!");
                fetchDataInspeksi();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data inspeksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusInspeksiTerpilih() {
        int[] selectedRows = tblInspeksi.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu data Inspeksi untuk dihapus!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus " + selectedRows.length + " data Inspeksi terpilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            InspeksiKeselamatanDAO dao = new InspeksiKeselamatanDAO();
            boolean success = true;
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = tblInspeksi.convertRowIndexToModel(selectedRows[i]);
                String id = listInspeksi.get(row).getIdInspeksi();
                if (!dao.delete(id)) {
                    success = false;
                }
            }
            fetchDataInspeksi();
            if (success) {
                JOptionPane.showMessageDialog(this, "Data Inspeksi berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus beberapa data Inspeksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
