package iuh.fit.elearning.payment.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** HTML đơn giản (mobile-first) cho trang thanh toán giả lập và trang kết quả. */
final class PaymentHtml {

    private PaymentHtml() {
    }

    record Line(String title, BigDecimal price) {
    }

    static String mockCheckout(String orderCode, BigDecimal total, List<Line> lines, String statusNote) {
        StringBuilder items = new StringBuilder();
        for (Line l : lines) {
            items.append("<li><span>").append(esc(l.title())).append("</span><b>").append(money(l.price()))
                    .append("</b></li>");
        }
        String actions = statusNote != null
                ? "<p class=\"note\">" + esc(statusNote) + "</p>"
                : "<form method=\"post\" action=\"/api/v1/payments/mock/" + esc(orderCode)
                + "/complete?result=SUCCESS\"><button class=\"ok\" type=\"submit\">Thanh toán thành công</button></form>"
                + "<form method=\"post\" action=\"/api/v1/payments/mock/" + esc(orderCode)
                + "/complete?result=FAILED\"><button class=\"bad\" type=\"submit\">Thanh toán thất bại</button></form>";
        return page("Thanh toán giả lập",
                "<h1>Thanh toán giả lập</h1><p class=\"muted\">Chỉ dùng cho phát triển - không thu tiền thật.</p>"
                        + "<p>Đơn hàng <b>" + esc(orderCode) + "</b></p><ul>" + items + "</ul>"
                        + "<p class=\"total\">Tổng thanh toán <b>" + money(total) + "</b></p>" + actions);
    }

    static String result(boolean success, String orderCode, String message, String appRedirectUrl) {
        String link = "";
        if (appRedirectUrl != null && !appRedirectUrl.isBlank() && orderCode != null) {
            String sep = appRedirectUrl.contains("?") ? "&" : "?";
            link = "<p><a class=\"btn\" href=\"" + esc(appRedirectUrl + sep + "orderCode=" + orderCode
                    + "&status=" + (success ? "SUCCESS" : "FAILED")) + "\">Quay lại ứng dụng</a></p>";
        } else {
            link = "<p class=\"muted\">Bạn có thể đóng trang này và quay lại ứng dụng.</p>";
        }
        return page(success ? "Thanh toán thành công" : "Thanh toán chưa hoàn tất",
                "<h1 class=\"" + (success ? "good" : "fail") + "\">" + (success ? "Thành công" : "Chưa hoàn tất") + "</h1>"
                        + (orderCode == null ? "" : "<p>Đơn hàng <b>" + esc(orderCode) + "</b></p>")
                        + "<p>" + esc(message) + "</p>" + link);
    }

    private static String page(String title, String body) {
        return "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>" + esc(title)
                + "</title><style>body{font-family:system-ui,sans-serif;background:#f4f6fb;margin:0;padding:24px}"
                + ".card{max-width:440px;margin:0 auto;background:#fff;border-radius:16px;padding:24px;"
                + "box-shadow:0 4px 18px rgba(0,0,0,.08)}h1{font-size:22px;margin:0 0 8px}ul{list-style:none;padding:0}"
                + "li{display:flex;justify-content:space-between;gap:12px;padding:8px 0;border-bottom:1px solid #eee}"
                + ".muted{color:#777;font-size:14px}.total{font-size:18px;text-align:right}.good{color:#1a9b4b}"
                + ".fail{color:#d33}.note{color:#b36b00}button,.btn{display:block;width:100%;box-sizing:border-box;"
                + "text-align:center;text-decoration:none;color:#fff;border:0;border-radius:12px;padding:14px;"
                + "font-size:16px;margin-top:12px;background:#4f46e5}.ok{background:#1a9b4b}.bad{background:#d33}</style>"
                + "</head><body><div class=\"card\">" + body + "</div></body></html>";
    }

    static String money(BigDecimal value) {
        return NumberFormat.getInstance(Locale.forLanguageTag("vi-VN")).format(value) + "đ";
    }

    static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
