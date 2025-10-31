package com.mobilewebservices.slider.controller;

import com.mobilewebservices.slider.dto.SliderDto;
import com.mobilewebservices.slider.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sliders")
public class SliderController {

    private final SliderService sliderService;

    @Autowired
    public SliderController(SliderService sliderService) {
        this.sliderService = sliderService;
    }

    @GetMapping
    public ResponseEntity<List<SliderDto>> getAllSliders() {
        List<SliderDto> sliders = sliderService.getAllSliders();
        return ResponseEntity.ok(sliders);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<SliderDto>> getLatestSliders() {
        List<SliderDto> sliders = sliderService.getLatest10Sliders();
        return ResponseEntity.ok(sliders);
    }

    @GetMapping("/last-month")
    public ResponseEntity<List<SliderDto>> getSlidersFromLastMonth() {
        List<SliderDto> sliders = sliderService.getSlidersFromLastMonth();
        return ResponseEntity.ok(sliders);
    }
}
