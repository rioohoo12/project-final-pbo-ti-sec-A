package rentalapp.service;

import rentalapp.entities.Rental;

public interface RentalService {
    Rental[] daftarKendaraan();
    Rental[] kendaraanTersedia();
    void tambahKendaraanBaru(String jenisKendaraan, String namaKendaraan, String platNomor, double hargaSewa);
    Boolean hapusKendaraan(Integer nomor);
    Boolean sewaKendaraan(Integer nomor, String noTelpon, String date);
    Boolean kembalikanKendaraan(Integer nomor);
    Boolean login(String noTelpon);
    Boolean daftar(String noTelpon,String nama);
    Rental[] historySewa(String noTelpon);
    Rental[] StatusSewa(String noTelpon);
    Boolean Batalsewa(Integer id);
    String PendapatanSebulan();
}