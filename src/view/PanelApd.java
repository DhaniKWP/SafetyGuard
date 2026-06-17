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

public class PanelApd extends JPanel {
    private Pengguna loggedInUser;
    private JTable tblApd;
    private DefaultTableModel modelApd;
    private JButton btnFetchApd, btnTambahApd;
    private List<DistribusiAPD> listApd;
    private List<Karyawan> listKaryawan;

    public PanelApd(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        initUI();
        fetchDataApd();
    }

    private void initUI() {
        modelApd = new DefaultTableModel(new String[]{
            "No", "Nama Penerima", "Jenis APD", "Tanggal Distribusi", "Jumlah", "Kondisi APD"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblApd = new JTable(modelApd);
        tblApd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tblApd.getSelectedRow() != -1) {
                    showFormEditApd();
                }
            }
        });

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterApd = new javax.swing.table.TableRowSorter<>(modelApd);
        tblApd.setRowSorter(sorterApd);

        JPanel filterPanelApd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanelApd.add(new JLabel("Cari:"));
        JTextField txtSearchApd = new JTextField(15);
        filterPanelApd.add(txtSearchApd);
        filterPanelApd.add(new JLabel(" Jenis APD:"));
        JComboBox<String> cbFilterJenisApd = new JComboBox<>(new String[]{
            "Semua", "Helm Keselamatan", "Sepatu Safety", "Kacamata Pelindung", "Sarung Tangan", "Rompi Reflektif", "Masker/Respirator"
        });
        filterPanelApd.add(cbFilterJenisApd);
        add(filterPanelApd, BorderLayout.NORTH);

        Runnable filterApd = () -> {
            String text = txtSearchApd.getText().trim();
            String jenis = cbFilterJenisApd.getSelectedItem().toString();
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>(2);
            if (!text.isEmpty()) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
            }
            if (!jenis.equals("Semua")) {
                filters.add(javax.swing.RowFilter.regexFilter("^" + jenis + "$", 2)); // 2 is index of Jenis APD
            }
            sorterApd.setRowFilter(javax.swing.RowFilter.andFilter(filters));
        };

        txtSearchApd.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterApd.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterApd.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterApd.run(); }
        });
        cbFilterJenisApd.addActionListener(e -> filterApd.run());

        add(new JScrollPane(tblApd), BorderLayout.CENTER);

        JPanel btnPanelApd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetchApd = new JButton("Refresh Data");
        btnTambahApd = new JButton("Tambah Distribusi APD");
        JButton btnHapusApd = new JButton("Hapus Terpilih");
        btnPanelApd.add(btnFetchApd);
        btnPanelApd.add(btnTambahApd);
        btnPanelApd.add(btnHapusApd);

        if (loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            btnTambahApd.setEnabled(false);
            btnTambahApd.setToolTipText("Hanya Staff HSE/Admin yang bisa distribusi APD.");
        }
        if (!loggedInUser.getPeran().equalsIgnoreCase("Admin")) {
            btnHapusApd.setEnabled(false);
            btnHapusApd.setToolTipText("Hanya Admin yang bisa menghapus data APD.");
        }

        add(btnPanelApd, BorderLayout.SOUTH);
        btnFetchApd.addActionListener(e -> fetchDataApd());
        btnTambahApd.addActionListener(e -> showFormTambahApd());
        btnHapusApd.addActionListener(e -> hapusApdTerpilih());
    }

    private void fetchDataApd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (listKaryawan == null) {
                listKaryawan = new KaryawanDAO().getAll();
            }

            DistribusiAPDDAO dao = new DistribusiAPDDAO();
            listApd = dao.getAll();
            modelApd.setRowCount(0);
            if (listApd != null && !listApd.isEmpty()) {
                int no = 1;
                for (DistribusiAPD apd : listApd) {

                    String infoPenerima = apd.getIdKaryawan();
                    if (listKaryawan != null && apd.getIdKaryawan() != null) {
                        for (Karyawan k : listKaryawan) {
                            if (apd.getIdKaryawan().equals(k.getIdKaryawan())) {
                                infoPenerima = k.getNamaLengkap() + " (" + k.getJabatan() + " - " + k.getDepartemen() + ")";
                                break;
                            }
                        }
                    }

                    modelApd.addRow(new Object[]{
                        no++,
                        infoPenerima,
                        apd.getJenisApd(),
                        apd.getTanggalPembagian(),
                        apd.getJumlah(),
                        apd.getKondisiApd()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showFormTambahApd() {
        if (this.listKaryawan == null) {
            this.listKaryawan = new KaryawanDAO().getAll();
        }
        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (this.listKaryawan != null) {
            for (Karyawan k : this.listKaryawan) {
                cbKaryawan.addItem(k);
            }
        }

        JComboBox<String> cbJenisApd = new JComboBox<>(new String[]{
            "Helm Keselamatan", "Sepatu Safety", "Kacamata Pelindung", "Sarung Tangan", "Rompi Reflektif", "Masker/Respirator"
        });

        JPanel panelTanggal = new JPanel(new BorderLayout());
        JTextField txtTanggal = new JTextField();
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih Tanggal");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtTanggal.setText(picked);
            }
        });
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        JSpinner spinJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JComboBox<String> cbKondisi = new JComboBox<>(new String[]{"Baru", "Bekas Layak", "Rusak"});

        Object[] message = {
            "Karyawan Penerima:", cbKaryawan,
            "Jenis APD:", cbJenisApd,
            "Tanggal Distribusi:", panelTanggal,
            "Jumlah:", spinJumlah,
            "Kondisi APD:", cbKondisi
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Distribusi APD Baru", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            DistribusiAPD apd = new DistribusiAPD();

            Karyawan selectedK = (Karyawan) cbKaryawan.getSelectedItem();
            if (selectedK != null) {
                apd.setIdKaryawan(selectedK.getIdKaryawan());
            }

            apd.setJenisApd((String) cbJenisApd.getSelectedItem());
            apd.setTanggalPembagian(txtTanggal.getText().trim());
            apd.setJumlah((Integer) spinJumlah.getValue());
            apd.setKondisiApd((String) cbKondisi.getSelectedItem());

            DistribusiAPDDAO dao = new DistribusiAPDDAO();
            if (dao.insert(apd)) {
                JOptionPane.showMessageDialog(this, "Distribusi APD berhasil dicatat!");
                fetchDataApd();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data distribusi APD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusApdTerpilih() {
        int[] selectedRows = tblApd.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu data Distribusi APD untuk dihapus!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus " + selectedRows.length + " data Distribusi APD terpilih?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            DistribusiAPDDAO dao = new DistribusiAPDDAO();
            boolean success = true;
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = tblApd.convertRowIndexToModel(selectedRows[i]);
                String id = listApd.get(row).getIdDistribusi();
                if (!dao.delete(id)) {
                    success = false;
                }
            }
            fetchDataApd();
            if (success) {
                JOptionPane.showMessageDialog(this, "Data Distribusi APD berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus beberapa data Distribusi APD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFormEditApd() {
        int selectedRow = tblApd.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data Distribusi APD yang ingin diedit!");
            return;
        }

        int modelRow = tblApd.convertRowIndexToModel(selectedRow);
        DistribusiAPD apd = listApd.get(modelRow);

        if (this.listKaryawan == null) {
            this.listKaryawan = new KaryawanDAO().getAll();
        }
        JComboBox<Karyawan> cbKaryawan = new JComboBox<>();
        if (this.listKaryawan != null) {
            for (Karyawan k : this.listKaryawan) {
                cbKaryawan.addItem(k);
                if (k.getIdKaryawan().equals(apd.getIdKaryawan())) {
                    cbKaryawan.setSelectedItem(k);
                }
            }
        }

        JComboBox<String> cbJenisApd = new JComboBox<>(new String[]{
            "Helm Keselamatan", "Sepatu Safety", "Kacamata Pelindung", "Sarung Tangan", "Rompi Reflektif", "Masker/Respirator"
        });
        cbJenisApd.setSelectedItem(apd.getJenisApd());

        JPanel panelTanggal = new JPanel(new BorderLayout());
        JTextField txtTanggal = new JTextField(apd.getTanggalPembagian());
        txtTanggal.setEditable(false);
        JButton btnPilihTanggal = new JButton("Pilih Tanggal");
        btnPilihTanggal.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            String picked = new helper.DatePicker(parentWindow instanceof JFrame ? (JFrame)parentWindow : null).setPickedDate();
            if (picked != null && !picked.trim().isEmpty()) {
                txtTanggal.setText(picked);
            }
        });
        panelTanggal.add(txtTanggal, BorderLayout.CENTER);
        panelTanggal.add(btnPilihTanggal, BorderLayout.EAST);

        JSpinner spinJumlah = new JSpinner(new SpinnerNumberModel((int)apd.getJumlah(), 1, 10, 1));
        JComboBox<String> cbKondisi = new JComboBox<>(new String[]{"Baru", "Bekas Layak", "Rusak"});
        cbKondisi.setSelectedItem(apd.getKondisiApd());

        Object[] message = {
            "Karyawan Penerima:", cbKaryawan,
            "Jenis APD:", cbJenisApd,
            "Tanggal Distribusi:", panelTanggal,
            "Jumlah:", spinJumlah,
            "Kondisi APD:", cbKondisi
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Distribusi APD", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Karyawan selectedK = (Karyawan) cbKaryawan.getSelectedItem();
            if (selectedK != null) {
                apd.setIdKaryawan(selectedK.getIdKaryawan());
            }

            apd.setJenisApd((String) cbJenisApd.getSelectedItem());
            apd.setTanggalPembagian(txtTanggal.getText().trim());
            apd.setJumlah((Integer) spinJumlah.getValue());
            apd.setKondisiApd((String) cbKondisi.getSelectedItem());

            DistribusiAPDDAO dao = new DistribusiAPDDAO();
            if (dao.update(apd)) {
                JOptionPane.showMessageDialog(this, "Data Distribusi APD berhasil diupdate!");
                fetchDataApd();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data distribusi APD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
