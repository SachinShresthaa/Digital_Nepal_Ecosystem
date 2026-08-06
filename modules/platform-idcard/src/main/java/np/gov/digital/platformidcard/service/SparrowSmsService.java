package np.gov.digital.platformidcard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SparrowSmsService {

    @Value("${sparrow.token:test-token}")
    private String sparrowToken;

    @Value("${sparrow.from:DigitalNepal}")
    private String senderName;

    private static final String SPARROW_API_URL =
            "http://api.sparrowsms.com/v2/sms/";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sends an SMS to a Nepal mobile number.
     * @param mobileNumber Nepal mobile e.g. 9841XXXXXX
     * @param message      SMS text (keep under 160 chars for single SMS)
     */
    public void sendSms(String mobileNumber, String message) {
        try {
            log.info("SparrowSmsService: sending SMS to {}", mobileNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("token",  sparrowToken);
            body.put("from",   senderName);
            body.put("to",     mobileNumber);
            body.put("text",   message);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.postForObject(SPARROW_API_URL, request, String.class);

            log.info("SparrowSmsService: SMS sent successfully to {}", mobileNumber);

        } catch (Exception e) {
            // NEVER let SMS failure break the main flow
            // Card is still APPROVED even if SMS fails
            log.error("SparrowSmsService: SMS failed to {} — {}",
                    mobileNumber, e.getMessage());
        }
    }

    /**
     * Sends ID card ready notification.
     * Called by IdCardController when LOCAL_BODY_ADMIN approves.
     */
    public void sendIdCardReadyNotification(String mobileNumber,
                                            String cardType,
                                            String wardNumber) {
        String message = "Your " + formatCardType(cardType) +
                " ID card is ready for collection at Ward " +
                wardNumber + " office. - Kummayak Rural Municipality";
        sendSms(mobileNumber, message);
    }

    /**
     * Sends grievance tracking number to citizen.
     */
    public void sendGrievanceTrackingNumber(String mobileNumber,
                                            String trackingNumber) {
        String message = "Your grievance has been received. " +
                "Tracking number: " + trackingNumber +
                ". - Kummayak Rural Municipality";
        sendSms(mobileNumber, message);
    }


    private String formatCardType(String cardType) {
        return switch (cardType) {
            case "DISABILITY"   -> "Disability";
            case "UNEMPLOYMENT" -> "Unemployment";
            case "SENIOR"       -> "Senior Citizen";
            default             -> cardType;
        };
    }
}
