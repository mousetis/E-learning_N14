package iuh.fit.elearning.common.util;

import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class CodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private CodeGenerator() {
    }

    /** Mã đơn hàng, ví dụ ORD261027143012A7F3 (tối đa 30 ký tự). */
    public static String orderCode() {
        String time = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        return "ORD" + time + randomString(4);
    }

    /** Mã chứng chỉ: ELN-yyyyMM-8 ký tự ngẫu nhiên. */
    public static String certificateCode() {
        String ym = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMM"));
        return "ELN-" + ym + "-" + randomString(8);
    }

    /** Mã OTP gồm các chữ số, dùng SecureRandom. */
    public static String numericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
