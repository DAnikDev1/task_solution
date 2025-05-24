package src.danik.tasksolution.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import src.danik.tasksolution.entity.TelegramUser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class AuthService {
    @Value("${bot.token}")
    private String botToken;
    private static final ObjectMapper mapper = new ObjectMapper();

    public TelegramUser validate(String initData) {
        Map<String, String> data = parseInitData(initData);

        String hash = data.remove("hash");
        String dataCheckString = data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));

        byte[] secretKey = hmacSha256("WebAppData".getBytes(StandardCharsets.UTF_8), botToken.getBytes());
        byte[] expectedHashBytes = hmacSha256(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
        String expectedHash = bytesToHex(expectedHashBytes);

        if (!expectedHash.equalsIgnoreCase(hash)) {
            throw new SecurityException("Invalid hash");
        }
        String userJson = data.get("user");
        try {
            return mapper.readValue(userJson, TelegramUser.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse json to telegram user", e);
        }
    }

    private Map<String, String> parseInitData(String raw) {
        return Arrays.stream(raw.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(a -> a[0], a -> URLDecoder.decode(a[1], StandardCharsets.UTF_8)));
    }

    private byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
            mac.init(secretKeySpec);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating hmac", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String signUser(TelegramUser user) {
        return "test";
    }
}
