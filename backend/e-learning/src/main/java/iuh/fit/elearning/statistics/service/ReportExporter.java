package iuh.fit.elearning.statistics.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.PdfFonts;
import iuh.fit.elearning.statistics.dto.StatisticsReport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Xuất báo cáo thống kê ra xlsx (Apache POI) hoặc pdf (OpenPDF). */
@Component
public class ReportExporter {

    private final PdfFonts fonts;

    public ReportExporter(PdfFonts fonts) {
        this.fonts = fonts;
    }

    public byte[] xlsx(StatisticsReport report) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bao cao");
            XSSFFont bold = workbook.createFont();
            bold.setBold(true);
            CellStyle title = workbook.createCellStyle();
            XSSFFont big = workbook.createFont();
            big.setBold(true);
            big.setFontHeightInPoints((short) 14);
            title.setFont(big);
            CellStyle header = workbook.createCellStyle();
            header.setFont(bold);
            header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            DataFormat format = workbook.createDataFormat();
            CellStyle number = workbook.createCellStyle();
            number.setDataFormat(format.getFormat("#,##0.##"));

            int r = 0;
            Row titleRow = sheet.createRow(r++);
            Cell tc = titleRow.createCell(0);
            tc.setCellValue(report.title());
            tc.setCellStyle(title);
            sheet.createRow(r++).createCell(0).setCellValue("Từ " + report.from() + " đến " + report.to());
            r++;
            for (Map.Entry<String, Object> e : report.summary().entrySet()) {
                Row row = sheet.createRow(r++);
                Cell label = row.createCell(0);
                label.setCellValue(e.getKey());
                label.setCellStyle(header);
                setValue(row.createCell(1), e.getValue(), number);
            }
            r++;
            Row head = sheet.createRow(r++);
            for (int i = 0; i < report.columns().size(); i++) {
                Cell c = head.createCell(i);
                c.setCellValue(report.columns().get(i));
                c.setCellStyle(header);
            }
            for (List<Object> values : report.rows()) {
                Row row = sheet.createRow(r++);
                for (int i = 0; i < values.size(); i++) {
                    setValue(row.createCell(i), values.get(i), number);
                }
            }
            for (int i = 0; i < Math.max(2, report.columns().size()); i++) {
                sheet.setColumnWidth(i, (i == 0 ? 34 : 20) * 256);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Không tạo được tệp Excel");
        }
    }

    public byte[] pdf(StatisticsReport report) {
        try {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph(fonts.text(report.title()), fonts.font(18, Font.BOLD, Color.BLACK)));
            Paragraph period = new Paragraph(fonts.text("Từ " + report.from() + " đến " + report.to()), fonts.font(11));
            period.setSpacingAfter(8);
            doc.add(period);
            for (Map.Entry<String, Object> e : report.summary().entrySet()) {
                doc.add(new Paragraph(fonts.text(e.getKey() + ": " + format(e.getValue())), fonts.font(10)));
            }

            PdfPTable table = new PdfPTable(report.columns().size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setHeaderRows(1);
            for (String column : report.columns()) {
                PdfPCell cell = new PdfPCell(new Phrase(fonts.text(column), fonts.font(9, Font.BOLD, Color.BLACK)));
                cell.setBackgroundColor(new Color(230, 230, 240));
                cell.setPadding(4);
                table.addCell(cell);
            }
            for (List<Object> values : report.rows()) {
                for (Object v : values) {
                    PdfPCell cell = new PdfPCell(new Phrase(fonts.text(format(v)), fonts.font(9)));
                    cell.setPadding(3);
                    if (v instanceof Number) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    table.addCell(cell);
                }
            }
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Không tạo được tệp PDF");
        }
    }

    private static void setValue(Cell cell, Object value, CellStyle numberStyle) {
        if (value instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
            cell.setCellStyle(numberStyle);
        } else if (value instanceof Number n) {
            cell.setCellValue(n.doubleValue());
            cell.setCellStyle(numberStyle);
        } else {
            cell.setCellValue(value == null ? "" : value.toString());
        }
    }

    private static String format(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal bd) {
            return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(bd);
        }
        if (value instanceof Number n) {
            return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(n);
        }
        return value.toString();
    }
}
