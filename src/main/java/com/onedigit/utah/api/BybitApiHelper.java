package com.onedigit.utah.api;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BybitApiHelper {
    @Value("${bybit.api.key.value}")
    private String apiKey;

    @Value("${bybit.api.key.secret}")
    private String secret;

    private final String recv_window = "5000";

    @SneakyThrows
    public HttpHeaders buildHeadersWithSignature(Map<String, List<String>> paramsMap) {
        String timestamp = Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-BAPI-API-KEY", apiKey);
        headers.add("X-BAPI-SIGN", generateSignature(paramsMap, timestamp));
        headers.add("X-BAPI-TIMESTAMP", timestamp);
        headers.add("X-BAPI-RECV-WINDOW", recv_window);
        return headers;
    }

    private String generateSignature(Map<String, List<String>> params, String timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
        String stringToSign = timestamp + apiKey + recv_window + queryString;
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256HMAC.init(secretKey);
        return bytesToHex(sha256HMAC.doFinal(stringToSign.getBytes()));
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
