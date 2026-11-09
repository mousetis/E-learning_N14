package iuh.fit.elearning.config;

import iuh.fit.elearning.common.util.PdfFonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Font dùng chung cho mọi tệp PDF (chứng chỉ, báo cáo thống kê). */
@Configuration
public class PdfConfig {

    @Bean
    public PdfFonts pdfFonts(@Value("${app.pdf.font-path:}") String fontPath) {
        return PdfFonts.load(fontPath);
    }
}
