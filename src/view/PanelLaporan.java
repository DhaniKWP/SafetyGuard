package view;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class PanelLaporan extends JPanel {

    private static final Color BG_MAIN    = new Color(15, 23, 42);
    private static final Color BG_CARD    = new Color(22, 27, 45);
    private static final Color BG_SECTION = new Color(30, 41, 59);
    private static final Color ACCENT_BLUE  = new Color(59, 130, 246);
    private static final Color ACCENT_CYAN  = new Color(34, 211, 238);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_ORANGE= new Color(245, 158, 11);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color TEXT_WHITE  = new Color(248, 250, 252);
    private static final Color TEXT_MUTED  = new Color(148, 163, 184);

    private Pengguna loggedInUser;
    private JComboBox<String> cbJenisLaporan;

    // Kartu modul laporan
    private final String[][] MODUL_DATA = {
        {"Laporan Insiden K3",       "Data insiden & status investigasi",   "Laporan Insiden"},
        {"Tindakan Perbaikan (CAPA)","Tindakan korektif & preventif CAPA",  "Tindakan CAPA"},
        {"Inspeksi Keselamatan",     "Hasil inspeksi area & skor kepatuhan", "Inspeksi Area"},
        {"Distribusi APD",           "Rekap penyaluran Alat Pelindung Diri", "Dist. APD"},
    };

    public PanelLaporan(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        initUI();
    }

    private void initUI() {
        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(17, 24, 39), getWidth(), 0, BG_CARD);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Garis bawah
                g2d.setColor(BORDER_COLOR);
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel lblTitle = new JLabel("Ekspor Laporan Resmi K3");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_WHITE);

        JLabel lblSub = new JLabel("Cetak laporan formal dengan kop surat & tanda tangan untuk arsip perusahaan");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(lblSub);
        headerPanel.add(titleBox, BorderLayout.CENTER);

        // ===== KONTEN UTAMA =====
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // -- Baris 1: Grid Kartu Modul --
        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        gridPanel.setOpaque(false);

        Color[] cardColors = {
            new Color(59, 130, 246),   // Biru - Insiden
            new Color(245, 158, 11),   // Oranye - CAPA
            new Color(16, 185, 129),   // Hijau - Inspeksi
            new Color(168, 85, 247),   // Ungu - APD
        };

        for (int i = 0; i < MODUL_DATA.length; i++) {
            gridPanel.add(createModulCard(MODUL_DATA[i], cardColors[i]));
        }

        // -- Baris 2: Panel Pilihan & Ekspor --
        JPanel actionPanel = createActionPanel();

        contentPanel.add(gridPanel, BorderLayout.NORTH);
        contentPanel.add(actionPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel lblFooter = new JLabel("File akan tersimpan otomatis di folder  Downloads  Anda");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(71, 85, 105));
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createModulCard(String[] data, Color accentColor) {
        // data: [namaModul, deskripsi, labelPendek]
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Garis atas berwarna accent
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                // Border
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setPreferredSize(new Dimension(0, 110));

        JLabel lblNama = new JLabel(data[2]);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNama.setForeground(TEXT_WHITE);

        JLabel lblDesc = new JLabel("<html><body style='width:120px'>" + data[1] + "</body></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(TEXT_MUTED);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.add(lblNama);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(lblDesc);

        card.add(textBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_SECTION);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul bagian
        JLabel lblSectionTitle = new JLabel("Konfigurasi Ekspor");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSectionTitle.setForeground(TEXT_WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblSectionTitle, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        gbc.gridy = 1;
        panel.add(sep, gbc);

        // Baris Label + Dropdown
        JLabel lblPilih = new JLabel("Jenis Laporan:");
        lblPilih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPilih.setForeground(TEXT_MUTED);
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(10, 8, 4, 8);
        panel.add(lblPilih, gbc);

        cbJenisLaporan = new JComboBox<>(new String[]{
            "Laporan Insiden K3",
            "Tindakan Perbaikan (CAPA)",
            "Inspeksi Keselamatan",
            "Distribusi APD"
        });
        cbJenisLaporan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbJenisLaporan.setPreferredSize(new Dimension(260, 38));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(cbJenisLaporan, gbc);

        // Baris Tombol Ekspor
        JLabel lblFormat = new JLabel("Format Output:");
        lblFormat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFormat.setForeground(TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.insets = new Insets(4, 8, 8, 8);
        panel.add(lblFormat, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnPDF = createExportButton("Ekspor PDF Resmi", ACCENT_BLUE, new Color(37, 99, 235));
        JButton btnCSV = createExportButton("Ekspor CSV (Excel)", ACCENT_GREEN, new Color(5, 150, 105));

        btnPDF.addActionListener(e -> exportToPDF(cbJenisLaporan.getSelectedItem().toString()));
        btnCSV.addActionListener(e -> exportToCSV(cbJenisLaporan.getSelectedItem().toString()));

        btnPanel.add(btnPDF);
        btnPanel.add(btnCSV);

        gbc.gridx = 1;
        panel.add(btnPanel, gbc);

        // Info keterangan
        JLabel lblInfo = new JLabel("Catatan: PDF mencakup kop surat, ringkasan eksekutif, dan kolom tanda tangan.");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(100, 116, 139));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 0, 8);
        panel.add(lblInfo, gbc);

        return panel;
    }

    private JButton createExportButton(String text, Color colorFrom, Color colorTo) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hovered ? colorTo : colorFrom;
                Color c2 = hovered ? colorFrom : colorTo;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(195, 40));
        return btn;
    }

    // ====== LOGIKA EKSPOR PDF (sama seperti sebelumnya) ======
    private void exportToPDF(String jenisLaporan) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String userHome = System.getProperty("user.home");
            String fileName = "Laporan_K3.pdf";
            String titleStr = "LAPORAN KESELAMATAN K3";
            String summaryStr = "";

            List<LaporanInsiden>      listInsiden  = null;
            List<TindakanPerbaikan>   listCapa     = null;
            List<InspeksiKeselamatan> listInspeksi = null;
            List<DistribusiAPD>       listApd      = null;
            List<Karyawan>            listKaryawan = new KaryawanDAO().getAll();

            if (jenisLaporan.equals("Laporan Insiden K3")) {
                listInsiden = new LaporanInsidenDAO().getAll();
                if (listInsiden == null || listInsiden.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Insiden kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
                }
                fileName    = "Rekap_Laporan_Insiden_K3.pdf";
                titleStr    = "LAPORAN REKAPITULASI INSIDEN KESELAMATAN K3";
                summaryStr  = "Dokumen ini merupakan laporan resmi mengenai rekapitulasi data insiden keselamatan kerja yang terjadi di lingkungan operasional pabrik. Laporan ini ditujukan sebagai bahan evaluasi dan audit K3 internal perusahaan guna mencegah terjadinya insiden serupa di masa mendatang.";
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                listCapa = new TindakanPerbaikanDAO().getAll();
                if (listCapa == null || listCapa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data CAPA kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
                }
                fileName    = "Laporan_Tindakan_CAPA.pdf";
                titleStr    = "LAPORAN TINDAKAN PERBAIKAN & PENCEGAHAN (CAPA)";
                summaryStr  = "Dokumen ini menyajikan status tindakan korektif dan preventif (CAPA) yang ditugaskan kepada penanggung jawab masing-masing departemen. Laporan ini berguna untuk memonitor progres penyelesaian perbaikan fasilitas dan kebijakan K3.";
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                listInspeksi = new InspeksiKeselamatanDAO().getAll();
                if (listInspeksi == null || listInspeksi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Inspeksi kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
                }
                fileName    = "Laporan_Inspeksi_Keselamatan.pdf";
                titleStr    = "LAPORAN HASIL INSPEKSI KESELAMATAN AREA";
                summaryStr  = "Dokumen ini menjabarkan hasil inspeksi rutin keselamatan kerja di berbagai area pabrik. Mencakup daftar temuan bahaya, rekomendasi perbaikan, serta penilaian (skor) kepatuhan terhadap standar HSE perusahaan.";
            } else if (jenisLaporan.equals("Distribusi APD")) {
                listApd = new DistribusiAPDDAO().getAll();
                if (listApd == null || listApd.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Distribusi APD kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
                }
                fileName    = "Laporan_Distribusi_APD.pdf";
                titleStr    = "LAPORAN DISTRIBUSI ALAT PELINDUNG DIRI (APD)";
                summaryStr  = "Dokumen ini adalah rekapitulasi penyaluran Alat Pelindung Diri (APD) kepada seluruh karyawan pabrik. Digunakan untuk memastikan setiap personel telah dibekali perlengkapan standar keamanan yang sesuai dengan prosedur operasional.";
            }

            String path = userHome + File.separator + "Downloads" + File.separator + fileName;
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontKop     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontSubKop  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Font fontTitle   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontHeader  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);

            com.itextpdf.text.Paragraph kop1 = new com.itextpdf.text.Paragraph("PT. PABRIK MANUFAKTUR SEJAHTERA", fontKop);
            kop1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(kop1);
            com.itextpdf.text.Paragraph kop2 = new com.itextpdf.text.Paragraph("Departemen Health, Safety, and Environment (HSE)", fontSubKop);
            kop2.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(kop2);
            com.itextpdf.text.Paragraph kop3 = new com.itextpdf.text.Paragraph("Kawasan Industri Terpadu Blok A-1, Jakarta, Indonesia\n_____________________________________________________________________________________________________________________\n\n");
            kop3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(kop3);

            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(titleStr, fontTitle);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph("\n"));

            String namaPencetak = "Admin"; String jabPencetak = "HSE Manager";
            if (loggedInUser != null) {
                namaPencetak = loggedInUser.getUsername(); jabPencetak = loggedInUser.getPeran();
                if (listKaryawan != null && loggedInUser.getIdKaryawan() != null) {
                    for (Karyawan k : listKaryawan) {
                        if (k.getIdKaryawan().equals(loggedInUser.getIdKaryawan())) {
                            namaPencetak = k.getNamaLengkap(); jabPencetak = k.getJabatan(); break;
                        }
                    }
                }
            }
            String pencetak = namaPencetak + " (" + jabPencetak + ")";
            com.itextpdf.text.Paragraph metaInfo = new com.itextpdf.text.Paragraph();
            metaInfo.setFont(fontNormal);
            metaInfo.add("Tanggal Cetak  : " + new java.text.SimpleDateFormat("dd MMMM yyyy HH:mm").format(new java.util.Date()) + "\n");
            metaInfo.add("Dicetak Oleh    : " + pencetak + "\n\nExecutive Summary:\n" + summaryStr + "\n\n");
            document.add(metaInfo);

            com.itextpdf.text.pdf.PdfPTable tablePdf = null;
            if (jenisLaporan.equals("Laporan Insiden K3")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 2, 2, 4, 2}); tablePdf.setWidthPercentage(100);
                for (String h : new String[]{"No","Tanggal","Lokasi","Kategori","Deskripsi","Status"}) { com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader)); c.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY); tablePdf.addCell(c); }
                int no = 1; for (LaporanInsiden ins : listInsiden) { tablePdf.addCell(String.valueOf(no++)); tablePdf.addCell(ins.getTanggalKejadian()); tablePdf.addCell(ins.getLokasiKejadian()); tablePdf.addCell(ins.getKategoriInsiden()); tablePdf.addCell(ins.getDeskripsiKejadian()); tablePdf.addCell(ins.getStatusInvestigasi()); }
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 4, 3, 2, 2, 2}); tablePdf.setWidthPercentage(100);
                for (String h : new String[]{"No","Detail Tindakan","Penanggung Jawab","Deadline","Tgl Selesai","Status"}) { com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader)); c.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY); tablePdf.addCell(c); }
                int no = 1; for (TindakanPerbaikan capa : listCapa) { tablePdf.addCell(String.valueOf(no++)); tablePdf.addCell(capa.getTindakanDetail()); String pj = capa.getIdPenanggungJawab(); if (listKaryawan != null) for (Karyawan k : listKaryawan) if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) { pj = k.getNamaLengkap(); break; } tablePdf.addCell(pj); tablePdf.addCell(capa.getDeadline() != null ? capa.getDeadline() : "-"); tablePdf.addCell(capa.getTanggalSelesai() != null ? capa.getTanggalSelesai() : "-"); tablePdf.addCell(capa.getStatusTindakan()); }
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 2, 3, 1, 3}); tablePdf.setWidthPercentage(100);
                for (String h : new String[]{"No","Tanggal","Area","Temuan Bahaya","Skor","Rekomendasi"}) { com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader)); c.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY); tablePdf.addCell(c); }
                int no = 1; for (InspeksiKeselamatan ins : listInspeksi) { tablePdf.addCell(String.valueOf(no++)); tablePdf.addCell(ins.getTanggalInspeksi()); tablePdf.addCell(ins.getAreaInspeksi()); tablePdf.addCell(ins.getTemuanBahaya() != null ? ins.getTemuanBahaya() : "Aman"); tablePdf.addCell(String.valueOf(ins.getSkorKepatuhan())); tablePdf.addCell(ins.getRekomendasi()); }
            } else if (jenisLaporan.equals("Distribusi APD")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 3, 2, 1, 2}); tablePdf.setWidthPercentage(100);
                for (String h : new String[]{"No","Tanggal","Penerima","Jenis APD","Jumlah","Kondisi"}) { com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader)); c.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY); tablePdf.addCell(c); }
                int no = 1; for (DistribusiAPD apd : listApd) { tablePdf.addCell(String.valueOf(no++)); tablePdf.addCell(apd.getTanggalPembagian()); String penerima = apd.getIdKaryawan(); if (listKaryawan != null) for (Karyawan k : listKaryawan) if (k.getIdKaryawan().equals(apd.getIdKaryawan())) { penerima = k.getNamaLengkap(); break; } tablePdf.addCell(penerima); tablePdf.addCell(apd.getJenisApd()); tablePdf.addCell(String.valueOf(apd.getJumlah())); tablePdf.addCell(apd.getKondisiApd()); }
            }
            if (tablePdf != null) document.add(tablePdf);

            document.add(new com.itextpdf.text.Paragraph("\n\n\n"));
            com.itextpdf.text.pdf.PdfPTable ttdTable = new com.itextpdf.text.pdf.PdfPTable(2); ttdTable.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell cellKiri = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("")); cellKiri.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            com.itextpdf.text.Paragraph ttdText = new com.itextpdf.text.Paragraph("Mengetahui,\n\n\n\n\n_________________________\n" + namaPencetak + "\n" + jabPencetak, fontNormal); ttdText.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            com.itextpdf.text.pdf.PdfPCell cellKanan = new com.itextpdf.text.pdf.PdfPCell(ttdText); cellKanan.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cellKanan.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            ttdTable.addCell(cellKiri); ttdTable.addCell(cellKanan); document.add(ttdTable);

            document.close();
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil dibuat!\nDisimpan di: " + path, "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // ====== LOGIKA EKSPOR CSV ======
    private void exportToCSV(String jenisLaporan) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String userHome = System.getProperty("user.home");
            String fileName = "Laporan_K3.csv";
            List<LaporanInsiden>      listInsiden  = null;
            List<TindakanPerbaikan>   listCapa     = null;
            List<InspeksiKeselamatan> listInspeksi = null;
            List<DistribusiAPD>       listApd      = null;
            List<Karyawan>            listKaryawan = new KaryawanDAO().getAll();

            if (jenisLaporan.equals("Laporan Insiden K3")) {
                listInsiden = new LaporanInsidenDAO().getAll();
                if (listInsiden == null || listInsiden.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data Insiden.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
                fileName = "Rekap_Laporan_Insiden_K3.csv";
                FileWriter w = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                w.append("ID Insiden,Tanggal Kejadian,Lokasi Kejadian,Kategori Insiden,Deskripsi Kejadian,Status Investigasi\n");
                for (LaporanInsiden ins : listInsiden) { w.append(ins.getIdInsiden()).append(",").append(ins.getTanggalKejadian()).append(",").append(ins.getLokasiKejadian()).append(",").append(ins.getKategoriInsiden()).append(",").append(ins.getDeskripsiKejadian().replace(",", " ")).append(",").append(ins.getStatusInvestigasi()).append("\n"); }
                w.close();
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                listCapa = new TindakanPerbaikanDAO().getAll();
                if (listCapa == null || listCapa.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data CAPA.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
                fileName = "Laporan_Tindakan_CAPA.csv";
                FileWriter w = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                w.append("ID Tindakan,Detail Tindakan,Penanggung Jawab,Deadline,Tanggal Selesai,Status\n");
                for (TindakanPerbaikan capa : listCapa) { String pj = capa.getIdPenanggungJawab(); if (listKaryawan != null) for (Karyawan k : listKaryawan) if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) { pj = k.getNamaLengkap(); break; } w.append(capa.getIdTindakan()).append(",").append(capa.getTindakanDetail().replace(",", " ")).append(",").append(pj).append(",").append(capa.getDeadline() != null ? capa.getDeadline() : "").append(",").append(capa.getTanggalSelesai() != null ? capa.getTanggalSelesai() : "").append(",").append(capa.getStatusTindakan()).append("\n"); }
                w.close();
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                listInspeksi = new InspeksiKeselamatanDAO().getAll();
                if (listInspeksi == null || listInspeksi.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data Inspeksi.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
                fileName = "Laporan_Inspeksi_Keselamatan.csv";
                FileWriter w = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                w.append("ID Inspeksi,Tanggal,Area,Temuan Bahaya,Skor,Rekomendasi\n");
                for (InspeksiKeselamatan ins : listInspeksi) { w.append(ins.getIdInspeksi()).append(",").append(ins.getTanggalInspeksi()).append(",").append(ins.getAreaInspeksi()).append(",").append(ins.getTemuanBahaya() != null ? ins.getTemuanBahaya().replace(",", " ").replace("\n", " ") : "Aman").append(",").append(String.valueOf(ins.getSkorKepatuhan())).append(",").append(ins.getRekomendasi().replace(",", " ").replace("\n", " ")).append("\n"); }
                w.close();
            } else if (jenisLaporan.equals("Distribusi APD")) {
                listApd = new DistribusiAPDDAO().getAll();
                if (listApd == null || listApd.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data APD.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
                fileName = "Laporan_Distribusi_APD.csv";
                FileWriter w = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                w.append("ID Distribusi,Tanggal Pembagian,Penerima,Jenis APD,Jumlah,Kondisi\n");
                for (DistribusiAPD apd : listApd) { String penerima = apd.getIdKaryawan(); if (listKaryawan != null) for (Karyawan k : listKaryawan) if (k.getIdKaryawan().equals(apd.getIdKaryawan())) { penerima = k.getNamaLengkap(); break; } w.append(apd.getIdDistribusi()).append(",").append(apd.getTanggalPembagian()).append(",").append(penerima).append(",").append(apd.getJenisApd()).append(",").append(String.valueOf(apd.getJumlah())).append(",").append(apd.getKondisiApd()).append("\n"); }
                w.close();
            }

            String path = userHome + File.separator + "Downloads" + File.separator + fileName;
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan CSV berhasil dibuat!\nDisimpan di: " + path, "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
