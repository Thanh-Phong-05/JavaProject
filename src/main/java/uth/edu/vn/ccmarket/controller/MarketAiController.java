package uth.edu.vn.ccmarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import uth.edu.vn.ccmarket.service.PriceSuggestionService;

@RestController
public class MarketAiController {

    private final PriceSuggestionService priceSuggestionService;

    public MarketAiController(PriceSuggestionService priceSuggestionService) {
        this.priceSuggestionService = priceSuggestionService;
    }

    @GetMapping("/market/ai/suggest")
    public double suggestPricePerCredit() {
        return priceSuggestionService.suggestPricePerCredit();
    }
}
