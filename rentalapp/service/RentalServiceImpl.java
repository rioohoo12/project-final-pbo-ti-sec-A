package rentalapp.service;

import org.springframework.stereotype.Component;
import rentalapp.entities.Rental;
import rentalapp.repositories.RentalRepository;

@Component
public class RentalServiceImpl implements rentalapp.service.RentalService {
    private final RentalRepository rentalRepository;

    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public Rental[] daftarKendaraan() {
        return rentalRepository.getAllKendaraan();
    }

    @Override
    public Rental[] kendaraanTersedia() {
        return rentalRepository.getKendaraanTersedia();
    }

    @Override
    public void tambahKendaraanBaru(String jenisKendaraan, String namaKendaraan,
                                    String platNomor, double hargaSewa) {
        Rental kendaraan = new Rental();
        kendaraan.setJenisKendaraan(jenisKendaraan);
        kendaraan.setNamaKendaraan(namaKendaraan);
        kendaraan.setPlatNomor(platNomor);
        kendaraan.setHargaSewa(hargaSewa);
        kendaraan.setSedangDisewa(false);
        rentalRepository.tambahKendaraan(kendaraan);
    }

    @Override
    public Boolean hapusKendaraan(Integer nomor) {
        return rentalRepository.hapusKendaraan(nomor);
    }

    @Override
    public Boolean sewaKendaraan(Integer nomor, String noTelpon, String date) {
        return rentalRepository.sewaKendaraan(nomor, noTelpon,date);
    }

    @Override
    public Boolean kembalikanKendaraan(Integer nomor) {
        return rentalRepository.kembalikanKendaraan(nomor);
    }

    @Override
    public Boolean login(String noTelpon) {
        return rentalRepository.login(noTelpon);
    }

    @Override
    public Boolean daftar(String noTelpon, String nama) {
        return rentalRepository.daftar(noTelpon,nama);
    }

    @Override
    public Rental[] historySewa(String noTelpon) {
        return rentalRepository.historySewa(noTelpon);
    }

    @Override
    public Rental[] StatusSewa(String noTelpon) {
        return rentalRepository.statusSewa(noTelpon);
    }

    @Override
    public Boolean Batalsewa(Integer id) {
        return rentalRepository.BatalSewa(id);
    }

    @Override
    public String PendapatanSebulan() {
        return rentalRepository.PendapatanSebulan();
    }
}