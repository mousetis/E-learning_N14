package iuh.fit.elearning.learning.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.CodeGenerator;
import iuh.fit.elearning.common.util.PdfFonts;
import iuh.fit.elearning.learning.dto.response.CertificateResponse;
import iuh.fit.elearning.learning.entity.Certificate;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.repository.CertificateRepository;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/** UC012: cấp và xuất chứng chỉ hoàn thành khóa học (mã ELN-yyyyMM-xxxxxxxx, PDF tạo theo yêu cầu). */
@Service
public class CertificateService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static final Color INDIGO = new Color(79, 70, 229);
    private static final Color GRAY = new Color(90, 90, 90);

    private final CertificateRepository certificateRepository;
    private final NotificationService notificationService;
    private final PdfFonts fonts;
    private final String publicBaseUrl;

    public CertificateService(CertificateRepository certificateRepository, NotificationService notificationService,
                              PdfFonts fonts,
                              @Value("${app.public-base-url}") String publicBaseUrl) {
        this.certificateRepository = certificateRepository;
        this.notificationService = notificationService;
        this.publicBaseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        this.fonts = fonts;
    }

    /** Cấp chứng chỉ một lần cho ghi danh (UNIQUE(enrollment_id)); trả về chứng chỉ hiện có nếu đã cấp. */
    @Transactional
    public Certificate issue(Enrollment enrollment) {
        return certificateRepository.findByEnrollmentId(enrollment.getId()).orElseGet(() -> {
            Certificate certificate = Certificate.builder()
                    .enrollment(enrollment)
                    .certificateCode(CodeGenerator.certificateCode())
                    .build();
            certificateRepository.saveAndFlush(certificate);
            notificationService.notifyUser(enrollment.getUser().getId(), NotificationType.CERTIFICATE,
                    "Bạn đã nhận chứng chỉ", "Chúc mừng! Bạn đã hoàn thành khóa học \""
                            + enrollment.getCourse().getTitle() + "\"", certificate.getId());
            return certificate;
        });
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> mine(UUID userId) {
        return certificateRepository.findByEnrollmentUserIdOrderByIssuedAtDesc(userId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CertificateResponse byCode(String code) {
        return toResponse(find(code));
    }

    @Transactional(readOnly = true)
    public byte[] pdf(String code) {
        Certificate c = find(code);
        String student = c.getEnrollment().getUser().getFullName();
        String course = c.getEnrollment().getCourse().getTitle();
        String instructor = c.getEnrollment().getCourse().getInstructor().getFullName();
        try {
            Rectangle page = PageSize.A4.rotate();
            Document doc = new Document(page, 60, 60, 60, 60);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            PdfContentByte canvas = writer.getDirectContent();
            canvas.setColorStroke(INDIGO);
            canvas.setLineWidth(4f);
            canvas.rectangle(24, 24, page.getWidth() - 48, page.getHeight() - 48);
            canvas.stroke();
            canvas.setLineWidth(1f);
            canvas.rectangle(34, 34, page.getWidth() - 68, page.getHeight() - 68);
            canvas.stroke();

            doc.add(centered(fonts.text("CHỨNG CHỈ HOÀN THÀNH"), fonts.font(34, Font.BOLD, INDIGO), 30, 18));
            doc.add(centered(fonts.text("Chứng nhận"), fonts.font(16, Font.NORMAL, GRAY), 0, 10));
            doc.add(centered(fonts.text(student), fonts.font(30, Font.BOLD, Color.BLACK), 0, 10));
            doc.add(centered(fonts.text("đã hoàn thành xuất sắc khóa học"), fonts.font(16, Font.NORMAL, GRAY), 0, 10));
            doc.add(centered(fonts.text(course), fonts.font(24, Font.BOLD, INDIGO), 0, 24));
            doc.add(centered(fonts.text("Giảng viên: " + instructor), fonts.font(14, Font.NORMAL, Color.BLACK), 0, 4));
            doc.add(centered(fonts.text("Ngày cấp: " + DATE.format(c.getIssuedAt())),
                    fonts.font(14, Font.NORMAL, Color.BLACK), 0, 4));
            doc.add(centered(fonts.text("Mã chứng chỉ: " + c.getCertificateCode()),
                    fonts.font(12, Font.NORMAL, GRAY), 14, 0));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Không tạo được tệp PDF chứng chỉ");
        }
    }

    private static Paragraph centered(String text, Font font, float before, float after) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(before);
        p.setSpacingAfter(after);
        return p;
    }

    private Certificate find(String code) {
        return certificateRepository.findByCertificateCode(code)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy chứng chỉ"));
    }

    private CertificateResponse toResponse(Certificate c) {
        Enrollment e = c.getEnrollment();
        return new CertificateResponse(c.getCertificateCode(), e.getCourse().getId(), e.getCourse().getTitle(),
                e.getUser().getFullName(), e.getCourse().getInstructor().getFullName(), c.getIssuedAt(),
                publicBaseUrl + "/api/v1/certificates/" + c.getCertificateCode() + "/pdf");
    }
}
