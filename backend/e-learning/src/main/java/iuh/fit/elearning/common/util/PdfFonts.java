package iuh.fit.elearning.common.util;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.List;

/**
 * Chọn font cho PDF (OpenPDF). Font chuẩn của PDF không có tiếng Việt, nên thử nạp font TrueType hệ thống
 * (Arial / Times / DejaVu / Liberation...) hoặc đường dẫn cấu hình (PDF_FONT_PATH). Không có font nào thì dùng
 * Helvetica và bỏ dấu tiếng Việt khỏi văn bản (xem {@link #text}).
 */
@Slf4j
public final class PdfFonts {

    private static final List<String> CANDIDATES = List.of(
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/times.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/usr/share/fonts/TTF/DejaVuSans.ttf",
            "/Library/Fonts/Arial Unicode.ttf",
            "/System/Library/Fonts/Supplemental/Arial.ttf");

    private final BaseFont baseFont;
    private final boolean unicode;

    private PdfFonts(BaseFont baseFont, boolean unicode) {
        this.baseFont = baseFont;
        this.unicode = unicode;
    }

    /** @param configuredPath đường dẫn font .ttf cấu hình; null / rỗng thì tự dò. */
    public static PdfFonts load(String configuredPath) {
        java.util.ArrayList<String> paths = new java.util.ArrayList<>();
        if (configuredPath != null && !configuredPath.isBlank()) {
            paths.add(configuredPath.trim());
        }
        paths.addAll(CANDIDATES);
        for (String p : paths) {
            try {
                if (Files.isReadable(Path.of(p))) {
                    return new PdfFonts(BaseFont.createFont(p, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), true);
                }
            } catch (Exception e) {
                log.debug("Không nạp được font {}: {}", p, e.getMessage());
            }
        }
        try {
            log.warn("Không tìm thấy font TrueType hỗ trợ tiếng Việt cho PDF; dùng Helvetica và bỏ dấu. "
                    + "Đặt PDF_FONT_PATH tới một tệp .ttf để hiển thị đầy đủ dấu.");
            return new PdfFonts(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), false);
        } catch (Exception e) {
            throw new IllegalStateException("Không khởi tạo được font PDF", e);
        }
    }

    public Font font(float size, int style, java.awt.Color color) {
        return new Font(baseFont, size, style, color);
    }

    public Font font(float size) {
        return new Font(baseFont, size, Font.NORMAL, java.awt.Color.BLACK);
    }

    /** Giữ nguyên nếu font hỗ trợ Unicode, ngược lại bỏ dấu tiếng Việt. */
    public String text(String s) {
        if (s == null) {
            return "";
        }
        if (unicode) {
            return s;
        }
        String t = s.replace('đ', 'd').replace('Đ', 'D');
        return Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
