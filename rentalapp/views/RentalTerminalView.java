package rentalapp.views;

import org.springframework.stereotype.Component;
import rentalapp.service.RentalService;
import rentalapp.entities.Rental;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Component
public class RentalTerminalView implements RentalView {
    private static Scanner scanner = new Scanner(System.in);
    private final RentalService rentalService;

    public RentalTerminalView(RentalService rentalService) {
        this.rentalService = rentalService;
    }
    public String nama="null";
    public String date="null";
    public String noTelpon="null";
    public Boolean running = false;
    @Override
    public void run() {
        menuUtama();
    }

    public void menuUtama() {
        this.running = true;
        while (this.running) {
            try {
                SelectRole();
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private void SelectRole() {
        System.out.println("Selamat Datang\nSilahkan Pilih masuk sebagai apa?");
        System.out.println("1. User\n2. Admin\n0. Keluar");
        String user = input("Masuk sebagai");
        switch (user){
            case "1":
                authUser();
                break;
            case "2":menuAdmin();break;
            case "0": System.out.println("Terima kasih telah menggunakan aplikasi.");
                this.running = false;
                break;
            default:
                System.out.println("Pilihan tidak valid. Silakan coba lagi.");
        }
    }

    private void authUser(){
        this.noTelpon = input("Masukkan No Telepon");
        boolean status =rentalService.login(this.noTelpon);
        if(status){
            menuUser();
        }else{
            this.nama = input("Nomor Belum terdaftar\nMasukkan Nama untuk nomor "+noTelpon);
            status = rentalService.daftar(this.noTelpon,this.nama);
            if (status){
                menuUser();
            }
        }
    }
    private void tampilkanSemuaKendaraan() {
        Rental[] kendaraan = rentalService.daftarKendaraan();
        if (kendaraan.length == 0) {
            System.out.println("Tidak ada kendaraan yang terdaftar.");
            return;
        }

        System.out.println("\n--- DAFTAR SEMUA KENDARAAN ---");
        for (int i = 0; i < kendaraan.length; i++) {
            Rental k = kendaraan[i];
            System.out.printf("%d. %s - %s (%s) - Rp%.2f %s\n",
                    i + 1, k.getJenisKendaraan(), k.getNamaKendaraan(),
                    k.getPlatNomor(), k.getHargaSewa(),
                    k.isSedangDisewa() ? "[Disewa]" : "[Tersedia]");
        }
    }
    private void menuAdmin(){
        while (this.running){
            try{
                System.out.println("\n--- RENTAL KENDARAAN ---");
                System.out.println("1. Daftar Kendaraan");
                System.out.println("2. Kendaraan Tersedia");
                System.out.println("3. Tambah Kendaraan");
                System.out.println("4. Sewa Kendaraan");
                System.out.println("5. Kembalikan Kendaraan");
                System.out.println("6. Hapus Kendaraan");
                System.out.println("7. Pendapatan Sebulan");
                System.out.println("99. Menu Role");

                String pilihan = input("Pilih Menu");

                switch (pilihan) {
                    case "1": tampilkanSemuaKendaraan(); break;
                    case "2": tampilkanKendaraanTersedia(); break;
                    case "3": tambahKendaraan(); break;
                    case "4": sewaKendaraan(); break;
                    case "5": kembalikanKendaraan(); break;
                    case "6": hapusKendaraan(); break;
                    case "7": pendatapanSebulan(); break;
                    case "99":
                        System.out.println("Anda Sudah Keluar Dari Role User.");
                        this.noTelpon="null";
                        this.date="null";
                        this.nama="null";
                        SelectRole();
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }

            }catch (Exception e){
                System.out.println("Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private void pendatapanSebulan() {
        String pendapatan=rentalService.PendapatanSebulan();
        System.out.println(pendapatan);
    }

    private void menuUser(){
        while (this.running){
            try{
                System.out.println("\n--- RENTAL KENDARAAN ---");
                System.out.println("1. Daftar Kendaraan");
                System.out.println("2. Kendaraan Tersedia");
                System.out.println("3. Sewa Kendaraan");
                System.out.println("4. Edit Sewa");
                System.out.println("5. Batal Sewa");
                System.out.println("6. History Sewa");
                System.out.println("99. Menu Role");
                String pilihan = input("Pilih Menu");
                switch (pilihan) {
                    case "1": tampilkanSemuaKendaraan(); break;
                    case "2": tampilkanKendaraanTersedia(); break;
                    case "3": sewaKendaraan(); break;
                    case "4": editSewa(); break;
                    case "5": batalSewa(); break;
                    case "6": historySewa(); break;
                    case "99":
                        System.out.println("Anda Sudah Keluar Dari Role User.");
                        this.noTelpon="null";
                        this.date="null";
                        this.nama="null";
                        SelectRole();
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            }catch (Exception e){
                System.out.println("Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private void historySewa() {
        Rental[] kendaraan = rentalService.historySewa(this.noTelpon);
        if(kendaraan.length == 0){
            System.out.println("Tidak Ada History Pesanan untuk anda");
            return;
        }
        System.out.println("\n---History Pesanan Kendaraan ---");
        for (int i = 0; i < kendaraan.length; i++) {
            Rental k = kendaraan[i];
            System.out.printf("%d. %s - %s (%s) - Rp%.2f %s\n",
                    i + 1, k.getJenisKendaraan(), k.getNamaKendaraan(),
                    k.getPlatNomor(), k.getHargaSewa(), k.getTanggal());
        }
    }

    private void batalSewa() {
        try {
            Rental[] kendaraan = rentalService.StatusSewa(this.noTelpon);

            if (kendaraan.length == 0) {
                System.out.println("Tidak ada history pesanan untuk Anda.");
                return;
            }

            System.out.println("\n--- Pesanan Kendaraan Anda ---");
            for (int i = 0; i < kendaraan.length; i++) {
                Rental k = kendaraan[i];
                System.out.printf("%d. %s - %s (%s) - Rp%.2f %s\n",
                        i + 1, k.getJenisKendaraan(), k.getNamaKendaraan(),
                        k.getPlatNomor(), k.getHargaSewa(), k.getTanggal());
            }


            // Meminta konfirmasi untuk membatalkan pesanan
            String status = input("Ingin membatalkan pesanan tersebut? (Y/N)").trim().toUpperCase();

            if ("Y".equals(status)) {
                int nomorPesanan;
                while (true) {
                    try {
                        nomorPesanan = Integer.parseInt(input("Masukkan nomor pesanan yang ingin dibatalkan:"));
                        if (nomorPesanan > 0 && nomorPesanan <= kendaraan.length) {
                            break;
                        } else {
                            System.out.println("Nomor pesanan tidak valid. Pilih nomor antara 1 dan " + kendaraan.length + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Masukkan angka yang valid untuk nomor pesanan.");
                    }
                }

                Rental kendaraanDipilih = kendaraan[nomorPesanan - 1];
                boolean berhasil = rentalService.Batalsewa(kendaraanDipilih.getId());

                if (berhasil) {
                    System.out.println("Pesanan berhasil dibatalkan.");
                } else {
                    System.out.println("Gagal membatalkan pesanan. Silakan coba lagi.");
                }
            } else {
                System.out.println("Pesanan tidak dibatalkan.");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membatalkan pesanan: " + e.getMessage());
        }
    }


    private void editSewa() {
        try {
            Rental[] kendaraan = rentalService.StatusSewa(this.noTelpon);

            if (kendaraan.length == 0) {
                System.out.println("Tidak ada history pesanan untuk Anda.");
                return;
            }

            System.out.println("\n--- Pesanan Kendaraan Anda ---");
            for (int i = 0; i < kendaraan.length; i++) {
                Rental k = kendaraan[i];
                System.out.printf("%d. %s - %s (%s) - Rp%.2f %s\n",
                        i + 1, k.getJenisKendaraan(), k.getNamaKendaraan(),
                        k.getPlatNomor(), k.getHargaSewa(), k.getTanggal());
            }

            // Meminta konfirmasi untuk membatalkan pesanan
            String status = input("Ingin Edit Tanggal Pesanan tersebut? (Y/N)").trim().toUpperCase();

            if ("Y".equals(status)) {
                int nomorPesanan;
                while (true) {
                    try {
                        nomorPesanan = Integer.parseInt(input("Masukkan nomor pesanan yang ingin dibatalkan:"));
                        if (nomorPesanan > 0 && nomorPesanan <= kendaraan.length) {
                            break;
                        } else {
                            System.out.println("Nomor pesanan tidak valid. Pilih nomor antara 1 dan " + kendaraan.length + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Masukkan angka yang valid untuk nomor pesanan.");
                    }
                }

                Rental kendaraanDipilih = kendaraan[nomorPesanan - 1];
                boolean berhasil = rentalService.Batalsewa(kendaraanDipilih.getId());

                if (berhasil) {
                    System.out.println("Pesanan berhasil dibatalkan.");
                } else {
                    System.out.println("Gagal membatalkan pesanan. Silakan coba lagi.");
                }
            } else {
                System.out.println("Pesanan tidak dibatalkan.");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membatalkan pesanan: " + e.getMessage());
        }
    }

    private void tampilkanKendaraanTersedia() {
        Rental[] kendaraan = rentalService.kendaraanTersedia();
        if (kendaraan.length == 0) {
            System.out.println("Tidak ada kendaraan yang tersedia.");
            return;
        }

        System.out.println("\n--- KENDARAAN TERSEDIA ---");
        for (int i = 0; i < kendaraan.length; i++) {
            Rental k = kendaraan[i];
            System.out.printf("%d. %s - %s (%s) - Rp%.2f\n",
                    i + 1, k.getJenisKendaraan(), k.getNamaKendaraan(),
                    k.getPlatNomor(), k.getHargaSewa());
        }
    }

    private void tambahKendaraan() {
        try {
            System.out.println("\n--- MENAMBAH KENDARAAN ---");

            // Validasi jenis kendaraan
            String jenisKendaraan;
            while (true) {
                jenisKendaraan = input("Jenis Kendaraan (motor/mobil)").toLowerCase();
                if (jenisKendaraan.equals("motor") || jenisKendaraan.equals("mobil")) {
                    break;
                }
                System.out.println("Jenis kendaraan harus 'motor' atau 'mobil'.");
            }

            // Validasi nama kendaraan
            String namaKendaraan;
            while (true) {
                namaKendaraan = input("Nama Kendaraan");
                if (!namaKendaraan.trim().isEmpty()) {
                    break;
                }
                System.out.println("Nama kendaraan tidak boleh kosong.");
            }

            // Validasi plat nomor
            String platNomor;
            while (true) {
                platNomor = input("Plat Nomor");
                if (!platNomor.trim().isEmpty()) {
                    break;
                }
                System.out.println("Plat nomor tidak boleh kosong.");
            }

            // Validasi harga sewa
            double hargaSewa;
            while (true) {
                try {
                    hargaSewa = Double.parseDouble(input("Harga Sewa"));
                    if (hargaSewa > 0) {
                        break;
                    }
                    System.out.println("Harga sewa harus lebih dari 0.");
                } catch (NumberFormatException e) {
                    System.out.println("Masukkan harga sewa dengan angka yang valid.");
                }
            }

            rentalService.tambahKendaraanBaru(jenisKendaraan, namaKendaraan, platNomor, hargaSewa);
            System.out.println("Kendaraan berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("Gagal menambahkan kendaraan: " + e.getMessage());
        }
    }

    private void sewaKendaraan() {
        try {
            System.out.println("\n--- SEWA KENDARAAN ---");
            tampilkanKendaraanTersedia(); // Menampilkan daftar kendaraan yang tersedia

            // Validasi nomor kendaraan
            int nomor;
            while (true) {
                try {
                    nomor = Integer.parseInt(input("Nomor Kendaraan")); // Input nomor kendaraan
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Masukkan nomor kendaraan dengan angka yang valid.");
                }
            }
            boolean isAdmin=false;
            // Validasi no telepon penyewa
            while (this.noTelpon == null || this.noTelpon.trim().isEmpty()) {
                isAdmin=true;
                this.noTelpon = input("No Telepon Penyewa:").trim();
                if (this.noTelpon.isEmpty()) {
                    System.out.println("No telepon tidak boleh kosong.");
                }
            }

            // Validasi dan konversi tanggal sewa
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (true) {
                try {
                    String inputTanggal = input("Tanggal Sewa (dd-MM-yyyy)").trim();
                    Date parsedDate = inputFormat.parse(inputTanggal); // Parsing input ke format Date
                    this.date = mysqlFormat.format(parsedDate); // Format ulang ke format MySQL
                    break;
                } catch (ParseException e) {
                    System.out.println("Format tanggal tidak valid. Gunakan format dd-MM-yyyy.");
                }
            }

            // Memanggil rentalapp.service untuk menyewa kendaraan
            if (rentalService.sewaKendaraan(nomor, this.noTelpon, this.date)) {
                if(isAdmin){
                    System.out.println("Kendaraan berhasil disewa.");
                    this.noTelpon = "null";
                }
                this.date="null";
            } else {
                System.out.println("Gagal menyewa kendaraan. Pastikan nomor kendaraan valid dan tersedia.");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat menyewa kendaraan: " + e.getMessage());
        }
    }

    private void kembalikanKendaraan() {
        try {
            System.out.println("\n--- KEMBALIKAN KENDARAAN ---");

            // Validasi nomor kendaraan
            int nomor;
            while (true) {
                try {
                    nomor = Integer.parseInt(input("Nomor Kendaraan"));
                    break;
                } catch (NumberFormatException e) { System.out.println("Masukkan nomor kendaraan dengan angka yang valid.");
                }
            }

            if (rentalService.kembalikanKendaraan(nomor)) {
                System.out.println("Kendaraan berhasil dikembalikan.");
            } else {
                System.out.println("Gagal mengembalikan kendaraan. Pastikan nomor kendaraan valid dan sedang disewa.");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat mengembalikan kendaraan: " + e.getMessage());
        }
    }

    private void hapusKendaraan() {
        try {
            System.out.println("\n--- HAPUS KENDARAAN ---");

            // Validasi nomor kendaraan
            int nomor;
            while (true) {
                try {
                    nomor = Integer.parseInt(input("Nomor Kendaraan"));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Masukkan nomor kendaraan dengan angka yang valid.");
                }
            }

            if (rentalService.hapusKendaraan(nomor)) {
                System.out.println("Kendaraan berhasil dihapus.");
            } else {
                System.out.println("Gagal menghapus kendaraan. Pastikan nomor kendaraan valid.");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat menghapus kendaraan: " + e.getMessage());
        }
    }

    private String input(String info) {
        System.out.print(info + " : ");
        return scanner.nextLine();
    }
}