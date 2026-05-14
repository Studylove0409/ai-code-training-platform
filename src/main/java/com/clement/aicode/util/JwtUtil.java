package com.clement.aicode.util;

import com.clement.aicode.common.BusinessException;
import com.clement.aicode.common.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expireSeconds;

    public JwtUtil(
            ObjectMapper objectMapper,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-minutes}") long expireMinutes
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expireSeconds = expireMinutes * 60;
    }

    public String generateToken(Long userId, String username) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("username", username);
            payload.put("exp", Instant.now().getEpochSecond() + expireSeconds);

            String headerText = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String payloadText = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String unsignedToken = headerText + "." + payloadText;
            String signature = sign(unsignedToken);

            return unsignedToken + "." + signature;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成 token 失败");
        }
    }

    public Long parseUserId(String token) {
        Map<String, Object> payload = parsePayload(token);
        Object userId = payload.get("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return Long.valueOf(userId.toString());
    }

    private Map<String, Object> parsePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8)
            )) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
            long exp = Long.parseLong(payload.get("exp").toString());
            if (Instant.now().getEpochSecond() > exp) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }

            return payload;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    private String sign(String text) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        mac.init(secretKeySpec);
        return base64UrlEncode(mac.doFinal(text.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
