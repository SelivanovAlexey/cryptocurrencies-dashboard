package com.onedigit.utah.integration.impl.helpers;

import com.onedigit.utah.config.IntegrationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BybitApiHelper {

    private final String apiKey;
    private final ThreadLocal<Mac> sha256HMAC;
    private final String recvWindow = "5000";
    private final static String HMACSHA256 = "HmacSHA256";

    public BybitApiHelper(IntegrationProperties integrationProperties) {
        this.apiKey = integrationProperties.bybitApikeyValue();
        String secret = Objects.requireNonNull(integrationProperties.bybitApiKeySecret());
        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMACSHA256);
        this.sha256HMAC = ThreadLocal.withInitial(() -> {
            try {
                Mac mac = Mac.getInstance(HMACSHA256);
                mac.init(secretKey);
                return mac;
            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                throw new IllegalStateException("Cannot initialize HMAC", e);
            }

        });
    }

    public HttpHeaders applySignedHeaders(Map<String, List<String>> paramsMap) {
        String timestamp = Long.toString(System.currentTimeMillis());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-BAPI-API-KEY", apiKey);
        headers.add("X-BAPI-SIGN", generateSignature(paramsMap, timestamp));
        headers.add("X-BAPI-TIMESTAMP", timestamp);
        headers.add("X-BAPI-RECV-WINDOW", recvWindow);
        return headers;
    }

    public HttpHeaders applySignedHeaders() {
        return applySignedHeaders(Map.of());
    }

    private String generateSignature(Map<String, List<String>> params, String timestamp) {
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
        String stringToSign = timestamp + apiKey + recvWindow + queryString;
        return bytesToHex(sha256HMAC.get().doFinal(stringToSign.getBytes()));
    }

    private String bytesToHex(byte[] hash) {
        return HexFormat.of().formatHex(hash);
    }
}
