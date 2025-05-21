package application;

public class BekleyenRezervasyon {
    private int id;
    private String ad;
    private String soyad;
    private String telefon;
    private String tarih;
    private String saat;
    private String kisiSayisi;
    private String notlar;

    public BekleyenRezervasyon(int id, String ad, String soyad, String telefon, java.sql.Date tarih, String saat, int kisiSayisi, String notlar) {
        this.id = id;
        this.ad = ad;
        this.soyad = soyad;
        this.telefon = telefon;
        this.tarih = tarih != null ? tarih.toString() : "";
        this.saat = saat;
        this.kisiSayisi = String.valueOf(kisiSayisi);
        this.notlar = notlar;
    }

    public int getId() { return id; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getTelefon() { return telefon; }
    public String getTarih() { return tarih; }
    public String getSaat() { return saat; }
    public String getKisiSayisi() { return kisiSayisi; }
    public String getNotlar() { return notlar; }
}  