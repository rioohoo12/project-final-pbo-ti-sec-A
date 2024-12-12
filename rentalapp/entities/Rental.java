package rentalapp.entities;

public class Rental {
    private int id;
    private String jenisKendaraan; // "motor" atau "mobil"
    private String namaKendaraan;
    private String platNomor;
    private double hargaSewa;
    private boolean sedangDisewa;
    private String Tanggal;



    public Rental() {}

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTanggal() { return Tanggal;}
    public void setTanggal(String tanggal) {Tanggal = tanggal;}
    public String getJenisKendaraan() { return jenisKendaraan; }
    public void setJenisKendaraan(String jenisKendaraan) { this.jenisKendaraan = jenisKendaraan; }
    public String getNamaKendaraan() { return namaKendaraan; }
    public void setNamaKendaraan(String namaKendaraan) { this.namaKendaraan = namaKendaraan; }
    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public double getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(double hargaSewa) { this.hargaSewa = hargaSewa; }
    public boolean isSedangDisewa() { return sedangDisewa; }
    public void setSedangDisewa(boolean sedangDisewa) { this.sedangDisewa = sedangDisewa; }
}