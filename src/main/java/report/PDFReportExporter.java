package report;

import exception.FileProcessingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Exports management reports as professionally
 * formatted PDF documents.
 *
 * PDF reports are output documents and therefore
 * remain separate from the FileManager persistence
 * hierarchy.
 */
public class PDFReportExporter {

    private static final float PAGE_MARGIN = 50;
    private static final float FOOTER_HEIGHT = 35;

    private static final float TITLE_SIZE = 17;
    private static final float SUBTITLE_SIZE = 9;
    private static final float SECTION_SIZE = 11;
    private static final float BODY_SIZE = 10;
    private static final float TABLE_SIZE = 8.5f;

    private static final float BODY_LINE_HEIGHT = 15;
    private static final float SECTION_SPACING = 22;

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy HH:mm:ss"
            );

    private static final Set<String> SECTION_HEADERS =
            Set.of(
                    "SUMMARY",
                    "TICKET TYPE STATISTICS",
                    "ROUTE POPULARITY",
                    "FARE STATISTICS",
                    "CANCELLED TICKETS",
                    "TICKET DETAILS"
            );

    private final PDFont regularFont;
    private final PDFont boldFont;

    public PDFReportExporter() {

        regularFont =
                new PDType1Font(
                        Standard14Fonts.FontName.HELVETICA
                );

        boldFont =
                new PDType1Font(
                        Standard14Fonts.FontName.HELVETICA_BOLD
                );
    }

    /**
     * Exports a report to a professionally formatted PDF.
     */
    public void exportReport(
            ArrayList<String> reportLines,
            String reportTitle,
            String fileName)
            throws FileProcessingException {

        validateInput(
                reportLines,
                reportTitle,
                fileName
        );

        try {

            Path outputPath =
                    Paths.get(
                            fileName
                    );

            Path parent =
                    outputPath.getParent();

            if (parent != null) {

                Files.createDirectories(
                        parent
                );
            }

            String generatedTime =
                    LocalDateTime.now()
                            .format(
                                    DATE_TIME_FORMAT
                            );

            try (PDDocument document =
                         new PDDocument()) {

                int pageNumber =
                        1;

                PageContext page =
                        createPage(
                                document,
                                reportTitle,
                                generatedTime,
                                pageNumber,
                                false
                        );

                String currentSection =
                        "";

                boolean tableHeaderDrawn =
                        false;

                for (String originalLine :
                        reportLines) {

                    String line =
                            originalLine == null
                                    ? ""
                                    : originalLine.trim();

                    /*
                     * The PDF already has its own professional
                     * title and generated timestamp, so remove
                     * duplicate console-report headings.
                     */
                    if (isRedundantHeaderLine(
                            line
                    )) {

                        continue;
                    }

                    /*
                     * Blank line = small visual spacing.
                     */
                    if (line.isBlank()) {

                        page.yPosition -=
                                6;

                        continue;
                    }

                    /*
                     * Section heading.
                     */
                    if (SECTION_HEADERS.contains(
                            line
                    )) {

                        page =
                                ensureSpace(
                                        document,
                                        page,
                                        SECTION_SPACING + 15,
                                        reportTitle,
                                        generatedTime
                                );

                        currentSection =
                                line;

                        tableHeaderDrawn =
                                false;

                        renderSectionHeader(
                                page,
                                line
                        );

                        continue;
                    }

                    /*
                     * Ticket-detail records use |
                     * and are displayed as aligned tables.
                     */
                    if (line.contains("|")) {

                        page =
                                ensureSpace(
                                        document,
                                        page,
                                        38,
                                        reportTitle,
                                        generatedTime
                                );

                        if (!tableHeaderDrawn) {

                            renderTicketTableHeader(
                                    page
                            );

                            tableHeaderDrawn =
                                    true;
                        }

                        page =
                                ensureSpace(
                                        document,
                                        page,
                                        20,
                                        reportTitle,
                                        generatedTime
                                );

                        renderTicketTableRow(
                                page,
                                line
                        );

                        continue;
                    }

                    /*
                     * Lines containing ":" are displayed
                     * as aligned label/value rows.
                     */
                    if (line.contains(":")) {

                        page =
                                ensureSpace(
                                        document,
                                        page,
                                        BODY_LINE_HEIGHT,
                                        reportTitle,
                                        generatedTime
                                );

                        renderKeyValueRow(
                                page,
                                line
                        );

                        continue;
                    }

                    /*
                     * Ordinary text such as
                     * "No cancelled tickets."
                     */
                    page =
                            renderBodyText(
                                    document,
                                    page,
                                    line,
                                    reportTitle,
                                    generatedTime
                            );
                }

                closePage(
                        page
                );

                document.save(
                        outputPath.toFile()
                );
            }

        } catch (IOException
                 | IllegalArgumentException e) {

            throw new FileProcessingException(
                    "Unable to export PDF report: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Validates PDF export input.
     */
    private void validateInput(
            ArrayList<String> reportLines,
            String reportTitle,
            String fileName)
            throws FileProcessingException {

        if (reportLines == null) {

            throw new FileProcessingException(
                    "Report data cannot be null."
            );
        }

        if (reportTitle == null
                || reportTitle.isBlank()) {

            throw new FileProcessingException(
                    "Report title cannot be blank."
            );
        }

        if (fileName == null
                || fileName.isBlank()) {

            throw new FileProcessingException(
                    "PDF file name cannot be blank."
            );
        }
    }

    /**
     * Creates a new professional report page.
     */
    private PageContext createPage(
            PDDocument document,
            String reportTitle,
            String generatedTime,
            int pageNumber,
            boolean continued)
            throws IOException {

        PDPage page =
                new PDPage(
                        PDRectangle.A4
                );

        document.addPage(
                page
        );

        PDPageContentStream stream =
                new PDPageContentStream(
                        document,
                        page
                );

        float pageWidth =
                page.getMediaBox()
                        .getWidth();

        float pageHeight =
                page.getMediaBox()
                        .getHeight();

        /*
         * Main title.
         */
        drawText(
                stream,
                boldFont,
                TITLE_SIZE,
                PAGE_MARGIN,
                pageHeight - PAGE_MARGIN,
                fitText(
                        reportTitle,
                        boldFont,
                        TITLE_SIZE,
                        pageWidth - (PAGE_MARGIN * 2)
                )
        );

        /*
         * Secondary heading.
         */
        String subtitle =
                continued
                        ? "Management Report - Continued"
                        : "Management Report";

        drawText(
                stream,
                regularFont,
                SUBTITLE_SIZE,
                PAGE_MARGIN,
                pageHeight - PAGE_MARGIN - 20,
                subtitle
        );

        /*
         * Generated date/time aligned to the right.
         */
        String generatedLabel =
                "Generated: "
                        + generatedTime;

        float generatedWidth =
                getTextWidth(
                        generatedLabel,
                        regularFont,
                        SUBTITLE_SIZE
                );

        drawText(
                stream,
                regularFont,
                SUBTITLE_SIZE,
                pageWidth
                        - PAGE_MARGIN
                        - generatedWidth,
                pageHeight
                        - PAGE_MARGIN
                        - 20,
                generatedLabel
        );

        /*
         * Header separator.
         */
        stream.setLineWidth(
                1.0f
        );

        stream.moveTo(
                PAGE_MARGIN,
                pageHeight
                        - PAGE_MARGIN
                        - 31
        );

        stream.lineTo(
                pageWidth
                        - PAGE_MARGIN,
                pageHeight
                        - PAGE_MARGIN
                        - 31
        );

        stream.stroke();

        return new PageContext(
                document,
                page,
                stream,
                pageNumber,
                pageHeight
                        - PAGE_MARGIN
                        - 55
        );
    }

    /**
     * Creates another page if there is not
     * enough remaining vertical space.
     */
    private PageContext ensureSpace(
            PDDocument document,
            PageContext page,
            float requiredHeight,
            String reportTitle,
            String generatedTime)
            throws IOException {

        if (page.yPosition
                - requiredHeight
                > FOOTER_HEIGHT + PAGE_MARGIN) {

            return page;
        }

        int nextPageNumber =
                page.pageNumber
                        + 1;

        closePage(
                page
        );

        return createPage(
                document,
                reportTitle,
                generatedTime,
                nextPageNumber,
                true
        );
    }

    /**
     * Renders a clean section heading.
     */
    private void renderSectionHeader(
            PageContext page,
            String sectionName)
            throws IOException {

        page.yPosition -=
                6;

        drawText(
                page.stream,
                boldFont,
                SECTION_SIZE,
                PAGE_MARGIN,
                page.yPosition,
                sectionName
        );

        page.yPosition -=
                7;

        page.stream.setLineWidth(
                0.6f
        );

        page.stream.moveTo(
                PAGE_MARGIN,
                page.yPosition
        );

        page.stream.lineTo(
                page.page
                        .getMediaBox()
                        .getWidth()
                        - PAGE_MARGIN,
                page.yPosition
        );

        page.stream.stroke();

        page.yPosition -=
                14;
    }

    /**
     * Renders an aligned:
     *
     * Label             Value
     *
     * row instead of printing raw console text.
     */
    private void renderKeyValueRow(
            PageContext page,
            String line)
            throws IOException {

        int separatorIndex =
                line.indexOf(
                        ':'
                );

        if (separatorIndex < 0) {

            return;
        }

        String label =
                line.substring(
                                0,
                                separatorIndex
                        )
                        .trim();

        String value =
                line.substring(
                                separatorIndex + 1
                        )
                        .trim();

        float labelX =
                PAGE_MARGIN + 8;

        float valueX =
                PAGE_MARGIN + 225;

        float labelWidth =
                valueX
                        - labelX
                        - 15;

        float valueWidth =
                page.page
                        .getMediaBox()
                        .getWidth()
                        - PAGE_MARGIN
                        - valueX;

        drawText(
                page.stream,
                regularFont,
                BODY_SIZE,
                labelX,
                page.yPosition,
                fitText(
                        label,
                        regularFont,
                        BODY_SIZE,
                        labelWidth
                )
        );

        drawText(
                page.stream,
                boldFont,
                BODY_SIZE,
                valueX,
                page.yPosition,
                fitText(
                        value,
                        boldFont,
                        BODY_SIZE,
                        valueWidth
                )
        );

        page.yPosition -=
                BODY_LINE_HEIGHT;
    }

    /**
     * Renders ordinary report text.
     */
    private PageContext renderBodyText(
            PDDocument document,
            PageContext page,
            String text,
            String reportTitle,
            String generatedTime)
            throws IOException {

        float availableWidth =
                page.page
                        .getMediaBox()
                        .getWidth()
                        - (PAGE_MARGIN * 2)
                        - 16;

        List<String> wrappedLines =
                wrapText(
                        text,
                        regularFont,
                        BODY_SIZE,
                        availableWidth
                );

        for (String wrappedLine :
                wrappedLines) {

            page =
                    ensureSpace(
                            document,
                            page,
                            BODY_LINE_HEIGHT,
                            reportTitle,
                            generatedTime
                    );

            drawText(
                    page.stream,
                    regularFont,
                    BODY_SIZE,
                    PAGE_MARGIN + 8,
                    page.yPosition,
                    wrappedLine
            );

            page.yPosition -=
                    BODY_LINE_HEIGHT;
        }

        return page;
    }

    /**
     * Renders Ticket Details table headings.
     */
    private void renderTicketTableHeader(
            PageContext page)
            throws IOException {

        float x =
                PAGE_MARGIN;

        float y =
                page.yPosition;

        float[] widths =
                getTicketColumnWidths();

        String[] headings =
                {
                        "Ticket ID",
                        "Route",
                        "Type",
                        "Status",
                        "Fare",
                        "Purchased"
                };

        page.stream.setLineWidth(
                0.7f
        );

        page.stream.moveTo(
                PAGE_MARGIN,
                y + 6
        );

        page.stream.lineTo(
                page.page
                        .getMediaBox()
                        .getWidth()
                        - PAGE_MARGIN,
                y + 6
        );

        page.stream.stroke();

        for (int i = 0;
             i < headings.length;
             i++) {

            drawText(
                    page.stream,
                    boldFont,
                    TABLE_SIZE,
                    x + 3,
                    y - 5,
                    fitText(
                            headings[i],
                            boldFont,
                            TABLE_SIZE,
                            widths[i] - 6
                    )
            );

            x +=
                    widths[i];
        }

        page.yPosition -=
                22;

        page.stream.moveTo(
                PAGE_MARGIN,
                page.yPosition + 7
        );

        page.stream.lineTo(
                page.page
                        .getMediaBox()
                        .getWidth()
                        - PAGE_MARGIN,
                page.yPosition + 7
        );

        page.stream.stroke();
    }

    /**
     * Renders one Ticket Details row.
     */
    private void renderTicketTableRow(
            PageContext page,
            String line)
            throws IOException {

        String[] values =
                line.split(
                        "\\|",
                        -1
                );

        float[] widths =
                getTicketColumnWidths();

        float x =
                PAGE_MARGIN;

        for (int i = 0;
             i < widths.length;
             i++) {

            String value =
                    i < values.length
                            ? values[i].trim()
                            : "";

            drawText(
                    page.stream,
                    regularFont,
                    TABLE_SIZE,
                    x + 3,
                    page.yPosition,
                    fitText(
                            value,
                            regularFont,
                            TABLE_SIZE,
                            widths[i] - 6
                    )
            );

            x +=
                    widths[i];
        }

        page.yPosition -=
                17;

        page.stream.setLineWidth(
                0.25f
        );

        page.stream.moveTo(
                PAGE_MARGIN,
                page.yPosition + 6
        );

        page.stream.lineTo(
                page.page
                        .getMediaBox()
                        .getWidth()
                        - PAGE_MARGIN,
                page.yPosition + 6
        );

        page.stream.stroke();
    }

    /**
     * Column widths for Ticket Details.
     *
     * Total width = usable A4 report width.
     */
    private float[] getTicketColumnWidths() {

        return new float[]{
                55,
                145,
                55,
                60,
                55,
                125
        };
    }

    /**
     * Draws text at an exact position.
     */
    private void drawText(
            PDPageContentStream stream,
            PDFont font,
            float fontSize,
            float x,
            float y,
            String text)
            throws IOException {

        stream.beginText();

        stream.setFont(
                font,
                fontSize
        );

        stream.newLineAtOffset(
                x,
                y
        );

        stream.showText(
                makePdfSafe(
                        text
                )
        );

        stream.endText();
    }

    /**
     * Fits text into a fixed width.
     *
     * Long values are shortened using ...
     * rather than overlapping the next column.
     */
    private String fitText(
            String text,
            PDFont font,
            float fontSize,
            float maxWidth)
            throws IOException {

        String safeText =
                makePdfSafe(
                        text
                );

        if (getTextWidth(
                safeText,
                font,
                fontSize
        ) <= maxWidth) {

            return safeText;
        }

        String suffix =
                "...";

        String shortened =
                safeText;

        while (!shortened.isEmpty()) {

            shortened =
                    shortened.substring(
                            0,
                            shortened.length() - 1
                    );

            String candidate =
                    shortened
                            + suffix;

            if (getTextWidth(
                    candidate,
                    font,
                    fontSize
            ) <= maxWidth) {

                return candidate;
            }
        }

        return suffix;
    }

    /**
     * Wraps normal text across multiple lines.
     */
    private List<String> wrapText(
            String text,
            PDFont font,
            float fontSize,
            float maxWidth)
            throws IOException {

        ArrayList<String> lines =
                new ArrayList<>();

        String safeText =
                makePdfSafe(
                        text
                );

        if (safeText.isBlank()) {

            lines.add(
                    ""
            );

            return lines;
        }

        String[] words =
                safeText.split(
                        "\\s+"
                );

        StringBuilder current =
                new StringBuilder();

        for (String word :
                words) {

            String candidate;

            if (current.isEmpty()) {

                candidate =
                        word;

            } else {

                candidate =
                        current
                                + " "
                                + word;
            }

            if (getTextWidth(
                    candidate,
                    font,
                    fontSize
            ) <= maxWidth) {

                current.setLength(
                        0
                );

                current.append(
                        candidate
                );

            } else {

                if (!current.isEmpty()) {

                    lines.add(
                            current.toString()
                    );

                    current.setLength(
                            0
                    );
                }

                current.append(
                        word
                );
            }
        }

        if (!current.isEmpty()) {

            lines.add(
                    current.toString()
            );
        }

        return lines;
    }

    /**
     * Calculates rendered PDF text width.
     */
    private float getTextWidth(
            String text,
            PDFont font,
            float fontSize)
            throws IOException {

        return font.getStringWidth(
                makePdfSafe(
                        text
                )
        )
                / 1000
                * fontSize;
    }

    /**
     * Removes duplicate console-style headers.
     */
    private boolean isRedundantHeaderLine(
            String line) {

        if (line.isBlank()) {
            return false;
        }

        if (line.equals(
                "SMART METRO TICKETING SYSTEM"
        )) {

            return true;
        }

        if (line.startsWith(
                "MONTHLY REPORT:"
        )) {

            return true;
        }

        if (line.startsWith(
                "QUARTERLY REPORT:"
        )) {

            return true;
        }

        if (line.startsWith(
                "YEARLY REPORT:"
        )) {

            return true;
        }

        if (line.startsWith(
                "ALL-TIME REPORT"
        )) {

            return true;
        }

        if (line.startsWith(
                "Generated:"
        )) {

            return true;
        }

        return line.matches(
                "=+"
        );
    }

    /**
     * Closes a page and adds a centered
     * page-number footer.
     */
    private void closePage(
            PageContext page)
            throws IOException {

        String footer =
                "Page "
                        + page.pageNumber;

        float footerWidth =
                getTextWidth(
                        footer,
                        regularFont,
                        8
                );

        float pageWidth =
                page.page
                        .getMediaBox()
                        .getWidth();

        drawText(
                page.stream,
                regularFont,
                8,
                (pageWidth - footerWidth) / 2,
                24,
                footer
        );

        page.stream.close();
    }

    /**
     * Converts unsupported characters into
     * printable characters supported by the
     * standard Helvetica PDF font.
     */
    private String makePdfSafe(
            String text) {

        if (text == null) {
            return "";
        }

        return text.replaceAll(
                "[^\\x20-\\x7E]",
                "?"
        );
    }

    /**
     * Stores the state of one currently
     * active PDF page.
     */
    private static class PageContext {

        private final PDDocument document;

        private final PDPage page;

        private final PDPageContentStream stream;

        private final int pageNumber;

        private float yPosition;

        private PageContext(
                PDDocument document,
                PDPage page,
                PDPageContentStream stream,
                int pageNumber,
                float yPosition) {

            this.document =
                    document;

            this.page =
                    page;

            this.stream =
                    stream;

            this.pageNumber =
                    pageNumber;

            this.yPosition =
                    yPosition;
        }
    }
}