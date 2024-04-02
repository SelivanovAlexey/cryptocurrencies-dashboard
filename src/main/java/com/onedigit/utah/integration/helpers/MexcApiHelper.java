package com.onedigit.utah.integration.helpers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MexcApiHelper {
    @Value("${mexc.api.key.value}")
    private String apiKey;

    @Value("${mexc.api.key.secret}")
    private String secret;

    private final String recv_window = "5000";

    @SneakyThrows
    public MultiValueMap<String, String> buildParamsWithSignature() {
        MultiValueMap<String, String> params = CollectionUtils.toMultiValueMap(new HashMap<>());
        ZonedDateTime timestamp = ZonedDateTime.now();
        params.put("timestamp", List.of(Long.toString(timestamp.toInstant().toEpochMilli())));
        params.put("recvWindow", List.of(recv_window));
        params.put("signature", List.of(generateSignature(params)));
        return params;
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MEXC-APIKEY", apiKey);
        return headers;
    }


    private String generateSignature(Map<String, List<String>> params) throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
        String stringToSign = queryString;
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
