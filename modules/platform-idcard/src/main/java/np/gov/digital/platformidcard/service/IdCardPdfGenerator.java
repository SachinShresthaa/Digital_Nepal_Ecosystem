package np.gov.digital.platformidcard.service;

import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * IdCardPdfGenerator
 * Generates government ID card PDFs using iText7.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdCardPdfGenerator {

    private final QrCodeService qrCodeService;

    public byte[] generateIdCard(String citizenId,
                                 String nameNp,
                                 String nameEn,
                                 String citizenshipNumber,
                                 String cardType,
                                 String wardNumber,
                                 String issuedDate,
                                 String expiryDate) throws WriterException, IOException {

        log.info("IdCardPdfGenerator: generating {} card for citizen={}", cardType, citizenId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter   writer      = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document    document    = new Document(pdfDocument, PageSize.A6.rotate());

        document.setMargins(15, 15, 15, 15);

        addHeader(document, cardType);

        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{35, 65}))
                .setWidth(UnitValue.createPercentValue(100));

        mainTable.addCell(buildPhotoCell());
        mainTable.addCell(buildDetailsCell(
                nameNp, nameEn, citizenshipNumber,
                wardNumber, issuedDate, expiryDate));

        document.add(mainTable);

        addQrCode(document, citizenId, cardType, issuedDate);

        document.close();

        log.info("IdCardPdfGenerator: PDF generated for citizen={}", citizenId);
        return outputStream.toByteArray();
    }

    private void addHeader(Document document, String cardType) {
        String cardTitle = switch (cardType) {
            case "DISABILITY"   -> "Disability Identity Card / अपाङ्गता परिचय पत्र";
            case "UNEMPLOYMENT" -> "Unemployment Identity Card / बेरोजगार परिचय पत्र";
            default             -> cardType + " Identity Card";
        };

        document.add(new Paragraph("Kummayak Rural Municipality / कुम्मायक गाउँपालिका")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setBold()
                .setFontColor(new DeviceRgb(0, 51, 102)));

        document.add(new Paragraph(cardTitle)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY));
    }

    private Cell buildPhotoCell() {
        Cell cell = new Cell()
                .setBorder(null)
                .setPadding(5);

        // Photo placeholder — replaced with real photo when available
        cell.add(new Paragraph("[PHOTO]")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setWidth(80)
                .setHeight(90));

        return cell;
    }

    private Cell buildDetailsCell(String nameNp,
                                  String nameEn,
                                  String citizenshipNumber,
                                  String wardNumber,
                                  String issuedDate,
                                  String expiryDate) {
        Cell cell = new Cell().setBorder(null).setPadding(5);

        cell.add(new Paragraph(nameNp).setFontSize(11).setBold());
        cell.add(new Paragraph(nameEn).setFontSize(9));
        cell.add(new Paragraph(" "));
        cell.add(new Paragraph("Citizenship No: " + citizenshipNumber).setFontSize(8));
        cell.add(new Paragraph("Ward No: " + wardNumber).setFontSize(8));
        cell.add(new Paragraph("Issued: " + issuedDate).setFontSize(8));
        cell.add(new Paragraph("Expires: " + expiryDate).setFontSize(8));

        return cell;
    }

    private void addQrCode(Document document,
                           String citizenId,
                           String cardType,
                           String issuedDate) throws WriterException, IOException {
        byte[] qrBytes = qrCodeService.generateQrCode(citizenId, cardType, issuedDate);

        Image qrImage = new Image(ImageDataFactory.create(qrBytes))
                .setWidth(60)
                .setHeight(60)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        document.add(new Paragraph("Scan to verify / स्क्यान गर्नुहोस्")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY));

        document.add(qrImage);
    }
}
