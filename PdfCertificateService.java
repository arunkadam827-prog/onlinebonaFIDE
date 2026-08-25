package com.college.bonafide.service;

import com.college.bonafide.model.BonafideRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfCertificateService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public byte[] generateCertificate(BonafideRequest request) throws DocumentException {
        Document document = new Document(PageSize.A4, 60, 60, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

        Paragraph collegeName = new Paragraph("XYZ COLLEGE OF ENGINEERING", headerFont);
        collegeName.setAlignment(Element.ALIGN_CENTER);
        document.add(collegeName);

        Paragraph address = new Paragraph("Affiliated to State University | Accredited Institution", smallFont);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        document.add(new Paragraph(" "));
        LineSeparator line = new LineSeparator();
        document.add(line);
        document.add(new Paragraph(" "));

        Paragraph title = new Paragraph("BONAFIDE CERTIFICATE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        String certNumber = "BC-" + request.getId() + "-" + request.getRequestedAt().getYear();
        Paragraph certNo = new Paragraph("Certificate No: " + certNumber, smallFont);
        document.add(certNo);
        document.add(new Paragraph(" "));

        String body = String.format(
            "This is to certify that Mr./Ms. %s, bearing Roll Number %s, is a bonafide student of " +
            "the %s Department, currently studying in %s of this institution.",
            request.getStudent().getName(),
            request.getStudent().getRollNumber(),
            request.getStudent().getDepartment(),
            request.getStudent().getYear()
        );

        Paragraph bodyPara = new Paragraph(body, bodyFont);
        bodyPara.setAlignment(Element.ALIGN_JUSTIFIED);
        bodyPara.setSpacingAfter(15);
        document.add(bodyPara);

        String purposeLine = String.format(
            "This certificate is issued upon the student's request for the purpose of: %s.",
            request.getPurpose()
        );
        Paragraph purposePara = new Paragraph(purposeLine, bodyFont);
        purposePara.setAlignment(Element.ALIGN_JUSTIFIED);
        purposePara.setSpacingAfter(25);
        document.add(purposePara);

        Paragraph closing = new Paragraph(
            "We wish the student success in all future endeavours.", bodyFont);
        closing.setSpacingAfter(40);
        document.add(closing);

        // Signature block
        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        PdfPCell dateCell = new PdfPCell(new Phrase(
            "Date of Issue: " + request.getProcessedAt().format(DATE_FMT), bodyFont));
        dateCell.setBorder(Rectangle.NO_BORDER);
        sigTable.addCell(dateCell);

        PdfPCell sigCell = new PdfPCell(new Phrase("Principal / Registrar\n(Authorized Signatory)", bodyFont));
        sigCell.setBorder(Rectangle.NO_BORDER);
        sigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sigTable.addCell(sigCell);

        document.add(sigTable);

        document.close();
        return out.toByteArray();
    }
}
