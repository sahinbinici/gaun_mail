package com.mobilewebservices.food.service;

import com.mobilewebservices.food.dto.DailyMenuDTO;
import com.mobilewebservices.food.repository.FoodProgramRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FoodProgramService {
    private final FoodProgramRepository foodProgramRepository;

    public FoodProgramService(FoodProgramRepository foodProgramRepository) {
        this.foodProgramRepository = foodProgramRepository;
    }

    @Cacheable(value = "food-menus", key = "'weekly-menu-' + T(java.time.LocalDate).now().toString()")
    public List<DailyMenuDTO> getFoodList(){
        return getLastWeek();
    }

    /**
     * Get food menu for a specific date
     * @param date The date to search for
     * @return List of daily menus for the specified date
     */
    @Cacheable(value = "food-menus", key = "'date-' + #date.toString()")
    public List<DailyMenuDTO> searchByDate(LocalDate date) {
        return foodProgramRepository.findDailyMenuByDate(date);
    }

    /**
     * Get food menus for the last 7 days (including today)
     * @return List of daily menus for the last week
     */
    @Cacheable(value = "food-menus", key = "'last-week-' + T(java.time.LocalDate).now().toString()")
    public List<DailyMenuDTO> getLastWeek() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days including today
        return foodProgramRepository.findDailyMenus(startDate, endDate);
    }

    /**
     * Get food menus for the last 30 days (including today)
     * @return List of daily menus for the last month
     */
    @Cacheable(value = "food-menus", key = "'last-month-' + T(java.time.LocalDate).now().toString()")
    public List<DailyMenuDTO> getLastMonth() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // Last 30 days including today
        return foodProgramRepository.findDailyMenus(startDate, endDate);
    }
}
