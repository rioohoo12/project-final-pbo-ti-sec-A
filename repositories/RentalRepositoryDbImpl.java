package rentalapp.repositories;

import org.springframework.stereotype.Component;
import rentalapp.config.Database;
import rentalapp.entities.Rental;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RentalRepositoryDbImpl implements rentalapp.repositories.RentalRepository {
    private final Database database;

    public RentalRepositoryDbImpl(Database database) {
        this.database = database;
    }

    @Override
    public Rental[] getAllKendaraan() {
        Connection conn = database.getConnection();
        List<Rental> kendaraanList = new ArrayList<>();
        String query = "SELECT * FROM kendaraan";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rental kendaraan = new Rental();
                kendaraan.setId(rs.getInt("id"));
                kendaraan.setJenisKendaraan(rs.getString("jenis_kendaraan"));
                kendaraan.setNamaKendaraan(rs.getString("nama_kendaraan"));
                kendaraan.setPlatNomor(rs.getString("plat_nomor"));
                kendaraan.setHargaSewa(rs.getDouble("harga_sewa"));
                kendaraan.setSedangDisewa(rs.getBoolean("sedang_disewa"));
                kendaraanList.add(kendaraan);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return kendaraanList.toArray(Rental[]::new);
    }

    @Override
    public Rental[] getKendaraanTersedia() {
        Connection conn = database.getConnection();
        List<Rental> kendaraanList = new ArrayList<>();
        String query = "SELECT * FROM kendaraan WHERE sedang_disewa = false";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rental kendaraan = new Rental();
                kendaraan.setId(rs.getInt("id"));
                kendaraan.setJenisKendaraan(rs.getString("jenis_kendaraan"));
                kendaraan.setNamaKendaraan(rs.getString("nama_kendaraan"));
                kendaraan.setPlatNomor(rs.getString("plat_nomor"));
                kendaraan.setHargaSewa(rs.getDouble("harga_sewa"));
                kendaraan.setSedangDisewa(false);
                kendaraanList.add(kendaraan);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return kendaraanList.toArray(Rental[]::new);
    }

    @Override
    public void tambahKendaraan(Rental kendaraan) {
        Connection conn = database.getConnection();
        String query = "INSERT INTO kendaraan " +
                "(jenis_kendaraan, nama_kendaraan, plat_nomor, harga_sewa, sedang_disewa) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, kendaraan.getJenisKendaraan());
            stmt.setString(2, kendaraan.getNamaKendaraan());
            stmt.setString(3, kendaraan.getPlatNomor());
            stmt.setDouble(4, kendaraan.getHargaSewa());
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Boolean hapusKendaraan(Integer id) {
        Connection conn = database.getConnection();
        String query = "DELETE FROM kendaraan WHERE id = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean sewaKendaraan(Integer id, String notelpon, String date) {
        Connection conn = database.getConnection();
        String checkUserQuery = "SELECT * FROM user WHERE noTelp = ?";
        String updateKendaraanQuery = "UPDATE kendaraan SET sedang_disewa = true WHERE id = ? AND sedang_disewa = false";
        String insertHistoryQuery = "INSERT INTO history (id_kendaraan, noTelp, tanggal_sewa) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Cek apakah pengguna terdaftar
            try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery)) {
                checkUserStmt.setString(1, notelpon);
                try (ResultSet rs = checkUserStmt.executeQuery()) {
                    if (!rs.next()) { // Jika tidak ditemukan
                        System.out.println("Pengguna dengan no telepon " + notelpon + " tidak terdaftar.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Update status kendaraan
            try (PreparedStatement updateKendaraanStmt = conn.prepareStatement(updateKendaraanQuery)) {
                updateKendaraanStmt.setInt(1, id);
                int kendaraanUpdated = updateKendaraanStmt.executeUpdate();
                if (kendaraanUpdated == 0) { // Jika tidak ada kendaraan yang diperbarui
                    System.out.println("Kendaraan dengan ID " + id + " tidak tersedia untuk disewa.");
                    conn.rollback();
                    return false;
                }
            }

            // 3. Insert ke tabel history
            try (PreparedStatement insertHistoryStmt = conn.prepareStatement(insertHistoryQuery)) {
                insertHistoryStmt.setInt(1, id);
                insertHistoryStmt.setString(2, notelpon);
                insertHistoryStmt.setString(3, date);
                insertHistoryStmt.executeUpdate();
            }

            conn.commit(); // Komit transaksi jika semua berhasil
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    @Override
    public Boolean kembalikanKendaraan(Integer id) {
        Connection conn = database.getConnection();
        String query = "UPDATE kendaraan SET sedang_disewa = false, penyewa = NULL WHERE id = ? AND sedang_disewa = true";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean login(String noTelpon) {
        Connection conn = database.getConnection();
        String query = "SELECT id FROM user WHERE noTelp = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, noTelpon); // Mengatur parameter noTelpon

            try (ResultSet rs = stmt.executeQuery()) { // Menjalankan query SELECT
                if (rs.next()) {
                    return true; // Mengembalikan true jika ada hasil
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false; // Mengembalikan false jika tidak ada hasil atau terjadi kesalahan
    }

    @Override
    public Boolean daftar(String noTelpon, String nama) {
        Connection conn = database.getConnection();
        String query = "INSERT INTO user (noTelp, nama) VALUES (?, ?)";
        try  {
            // Pastikan koneksi terbuka sebelum membuat statement
            if (conn == null || conn.isClosed()) {
                System.out.println("Koneksi ke database sudah ditutup.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, noTelpon);
                stmt.setString(2, nama);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Rental[] historySewa(String noTelpon) {
        Connection conn = database.getConnection();
        List<Rental> kendaraanList = new ArrayList<>();
        String historyQuery = "SELECT * FROM history WHERE noTelp = ?";
        String kendaraanQuery = "SELECT * FROM kendaraan WHERE id = ?";

        try {
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);

            // Set parameter untuk query history
            historyStmt.setString(1, noTelpon);

            // Eksekusi query untuk mendapatkan id_kendaraan
            try (ResultSet historyRs = historyStmt.executeQuery()) {
                while (historyRs.next()) {
                    int idKendaraan = historyRs.getInt("id_kendaraan");

                    // Query kendaraan berdasarkan id_kendaraan
                    try (PreparedStatement kendaraanStmt = conn.prepareStatement(kendaraanQuery)) {
                        kendaraanStmt.setInt(1, idKendaraan);

                        try (ResultSet kendaraanRs = kendaraanStmt.executeQuery()) {
                            if (kendaraanRs.next()) {
                                Rental kendaraan = new Rental();
                                kendaraan.setId(kendaraanRs.getInt("id"));
                                kendaraan.setJenisKendaraan(kendaraanRs.getString("jenis_kendaraan"));
                                kendaraan.setNamaKendaraan(kendaraanRs.getString("nama_kendaraan"));
                                kendaraan.setPlatNomor(kendaraanRs.getString("plat_nomor"));
                                kendaraan.setHargaSewa(kendaraanRs.getDouble("harga_sewa"));
                                kendaraan.setTanggal(historyRs.getString("tanggal_sewa"));
                                kendaraanList.add(kendaraan);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return kendaraanList.toArray(Rental[]::new);
    }

    @Override
    public Rental[] statusSewa(String noTelpon) {
        Connection conn = database.getConnection();
        List<Rental> kendaraanList = new ArrayList<>();
        String historyQuery = "SELECT * FROM history WHERE noTelp = ? ORDER BY tanggal_sewa DESC LIMIT 1"; // Ambil pemesanan terakhir
        String kendaraanQuery = "SELECT * FROM kendaraan WHERE id = ? AND sedang_disewa = true";

        try {
            PreparedStatement historyStmt = conn.prepareStatement(historyQuery);
            // Set parameter untuk query history
            historyStmt.setString(1, noTelpon);

            // Eksekusi query untuk mendapatkan id_kendaraan
            try (ResultSet historyRs = historyStmt.executeQuery()) {
                if (historyRs.next()) {
                    int idKendaraan = historyRs.getInt("id_kendaraan");

                    // Query kendaraan berdasarkan id_kendaraan
                    try (PreparedStatement kendaraanStmt = conn.prepareStatement(kendaraanQuery)) {
                        kendaraanStmt.setInt(1, idKendaraan);

                        try (ResultSet kendaraanRs = kendaraanStmt.executeQuery()) {
                            if (kendaraanRs.next()) {
                                Rental kendaraan = new Rental();
                                kendaraan.setId(historyRs.getInt("id"));
                                kendaraan.setJenisKendaraan(kendaraanRs.getString("jenis_kendaraan"));
                                kendaraan.setNamaKendaraan(kendaraanRs.getString("nama_kendaraan"));
                                kendaraan.setPlatNomor(kendaraanRs.getString("plat_nomor"));
                                kendaraan.setHargaSewa(kendaraanRs.getDouble("harga_sewa"));
                                kendaraan.setTanggal(historyRs.getString("tanggal_sewa"));
                                kendaraanList.add(kendaraan);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return kendaraanList.toArray(Rental[]::new);
    }
    @Override
    public Boolean BatalSewa(Integer id) {
        String getKendaraanQuery = "SELECT id_kendaraan FROM history WHERE id = ?";  // Mendapatkan id_kendaraan berdasarkan id history
        String deleteHistoryQuery = "DELETE FROM history WHERE id = ?";  // Menghapus riwayat penyewaan berdasarkan id history
        String updateKendaraanQuery = "UPDATE kendaraan SET sedang_disewa = false WHERE id = ?";  // Update status kendaraan

        try {Connection conn = database.getConnection();
            PreparedStatement getKendaraanStmt = conn.prepareStatement(getKendaraanQuery);
            PreparedStatement deleteHistoryStmt = conn.prepareStatement(deleteHistoryQuery);
            PreparedStatement updateKendaraanStmt = conn.prepareStatement(updateKendaraanQuery);

            // Mulai transaksi
            conn.setAutoCommit(false);

            try {
                // Mendapatkan ID kendaraan berdasarkan ID history
                getKendaraanStmt.setInt(1, id);
                try (ResultSet rs = getKendaraanStmt.executeQuery()) {
                    if (rs.next()) {
                        int idKendaraan = rs.getInt("id_kendaraan");

                        // Update tabel kendaraan untuk mengubah status `sedang_disewa` menjadi false
                        updateKendaraanStmt.setInt(1, idKendaraan);
                        int kendaraanUpdated = updateKendaraanStmt.executeUpdate();

                        // Hapus data dari tabel history untuk id history yang sesuai
                        deleteHistoryStmt.setInt(1, id);
                        int historyDeleted = deleteHistoryStmt.executeUpdate();

                        // Jika kedua operasi berhasil, commit transaksi
                        if (kendaraanUpdated > 0 && historyDeleted > 0) {
                            conn.commit(); // Commit perubahan
                            return true;
                        }
                    }
                }

                conn.rollback(); // Rollback jika ada yang gagal
                return false;
            } catch (Exception e) {
                conn.rollback(); // Rollback transaksi jika terjadi kesalahan
                System.out.println(e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true); // Set kembali ke auto-commit setelah transaksi selesai
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public String PendapatanSebulan() {
        Connection conn = database.getConnection();
        String query = "SELECT SUM(k.harga_sewa) AS total_pendapatan " +
                "FROM history h " +
                "JOIN kendaraan k ON h.id_kendaraan = k.id " +
                "WHERE MONTH(h.tanggal_sewa) = ? AND YEAR(h.tanggal_sewa) = ?";

        double totalPendapatan = 0;

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            // Menentukan bulan dan tahun saat ini
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();  // Bulan saat ini
            int currentYear = currentDate.getYear();        // Tahun saat ini

            // Mengatur parameter untuk bulan dan tahun
            stmt.setInt(1, currentMonth);
            stmt.setInt(2, currentYear);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalPendapatan = rs.getDouble("total_pendapatan");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("Pendapatan Sebulan: %.2f", totalPendapatan);
    }

}