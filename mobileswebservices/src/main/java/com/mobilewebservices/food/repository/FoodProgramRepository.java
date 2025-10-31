package com.mobilewebservices.food.repository;

import com.mobilewebservices.food.dto.DailyMenuDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class FoodProgramRepository {

    private final JdbcTemplate jdbcTemplate;

    public FoodProgramRepository(@Qualifier("defaultJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DailyMenuDTO> findDailyMenus(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                DATE_FORMAT(yp.tarih, '%Y-%m-%d') AS tarih,
                yl.ad AS yemek_adi
            FROM 
                yemek_program yp
            INNER JOIN 
                yemek_liste yl ON yp.yemek_ID = yl.ID
            WHERE 
                (? IS NULL OR yp.tarih >= ?)
                AND (? IS NULL OR yp.tarih <= ?)
            ORDER BY 
                yp.tarih DESC, yl.ad ASC
            """;

        // First, get all rows from the database
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            sql,
            startDate, startDate,
            endDate, endDate
        );

        // Then group them by date
        Map<LocalDate, DailyMenuDTO> menuMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            LocalDate date = LocalDate.parse((String) row.get("tarih"));
            String mealName = (String) row.get("yemek_adi");

            // Get or create the daily menu for this date
            DailyMenuDTO dailyMenu = menuMap.computeIfAbsent(date, DailyMenuDTO::new);

            // Add the meal to the daily menu
            dailyMenu.addMeal(mealName);
        }

        // Convert the map values to a list and return
        return new ArrayList<>(menuMap.values());
    }

    public List<DailyMenuDTO> findDailyMenuByDate(LocalDate date) {
        String sql = """
            SELECT 
                DATE_FORMAT(yp.tarih, '%Y-%m-%d') AS tarih,
                yl.ad AS yemek_adi
            FROM 
                yemek_program yp
            INNER JOIN 
                yemek_liste yl ON yp.yemek_ID = yl.ID
            WHERE 
                yp.tarih = ?
            ORDER BY 
                yl.ad ASC
            """;

        // First, get all rows for the date
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            sql,
            date
        );

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a single DailyMenuDTO for the date
        DailyMenuDTO dailyMenu = new DailyMenuDTO(date);
        for (Map<String, Object> row : rows) {
            String mealName = (String) row.get("yemek_adi");
            dailyMenu.addMeal(mealName);
        }

        return Collections.singletonList(dailyMenu);
    }
}
