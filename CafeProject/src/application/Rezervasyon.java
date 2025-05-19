package application;

import java.time.LocalDate;

public class Rezervasyon {
    private String ad;
    private String soyad;
    private String telefon;
    private LocalDate tarih;
    private String saat;
    private int kisiSayisi;
    private String notlar;

    public Rezervasyon(String ad, String soyad, String telefon, LocalDate tarih, String saat, int kisiSayisi, String notlar) {
        this.ad = ad;
        this.soyad = soyad;
        this.telefon = telefon;
        this.tarih = tarih;
        this.saat = saat;
        this.kisiSayisi = kisiSayisi;
        this.notlar = notlar;
    }

    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getTelefon() { return telefon; }
    public LocalDate getTarih() { return tarih; }
    public String getSaat() { return saat; }
    public int getKisiSayisi() { return kisiSayisi; }
    public String getNotlar() { return notlar; }
} 