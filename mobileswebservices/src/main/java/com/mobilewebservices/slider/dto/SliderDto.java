package com.mobilewebservices.slider.dto;

import java.time.LocalDateTime;

public class SliderDto {

    private int id;
    private String resimyolu;
    private int sira;
    private String baslik;
    private String link;
    private String duyuruEtkinlikId;
    private int durum;
    private int lang;
    private LocalDateTime basTarih;
    private LocalDateTime bitTarih;
    private Integer ekleyenId;
    private String ekleyenIp;
    private LocalDateTime eklenmeTarih;

    // Getters and Setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResimyolu() {
        return resimyolu;
    }

    public void setResimyolu(String resimyolu) {
        this.resimyolu = resimyolu;
    }

    public int getSira() {
        return sira;
    }

    public void setSira(int sira) {
        this.sira = sira;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDuyuruEtkinlikId() {
        return duyuruEtkinlikId;
    }

    public void setDuyuruEtkinlikId(String duyuruEtkinlikId) {
        this.duyuruEtkinlikId = duyuruEtkinlikId;
    }

    public int getDurum() {
        return durum;
    }

    public void setDurum(int durum) {
        this.durum = durum;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public LocalDateTime getBasTarih() {
        return basTarih;
    }

    public void setBasTarih(LocalDateTime basTarih) {
        this.basTarih = basTarih;
    }

    public LocalDateTime getBitTarih() {
        return bitTarih;
    }

    public void setBitTarih(LocalDateTime bitTarih) {
        this.bitTarih = bitTarih;
    }

    public Integer getEkleyenId() {
        return ekleyenId;
    }

    public void setEkleyenId(Integer ekleyenId) {
        this.ekleyenId = ekleyenId;
    }

    public String getEkleyenIp() {
        return ekleyenIp;
    }

    public void setEkleyenIp(String ekleyenIp) {
        this.ekleyenIp = ekleyenIp;
    }

    public LocalDateTime getEklenmeTarih() {
        return eklenmeTarih;
    }

    public void setEklenmeTarih(LocalDateTime eklenmeTarih) {
        this.eklenmeTarih = eklenmeTarih;
    }
}
