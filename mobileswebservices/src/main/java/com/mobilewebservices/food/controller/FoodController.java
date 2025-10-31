package com.mobilewebservices.food.controller;

import com.mobilewebservices.food.service.FoodProgramService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/food")
public class FoodController {
    private final FoodProgramService foodProgramService;

    public FoodController(FoodProgramService foodProgramService) {
        this.foodProgramService = foodProgramService;
    }

    @GetMapping("/list")
    public Object getFoodList(){
        return foodProgramService.getFoodList();
    }

    /**
     * Search food menu by specific date
     * @param date Date in format YYYY-MM-DD (e.g., 2025-09-30)
     * @return Food menu for the specified date
     */
    @GetMapping("/search-by-date")
    public Object searchByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return foodProgramService.searchByDate(date);
    }

    /**
     * Get food menus for the last 7 days (including today)
     * @return List of daily menus for the last week
     */
    @GetMapping("/last-week")
    public Object getLastWeek() {
        return foodProgramService.getLastWeek();
    }

    /**
     * Get food menus for the last 30 days (including today)
     * @return List of daily menus for the last month
     */
    @GetMapping("/last-month")
    public Object getLastMonth() {
        return foodProgramService.getLastMonth();
    }
}
