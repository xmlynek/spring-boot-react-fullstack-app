package com.filip.managementapp.validation;

import com.filip.managementapp.dto.ProductRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    public String[] getParamsFromProductRequest(ProductRequest request) {
        return new String[]{
                "name", request.name(),
                "description", request.description(),
                "shortDescription", request.shortDescription(),
                "quantity", request.quantity().toString(),
                "price", request.price().toString(),
                "isAvailable", request.isAvailable().toString(),
        };
    }

    // method taken from https://stackoverflow.com/a/13401094
    public String buildUrlEncodedFormEntity(String... params) {
        if( (params.length % 2) > 0 ) {
            throw new IllegalArgumentException("Need to give an even number of parameters");
        }
        StringBuilder result = new StringBuilder();
        for (int i=0; i<params.length; i+=2) {
            if( i > 0 ) {
                result.append('&');
            }
            result.
                    append(URLEncoder.encode(params[i], StandardCharsets.UTF_8)).
                    append('=').
                    append(URLEncoder.encode(params[i+1], StandardCharsets.UTF_8));
        }
        return result.toString();
    }
}
