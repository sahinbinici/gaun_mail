package com.mobilewebservices.slider.repository;
import com.mobilewebservices.slider.dto.SliderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class SliderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SliderRepository(@Qualifier("defaultJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SliderDto> findAll() {
        // Tablo ve sütun adlarının doğru olduğundan emin olun.
        String sql = "SELECT id, resimyolu, sira, baslik, link, duyuru_etkinlik_id as duyuruEtkinlikId, " +
                     "durum, lang, bas_tarih as basTarih, bit_tarih as bitTarih, ekleyen_id as ekleyenId, " +
                     "ekleyen_ip as ekleyenIp, eklenme_tarih as eklenmeTarih FROM slider ORDER BY sira ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SliderDto.class));
    }

    public List<SliderDto> findLatest10() {
        String sql = "SELECT id, resimyolu, sira, baslik, link, duyuru_etkinlik_id as duyuruEtkinlikId, " +
                     "durum, lang, bas_tarih as basTarih, bit_tarih as bitTarih, ekleyen_id as ekleyenId, " +
                     "ekleyen_ip as ekleyenIp, eklenme_tarih as eklenmeTarih FROM slider " +
                     "ORDER BY eklenme_tarih DESC LIMIT 10";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SliderDto.class));
    }

    public List<SliderDto> findFromLastMonth() {
        String sql = "SELECT id, resimyolu, sira, baslik, link, duyuru_etkinlik_id as duyuruEtkinlikId, " +
                     "durum, lang, bas_tarih as basTarih, bit_tarih as bitTarih, ekleyen_id as ekleyenId, " +
                     "ekleyen_ip as ekleyenIp, eklenme_tarih as eklenmeTarih FROM slider " +
                     "WHERE eklenme_tarih >= ? ORDER BY eklenme_tarih DESC";
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SliderDto.class), oneMonthAgo);
    }
}
