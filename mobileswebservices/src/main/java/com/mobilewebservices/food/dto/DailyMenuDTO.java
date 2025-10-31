package com.mobilewebservices.food.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DTO for daily food program
 */
public class DailyMenuDTO {
    private String id;
    private String day;
    private String date;
    private List<String> meals;

    // Turkish locale for day names
    private static final Locale TR_LOCALE = new Locale("tr", "TR");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Constructors
    public DailyMenuDTO() {
        this.meals = new ArrayList<>();
    }

    public DailyMenuDTO(LocalDate localDate) {
        this.meals = new ArrayList<>();
        if (localDate != null) {
            setDateFromLocalDate(localDate);
        }
    }

    public DailyMenuDTO(String id, String day, String date) {
        this.id = id;
        this.day = day;
        this.date = date;
        this.meals = new ArrayList<>();
    }

    // Helper method to set date, day, and id from LocalDate
    public void setDateFromLocalDate(LocalDate localDate) {
        if (localDate != null) {
            // Set ID (YYYYMMDD format)
            this.id = localDate.toString().replace("-", "");

            // Set day name in Turkish (Pazartesi, Salı, etc.)
            this.day = localDate.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, TR_LOCALE);
            // Capitalize first letter
            this.day = this.day.substring(0, 1).toUpperCase(TR_LOCALE) + this.day.substring(1);

            // Set date in dd.MM.yyyy format
            this.date = localDate.format(DATE_FORMATTER);
        }
    }

    // Getter and Setter methods
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<String> getMeals() { return meals; }
    public void setMeals(List<String> meals) { this.meals = meals; }

    // Helper method to add meal
    public void addMeal(String mealName) {
        if (this.meals == null) {
            this.meals = new ArrayList<>();
        }
        this.meals.add(mealName);
    }
}
