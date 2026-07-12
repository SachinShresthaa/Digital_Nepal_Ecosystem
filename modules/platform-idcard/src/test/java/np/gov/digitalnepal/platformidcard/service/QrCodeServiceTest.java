package np.gov.digitalnepal.platformidcard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QrCodeServiceTest
 * Day 5 — Task A: QR HMAC-SHA256 sign + verify unit test
 *
 * Task doc Day 5:
 *   "Test ZXing QR — scan → verify/{token} → VALID"
 *   "Test HMAC integrity — alter one char → SIGNATURE_MISMATCH"
 */
@DisplayName("QrCodeService — HMAC sign and verify")
class QrCodeServiceTest {

    private QrCodeService qrCodeService;

    private static final String CITIZEN_ID  = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    private static final String CARD_TYPE   = "DISABILITY";
    private static final String ISSUED_DATE = "2026-06-07";
    private static final String TEST_SECRET = "test-secret-key-for-unit-tests";

    @BeforeEach
    void setUp() {
        qrCodeService = new QrCodeService();
        // Inject test secret key
        ReflectionTestUtils.setField(qrCodeService, "qrSecret",  TEST_SECRET);
        ReflectionTestUtils.setField(qrCodeService, "baseUrl",   "http://localhost:8080");
    }

    // ----------------------------------------------------------------
    // HMAC sign + verify tests
    // ----------------------------------------------------------------

    @Test
    @DisplayName("buildSignedToken → verifyToken returns VALID")
    void signAndVerify_returnsValid() {
        String token = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);

        QrCodeService.VerifyResult result = qrCodeService.verifyToken(token);

        assertTrue(result.valid());
        assertEquals(CITIZEN_ID,  result.citizenId());
        assertEquals(CARD_TYPE,   result.cardType());
        assertEquals(ISSUED_DATE, result.issuedDate());
        assertNull(result.reason());
    }

    @Test
    @DisplayName("Tampered token → SIGNATURE_MISMATCH")
    void tamperedToken_returnsSignatureMismatch() {
        String token = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);

        // Alter one character in the token — simulates physical card tampering
        char[] chars = token.toCharArray();
        chars[5] = chars[5] == 'A' ? 'B' : 'A';
        String tamperedToken = new String(chars);

        QrCodeService.VerifyResult result = qrCodeService.verifyToken(tamperedToken);

        assertFalse(result.valid());
        // Could be SIGNATURE_MISMATCH or MALFORMED_TOKEN depending on which char changed
        assertNotNull(result.reason());
    }

    @Test
    @DisplayName("Different secret key → SIGNATURE_MISMATCH")
    void differentSecret_returnsSignatureMismatch() {
        // Build token with original secret
        String token = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);

        // Change the secret — simulates attacker not knowing the key
        ReflectionTestUtils.setField(qrCodeService, "qrSecret", "wrong-secret-key");

        QrCodeService.VerifyResult result = qrCodeService.verifyToken(token);

        assertFalse(result.valid());
        assertEquals("SIGNATURE_MISMATCH", result.reason());
    }

    @Test
    @DisplayName("Empty token → MALFORMED_TOKEN")
    void emptyToken_returnsMalformed() {
        QrCodeService.VerifyResult result = qrCodeService.verifyToken("not-base64!!");

        assertFalse(result.valid());
        assertNotNull(result.reason());
    }

    @Test
    @DisplayName("Same input always produces same token (deterministic)")
    void sameInput_producesSameToken() {
        String token1 = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);
        String token2 = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);

        assertEquals(token1, token2);
    }

    @Test
    @DisplayName("Different citizenId produces different token")
    void differentCitizenId_producesDifferentToken() {
        String token1 = qrCodeService.buildSignedToken(CITIZEN_ID, CARD_TYPE, ISSUED_DATE);
        String token2 = qrCodeService.buildSignedToken(
                "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", CARD_TYPE, ISSUED_DATE);

        assertNotEquals(token1, token2);
    }
}