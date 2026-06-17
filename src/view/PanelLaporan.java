package view;

import dao.DistribusiAPDDAO;
import dao.InspeksiKeselamatanDAO;
import dao.KaryawanDAO;
import dao.LaporanInsidenDAO;
import dao.TindakanPerbaikanDAO;
import model.DistribusiAPD;
import model.InspeksiKeselamatan;
import model.Karyawan;
import model.LaporanInsiden;
import model.Pengguna;
import model.TindakanPerbaikan;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class PanelLaporan extends JPanel {
    private Pengguna loggedInUser;
    private JButton btnExportPDF, btnExportCSV;
    private JComboBox<String> cbJenisLaporan;

    public PanelLaporan(Pengguna user) {
        this.loggedInUser = user;
        setLayout(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTitle = new JLabel("Menu Ekspor Laporan Komprehensif K3", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);
        
        JLabel lblPilih = new JLabel("Pilih Jenis Laporan:");
        lblPilih.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lblPilih, gbc);
        
        cbJenisLaporan = new JComboBox<>(new String[]{
            "Laporan Insiden K3", "Tindakan Perbaikan (CAPA)", "Inspeksi Keselamatan", "Distribusi APD"
        });
        cbJenisLaporan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(cbJenisLaporan, gbc);
        
        btnExportPDF = new JButton("Ekspor ke PDF (Format Resmi)");
        btnExportPDF.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(btnExportPDF, gbc);
        
        btnExportCSV = new JButton("Ekspor ke CSV (Excel)");
        btnExportCSV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        add(btnExportCSV, gbc);
        
        btnExportPDF.addActionListener(e -> exportToPDF(cbJenisLaporan.getSelectedItem().toString()));
        btnExportCSV.addActionListener(e -> exportToCSV(cbJenisLaporan.getSelectedItem().toString()));
    }

    private void exportToPDF(String jenisLaporan) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String userHome = System.getProperty("user.home");
            String fileName = "Laporan_K3.pdf";
            String titleStr = "LAPORAN KESELAMATAN K3";
            String summaryStr = "";

            List<LaporanInsiden> listInsiden = null;
            List<TindakanPerbaikan> listCapa = null;
            List<InspeksiKeselamatan> listInspeksi = null;
            List<DistribusiAPD> listApd = null;
            List<Karyawan> listKaryawan = new KaryawanDAO().getAll();

            if (jenisLaporan.equals("Laporan Insiden K3")) {
                listInsiden = new LaporanInsidenDAO().getAll();
                if (listInsiden == null || listInsiden.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Insiden kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Rekap_Laporan_Insiden_K3.pdf";
                titleStr = "LAPORAN REKAPITULASI INSIDEN KESELAMATAN K3";
                summaryStr = "Dokumen ini merupakan laporan resmi mengenai rekapitulasi data insiden keselamatan kerja yang terjadi di lingkungan operasional pabrik. Laporan ini ditujukan sebagai bahan evaluasi dan audit K3 internal perusahaan guna mencegah terjadinya insiden serupa di masa mendatang.";
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                listCapa = new TindakanPerbaikanDAO().getAll();
                if (listCapa == null || listCapa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data CAPA kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Tindakan_CAPA.pdf";
                titleStr = "LAPORAN TINDAKAN PERBAIKAN & PENCEGAHAN (CAPA)";
                summaryStr = "Dokumen ini menyajikan status tindakan korektif dan preventif (CAPA) yang ditugaskan kepada penanggung jawab masing-masing departemen. Laporan ini berguna untuk memonitor progres penyelesaian perbaikan fasilitas dan kebijakan K3.";
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                listInspeksi = new InspeksiKeselamatanDAO().getAll();
                if (listInspeksi == null || listInspeksi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Inspeksi kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Inspeksi_Keselamatan.pdf";
                titleStr = "LAPORAN HASIL INSPEKSI KESELAMATAN AREA";
                summaryStr = "Dokumen ini menjabarkan hasil inspeksi rutin keselamatan kerja di berbagai area pabrik. Mencakup daftar temuan bahaya, rekomendasi perbaikan, serta penilaian (skor) kepatuhan terhadap standar HSE perusahaan.";
            } else if (jenisLaporan.equals("Distribusi APD")) {
                listApd = new DistribusiAPDDAO().getAll();
                if (listApd == null || listApd.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data Distribusi APD kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Distribusi_APD.pdf";
                titleStr = "LAPORAN DISTRIBUSI ALAT PELINDUNG DIRI (APD)";
                summaryStr = "Dokumen ini adalah rekapitulasi penyaluran Alat Pelindung Diri (APD) kepada seluruh karyawan pabrik. Digunakan untuk memastikan setiap personel telah dibekali perlengkapan standar keamanan yang sesuai dengan prosedur operasional.";
            }

            String path = userHome + File.separator + "Downloads" + File.separator + fileName;
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontKop = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontSubKop = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);

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

            String namaPencetak = "Admin";
            String jabPencetak = "HSE Manager";
            if (loggedInUser != null) {
                namaPencetak = loggedInUser.getUsername();
                jabPencetak = loggedInUser.getPeran();
                if (listKaryawan != null && loggedInUser.getIdKaryawan() != null) {
                    for (Karyawan k : listKaryawan) {
                        if (k.getIdKaryawan().equals(loggedInUser.getIdKaryawan())) {
                            namaPencetak = k.getNamaLengkap();
                            jabPencetak = k.getJabatan();
                            break;
                        }
                    }
                }
            }
            String pencetak = namaPencetak + " (" + jabPencetak + ")";
            com.itextpdf.text.Paragraph metaInfo = new com.itextpdf.text.Paragraph();
            metaInfo.setFont(fontNormal);
            metaInfo.add("Tanggal Cetak  : " + new java.text.SimpleDateFormat("dd MMMM yyyy HH:mm").format(new java.util.Date()) + "\n");
            metaInfo.add("Dicetak Oleh    : " + pencetak + "\n\n");
            metaInfo.add("Executive Summary:\n");
            metaInfo.add(summaryStr + "\n\n");
            document.add(metaInfo);

            com.itextpdf.text.pdf.PdfPTable tablePdf = null;

            if (jenisLaporan.equals("Laporan Insiden K3")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 2, 2, 4, 2});
                tablePdf.setWidthPercentage(100);
                String[] headers = {"No", "Tanggal", "Lokasi", "Kategori", "Deskripsi", "Status"};
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader));
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablePdf.addCell(cell);
                }
                int no = 1;
                for (LaporanInsiden ins : listInsiden) {
                    tablePdf.addCell(String.valueOf(no++));
                    tablePdf.addCell(ins.getTanggalKejadian());
                    tablePdf.addCell(ins.getLokasiKejadian());
                    tablePdf.addCell(ins.getKategoriInsiden());
                    tablePdf.addCell(ins.getDeskripsiKejadian());
                    tablePdf.addCell(ins.getStatusInvestigasi());
                }
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 4, 3, 2, 2, 2});
                tablePdf.setWidthPercentage(100);
                String[] headers = {"No", "Detail Tindakan", "Penanggung Jawab", "Deadline", "Tgl Selesai", "Status"};
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader));
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablePdf.addCell(cell);
                }
                int no = 1;
                for (TindakanPerbaikan capa : listCapa) {
                    tablePdf.addCell(String.valueOf(no++));
                    tablePdf.addCell(capa.getTindakanDetail());
                    String pjName = capa.getIdPenanggungJawab();
                    if (listKaryawan != null) {
                        for (Karyawan k : listKaryawan) {
                            if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) {
                                pjName = k.getNamaLengkap();
                                break;
                            }
                        }
                    }
                    tablePdf.addCell(pjName);
                    tablePdf.addCell(capa.getDeadline() != null ? capa.getDeadline() : "-");
                    tablePdf.addCell(capa.getTanggalSelesai() != null ? capa.getTanggalSelesai() : "-");
                    tablePdf.addCell(capa.getStatusTindakan());
                }
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 2, 3, 1, 3});
                tablePdf.setWidthPercentage(100);
                String[] headers = {"No", "Tanggal", "Area", "Temuan Bahaya", "Skor", "Rekomendasi"};
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader));
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablePdf.addCell(cell);
                }
                int no = 1;
                for (InspeksiKeselamatan ins : listInspeksi) {
                    tablePdf.addCell(String.valueOf(no++));
                    tablePdf.addCell(ins.getTanggalInspeksi());
                    tablePdf.addCell(ins.getAreaInspeksi());
                    tablePdf.addCell(ins.getTemuanBahaya() != null ? ins.getTemuanBahaya() : "Aman");
                    tablePdf.addCell(String.valueOf(ins.getSkorKepatuhan()));
                    tablePdf.addCell(ins.getRekomendasi());
                }
            } else if (jenisLaporan.equals("Distribusi APD")) {
                tablePdf = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 2, 3, 2, 1, 2});
                tablePdf.setWidthPercentage(100);
                String[] headers = {"No", "Tanggal", "Penerima", "Jenis APD", "Jumlah", "Kondisi"};
                for (String h : headers) {
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h, fontHeader));
                    cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablePdf.addCell(cell);
                }
                int no = 1;
                for (DistribusiAPD apd : listApd) {
                    tablePdf.addCell(String.valueOf(no++));
                    tablePdf.addCell(apd.getTanggalPembagian());
                    String penerima = apd.getIdKaryawan();
                    if (listKaryawan != null) {
                        for (Karyawan k : listKaryawan) {
                            if (k.getIdKaryawan().equals(apd.getIdKaryawan())) {
                                penerima = k.getNamaLengkap();
                                break;
                            }
                        }
                    }
                    tablePdf.addCell(penerima);
                    tablePdf.addCell(apd.getJenisApd());
                    tablePdf.addCell(String.valueOf(apd.getJumlah()));
                    tablePdf.addCell(apd.getKondisiApd());
                }
            }

            if (tablePdf != null) document.add(tablePdf);

            document.add(new com.itextpdf.text.Paragraph("\n\n\n"));
            com.itextpdf.text.pdf.PdfPTable ttdTable = new com.itextpdf.text.pdf.PdfPTable(2);
            ttdTable.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell cellKiri = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(""));
            cellKiri.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

            String namaTtd = namaPencetak;
            String jabTtd = jabPencetak;
            com.itextpdf.text.Paragraph ttdText = new com.itextpdf.text.Paragraph("Mengetahui,\n\n\n\n\n_________________________\n" + namaTtd + "\n" + jabTtd, fontNormal);
            ttdText.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            com.itextpdf.text.pdf.PdfPCell cellKanan = new com.itextpdf.text.pdf.PdfPCell(ttdText);
            cellKanan.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            cellKanan.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);

            ttdTable.addCell(cellKiri);
            ttdTable.addCell(cellKanan);
            document.add(ttdTable);

            document.close();
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan Resmi " + jenisLaporan + " berhasil dibuat di folder Downloads!\nPath: " + path, "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void exportToCSV(String jenisLaporan) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String userHome = System.getProperty("user.home");
            String fileName = "Laporan_K3.csv";

            List<LaporanInsiden> listInsiden = null;
            List<TindakanPerbaikan> listCapa = null;
            List<InspeksiKeselamatan> listInspeksi = null;
            List<DistribusiAPD> listApd = null;
            List<Karyawan> listKaryawan = new KaryawanDAO().getAll();

            if (jenisLaporan.equals("Laporan Insiden K3")) {
                listInsiden = new LaporanInsidenDAO().getAll();
                if (listInsiden == null || listInsiden.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data Insiden.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Rekap_Laporan_Insiden_K3.csv";
                FileWriter writer = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                writer.append("ID Insiden,Tanggal Kejadian,Lokasi Kejadian,Kategori Insiden,Deskripsi Kejadian,Status Investigasi\n");
                for (LaporanInsiden ins : listInsiden) {
                    writer.append(ins.getIdInsiden()).append(",")
                          .append(ins.getTanggalKejadian()).append(",")
                          .append(ins.getLokasiKejadian()).append(",")
                          .append(ins.getKategoriInsiden()).append(",")
                          .append(ins.getDeskripsiKejadian().replace(",", " ")).append(",") 
                          .append(ins.getStatusInvestigasi()).append("\n");
                }
                writer.close();
            } else if (jenisLaporan.equals("Tindakan Perbaikan (CAPA)")) {
                listCapa = new TindakanPerbaikanDAO().getAll();
                if (listCapa == null || listCapa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data CAPA.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Tindakan_CAPA.csv";
                FileWriter writer = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                writer.append("ID Tindakan,Detail Tindakan,Penanggung Jawab,Deadline,Tanggal Selesai,Status\n");
                for (TindakanPerbaikan capa : listCapa) {
                    String pjName = capa.getIdPenanggungJawab();
                    if (listKaryawan != null) {
                        for (Karyawan k : listKaryawan) {
                            if (k.getIdKaryawan().equals(capa.getIdPenanggungJawab())) {
                                pjName = k.getNamaLengkap();
                                break;
                            }
                        }
                    }
                    writer.append(capa.getIdTindakan()).append(",")
                          .append(capa.getTindakanDetail().replace(",", " ")).append(",")
                          .append(pjName).append(",")
                          .append(capa.getDeadline() != null ? capa.getDeadline() : "").append(",")
                          .append(capa.getTanggalSelesai() != null ? capa.getTanggalSelesai() : "").append(",")
                          .append(capa.getStatusTindakan()).append("\n");
                }
                writer.close();
            } else if (jenisLaporan.equals("Inspeksi Keselamatan")) {
                listInspeksi = new InspeksiKeselamatanDAO().getAll();
                if (listInspeksi == null || listInspeksi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data Inspeksi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Inspeksi_Keselamatan.csv";
                FileWriter writer = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                writer.append("ID Inspeksi,Tanggal,Area,Temuan Bahaya,Skor,Rekomendasi\n");
                for (InspeksiKeselamatan ins : listInspeksi) {
                    writer.append(ins.getIdInspeksi()).append(",")
                          .append(ins.getTanggalInspeksi()).append(",")
                          .append(ins.getAreaInspeksi()).append(",")
                          .append(ins.getTemuanBahaya() != null ? ins.getTemuanBahaya().replace(",", " ").replace("\n", " ") : "Aman").append(",")
                          .append(String.valueOf(ins.getSkorKepatuhan())).append(",")
                          .append(ins.getRekomendasi().replace(",", " ").replace("\n", " ")).append("\n");
                }
                writer.close();
            } else if (jenisLaporan.equals("Distribusi APD")) {
                listApd = new DistribusiAPDDAO().getAll();
                if (listApd == null || listApd.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data Distribusi APD.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                fileName = "Laporan_Distribusi_APD.csv";
                FileWriter writer = new FileWriter(userHome + File.separator + "Downloads" + File.separator + fileName);
                writer.append("ID Distribusi,Tanggal Pembagian,Penerima,Jenis APD,Jumlah,Kondisi\n");
                for (DistribusiAPD apd : listApd) {
                    String penerima = apd.getIdKaryawan();
                    if (listKaryawan != null) {
                        for (Karyawan k : listKaryawan) {
                            if (k.getIdKaryawan().equals(apd.getIdKaryawan())) {
                                penerima = k.getNamaLengkap();
                                break;
                            }
                        }
                    }
                    writer.append(apd.getIdDistribusi()).append(",")
                          .append(apd.getTanggalPembagian()).append(",")
                          .append(penerima).append(",")
                          .append(apd.getJenisApd()).append(",")
                          .append(String.valueOf(apd.getJumlah())).append(",")
                          .append(apd.getKondisiApd()).append("\n");
                }
                writer.close();
            }

            String path = userHome + File.separator + "Downloads" + File.separator + fileName;
            Desktop.getDesktop().open(new File(path));
            JOptionPane.showMessageDialog(this, "Laporan CSV berhasil dibuat di folder Downloads!\nPath: " + path, "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
