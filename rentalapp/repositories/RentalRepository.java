package rentalapp.repositories;

import rentalapp.entities.Rental;

public interface RentalRepository {
    Rental[] getAllKendaraan();
    Rental[] getKendaraanTersedia();
    void tambahKendaraan(Rental kendaraan);
    Boolean hapusKendaraan(Integer id);
    Boolean sewaKendaraan(Integer id, String noTelp, String date);
    Boolean kembalikanKendaraan(Integer id);
    Boolean login(String noTelpon);
    Boolean daftar(String noTelpon,String nama);
    Rental[] historySewa(String noTelpon);
    Rental[] statusSewa(String noTelpon);
    Boolean BatalSewa(Integer id);

    String PendapatanSebulan();
}