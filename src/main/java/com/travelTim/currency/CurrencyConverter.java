package com.travelTim.currency;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyConverter {

    private static final String API_KEY = "1e23c37d20db463a80fea05d54befd27";

    public CurrencyConverter() {
    }

    public Float getCurrencyConversionRate(String fromCode, String toCode) throws IOException {
        String REQUEST_URL = "https://api.currconv.com/api/v7/convert?q=" +
                fromCode + '_' + toCode + "&compact=ultra&apiKey=" + API_KEY;
        URL url = new URL(REQUEST_URL);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null){
                response.append(inputLine);
            }
            bufferedReader.close();
        }
        JSONObject jsonObject = new JSONObject(response.toString());
        String key = fromCode + "_" + toCode;
        return jsonObject.getFloat(key);
    }

    public Float getConvertedPrice(Float amount, Float conversionRate){
        Float convertedPrice = amount * conversionRate;
        DecimalFormatSymbols separator = new DecimalFormatSymbols();
        separator.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00", separator);
        return Float.parseFloat(decimalFormat.format(convertedPrice));
    }

}
