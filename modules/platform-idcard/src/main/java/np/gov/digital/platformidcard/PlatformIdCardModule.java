package np.gov.digital.platformidcard;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * PlatformIdCardModule
 *
 * Entry point for platform-idcard module.
 * Exposes:
 *   IdCardPdfGenerator  — iText7 PDF generation
 *   QrCodeService       — ZXing QR + HMAC-SHA256
 *   SparrowSmsService   — Nepal SMS gateway
 *   IdCardController    — 3 REST endpoints
 */
@Configuration
@ComponentScan(basePackages = "np.gov.digital.platformidcard")
public class PlatformIdCardModule {
}