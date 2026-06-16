/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import com.formdev.flatlaf.FlatDarkLaf;
import dao.LaporanInsidenDAO;
import model.LaporanInsiden;
import model.Pengguna;
import helper.SupabaseClient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author macbook
 */
public class DashboardFrame extends JFrame {
    private Pengguna loggedInUser;
    private JTabbedPane tabbedPane;
    // Komponen Tab Laporan Insiden
    private JTable tblInsiden;
    private DefaultTableModel modelInsiden;
    private JButton btnFetchInsiden, btnTambahInsiden, btnHapusInsiden;
    private List<LaporanInsiden> listInsiden;
    // Komponen Tab Laporan & Ekspor
    private JButton btnExportPDF, btnExportCSV;
    public DashboardFrame(Pengguna user) {
        this.loggedInUser = user;
        setTitle("SafetyGuard HSE Dashboard - Login sebagai: " + user.getUsername() + " (" + user.getPeran() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Inisialisasi Tabbed Pane
        tabbedPane = new JTabbedPane();
        // 1. Inisialisasi Tab-Tab
        initTabInsiden();
        initTabLainnya(); // Panel placeholder untuk CAPA, Inspeksi, APD
        initTabLaporan();  // Menu Ekspor PDF & CSV
        add(tabbedPane, BorderLayout.CENTER);
        // Footer Status Bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblStatus = new JLabel("Status: Terhubung ke database Supabase | Pengguna Aktif: " + user.getUsername());
        statusPanel.add(lblStatus);
        add(statusPanel, BorderLayout.SOUTH);
        // Fetch data awal untuk tabel insiden
        fetchDataInsiden();
    }
    // ================= TAB 1: LAPORAN INSIDEN =================
    private void initTabInsiden() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabel Model & JTable
        modelInsiden = new DefaultTableModel(new String[]{
            "ID Insiden", "Tanggal", "Lokasi", "Kategori", "Deskripsi", "Status Investigasi"
        }, 0);
        tblInsiden = new JTable(modelInsiden);
        JScrollPane scrollPane = new JScrollPane(tblInsiden);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Panel Tombol Aksi (CRUD)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFetchInsiden = new JButton("Refresh Data");
        btnTambahInsiden = new JButton("Tambah Insiden Baru");
        btnHapusInsiden = new JButton("Hapus Terpilih");
        buttonPanel.add(btnFetchInsiden);
        buttonPanel.add(btnTambahInsiden);
        buttonPanel.add(btnHapusInsiden);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        // Role-based Security: Jika Karyawan, sembunyikan tombol hapus
        if (loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            btnHapusInsiden.setEnabled(false);
            btnHapusInsiden.setToolTipText("Hanya Staff HSE/Admin yang bisa menghapus data.");
        }
        // Action Listener
        btnFetchInsiden.addActionListener(e -> fetchDataInsiden());
        btnTambahInsiden.addActionListener(e -> showFormTambahInsiden());
        btnHapusInsiden.addActionListener(e -> hapusInsidenTerpilih());
        tabbedPane.addTab("Laporan Insiden", panel);
    }
    private void fetchDataInsiden() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            listInsiden = dao.getAll();
            modelInsiden.setRowCount(0); // Bersihkan tabel
            if (listInsiden != null && !listInsiden.isEmpty()) {
                for (LaporanInsiden ins : listInsiden) {
                    modelInsiden.addRow(new Object[]{
                        ins.getIdInsiden(),
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
        // Membuat dialog form input popup sederhana
        JTextField txtLokasi = new JTextField();
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{
            "Near Miss", "Minor Injury", "Major Injury", "Property Damage"
        });
        JTextArea txtDeskripsi = new JTextArea(4, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDeskripsi);
        JTextField txtTanggal = new JTextField("2026-06-16"); // Default tanggal hari ini
        Object[] message = {
            "Tanggal Kejadian (yyyy-mm-dd):", txtTanggal,
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
            insiden.setIdPelapor(loggedInUser.getIdKaryawan()); // Diisi ID Karyawan yang login
            insiden.setStatusInvestigasi("Baru");
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            if (dao.insert(insiden)) {
                JOptionPane.showMessageDialog(this, "Insiden berhasil dilaporkan!");
                fetchDataInsiden(); // Refresh tabel
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void hapusInsidenTerpilih() {
        int selectedRow = tblInsiden.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris data yang ingin dihapus.");
            return;
        }
        String id = (String) modelInsiden.getValueAt(selectedRow, 0);
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus insiden ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            LaporanInsidenDAO dao = new LaporanInsidenDAO();
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                fetchDataInsiden();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // ================= TAB LURUSAN LAINNYA (PLACEHOLDER) =================
    private void initTabLainnya() {
        tabbedPane.addTab("Tindakan CAPA", new JPanel(new FlowLayout()));
        
        // Role-based: Karyawan tidak boleh mengakses audit inspeksi keselamatan
        if (!loggedInUser.getPeran().equalsIgnoreCase("Karyawan")) {
            tabbedPane.addTab("Inspeksi Keselamatan", new JPanel(new FlowLayout()));
        }
        
        tabbedPane.addTab("Distribusi APD", new JPanel(new FlowLayout()));
    }
    // ================= TAB LELAPORAN & EKSPOR (iText & FileWriter) =================
    private void initTabLaporan() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblTitle = new JLabel("Menu Ekspor Laporan Rekapitulasi Keselamatan K3", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);
        btnExportPDF = new JButton("Ekspor Rekap Insiden ke PDF (iText)");
        btnExportPDF.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(btnExportPDF, gbc);
        btnExportCSV = new JButton("Ekspor Rekap Insiden ke CSV");
        btnExportCSV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(btnExportCSV, gbc);
        // Action Listener Ekspor
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnExportCSV.addActionListener(e -> exportToCSV());
        tabbedPane.addTab("Laporan & Rekap", panel);
    }
    // ================= EKSPOR PDF (iText) =================
    private void exportToPDF() {
        if (listInsiden == null || listInsiden.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor. Silakan refresh tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Deteksi otomatis folder Downloads pengguna
            String userHome = System.getProperty("user.home");
            String path = userHome + File.separator + "Downloads" + File.separator + "Rekap_Laporan_Insiden_K3.pdf";
            // Membuat Document iText
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(path));
            document.open();
            // Tambah Judul Laporan
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("LAPORAN REKAPITULASI INSIDEN KESELAMATAN K3 (SAFETYGUARD)\n\n");
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            // Membuat Tabel dengan 5 kolom
            com.itextpdf.text.pdf.PdfPTable tablePdf = new com.itextpdf.text.pdf.PdfPTable(5);
            tablePdf.setWidthPercentage(100);
            // Header Tabel
            tablePdf.addCell("Tanggal");
            tablePdf.addCell("Lokasi");
            tablePdf.addCell("Kategori");
            tablePdf.addCell("Deskripsi");
            tablePdf.addCell("Status");
            // Isi Data
            for (LaporanInsiden ins : listInsiden) {
                tablePdf.addCell(ins.getTanggalKejadian());
                tablePdf.addCell(ins.getLokasiKejadian());
                tablePdf.addCell(ins.getKategoriInsiden());
                tablePdf.addCell(ins.getDeskripsiKejadian());
                tablePdf.addCell(ins.getStatusInvestigasi());
            }
            document.add(tablePdf);
            document.close();
            // Membuka file PDF secara otomatis
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil dibuat di folder Downloads!\nPath: " + path, "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    // ================= EKSPOR CSV (FileWriter) =================
    private void exportToCSV() {
        if (listInsiden == null || listInsiden.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Deteksi otomatis folder Downloads pengguna
            String userHome = System.getProperty("user.home");
            String path = userHome + File.separator + "Downloads" + File.separator + "Rekap_Laporan_Insiden_K3.csv";
            FileWriter writer = new FileWriter(path);
            // Menulis Header CSV
            writer.append("ID Insiden,Tanggal Kejadian,Lokasi Kejadian,Kategori Insiden,Deskripsi Kejadian,Status Investigasi\n");
            // Menulis Baris Data
            for (LaporanInsiden ins : listInsiden) {
                writer.append(ins.getIdInsiden()).append(",")
                      .append(ins.getTanggalKejadian()).append(",")
                      .append(ins.getLokasiKejadian()).append(",")
                      .append(ins.getKategoriInsiden()).append(",")
                      .append(ins.getDeskripsiKejadian().replace(",", " ")).append(",") // menghindari koma memecah kolom
                      .append(ins.getStatusInvestigasi()).append("\n");
            }
            writer.close();
            // Membuka file CSV secara otomatis
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan CSV berhasil dibuat di folder Downloads!\nPath: " + path, "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    public static void main(String[] args) {
        // Kelas main cadangan jika ingin langsung mengetes DashboardFrame tanpa login
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Buat mock user untuk testing
        Pengguna mockUser = new Pengguna("hse_test", "password", "mock-uuid", "Staff HSE");
        SwingUtilities.invokeLater(() -> new DashboardFrame(mockUser).setVisible(true));
    }
}
