package in.deepak.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${expenseiq.brevo.api.key}")
    private String apiKey;

    @Value("${expenseiq.from.email}")
    private String fromEmail;

    @Value("${expenseiq.from.name}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String body) {
        try {
            // ----------------------------
            // Prepare payload
            // ----------------------------
            Map<String, Object> payload = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", fromName);
            sender.put("email", fromEmail);
            payload.put("sender", sender);

            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            payload.put("to", new Map[]{recipient});

            payload.put("subject", subject);
            payload.put("htmlContent", "<p>" + body + "</p>");

            // ----------------------------
            // Set headers
            // ----------------------------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // ----------------------------
            // Send request to Brevo API
            // ----------------------------
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("Mail sending failed: " + e.getMessage(), e);
        }
    }
}
