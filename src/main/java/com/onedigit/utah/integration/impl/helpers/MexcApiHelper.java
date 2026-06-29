package com.onedigit.utah.integration.impl.helpers;

import com.onedigit.utah.config.IntegrationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MexcApiHelper {

    private final ThreadLocal<Mac> sha256HMAC;
    private final String recvWindow = "5000";
    private final static String HMACSHA256 = "HmacSHA256";

    public MexcApiHelper(IntegrationProperties integrationProperties) {
        String secret = Objects.requireNonNull(integrationProperties.mexcApiKeySecret());
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

    public Map<String, List<String>> signQueryString(Map<String, List<String>> paramsMap) {
        String timestamp = Long.toString(System.currentTimeMillis());
        Map<String, List<String>> signedParams = new LinkedHashMap<>(paramsMap);
        signedParams.put("timestamp", List.of(timestamp));
        signedParams.put("recvWindow", List.of(recvWindow));
        signedParams.put("signature", List.of(generateSignature(signedParams)));
        return signedParams;
    }

    public Map<String, List<String>> signQueryString() {
        return signQueryString(Map.of());
    }

    private String generateSignature(Map<String, List<String>> params) {
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
        return bytesToHex(sha256HMAC.get().doFinal(queryString.getBytes(StandardCharsets.UTF_8)));
    }

    private String bytesToHex(byte[] hash) {
        return HexFormat.of().formatHex(hash);
    }
}
