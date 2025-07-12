package com.minedu.gob.pe.mssigetip.util;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;

import java.io.*;

public class PDFSigner {

    public static byte[] firmarDocumento(byte[] pdfOriginal, String nombreUsuario) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfOriginal));
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            int numPaginas = pdfDoc.getNumberOfPages();
            PdfPage ultimaPagina = pdfDoc.getPage(numPaginas);

            Rectangle rect = new Rectangle(36, 36, 200, 100); // posición inferior izquierda
            Canvas canvas = new Canvas(ultimaPagina, rect);

            String fecha = java.time.LocalDateTime.now().toString();
            canvas.add(new Paragraph("Firmado por: " + nombreUsuario));
            canvas.add(new Paragraph("Fecha: " + fecha));

            canvas.close();
            pdfDoc.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
