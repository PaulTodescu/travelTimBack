package com.travelTim.currency;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping(path = "/currency")
public class CurrencyController {

    @GetMapping(path = "/conversion/rate")
    public ResponseEntity<Float> getCurrencyConversionRate(
            @RequestParam(value = "fromCode") Currency fromCode,
            @RequestParam(value = "toCode") Currency toCode) throws IOException {
        CurrencyConverter currencyConverter = new CurrencyConverter();
        //Float conversionRate = currencyConverter.getCurrencyConversionRate(fromCode.name(), toCode.name());
        return new ResponseEntity<>(1F, HttpStatus.OK);
    }

}
