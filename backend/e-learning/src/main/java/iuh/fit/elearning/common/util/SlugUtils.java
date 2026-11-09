package iuh.fit.elearning.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class SlugUtils {

    private SlugUtils() {
    }

    /** "Thiết kế UX/UI" -> "thiet-ke-ux-ui" */
    public static String slugify(String input) {
        if (input == null) {
            return "";
        }
        String s = input.replace('đ', 'd').replace('Đ', 'D');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        return s;
    }

    /** Slug kèm hậu tố ngẫu nhiên ngắn để tránh trùng (dùng cho khóa học). */
    public static String uniqueSlug(String input) {
        String base = slugify(input);
        if (base.length() > 200) {
            base = base.substring(0, 200);
        }
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return base.isEmpty() ? suffix : base + "-" + suffix;
    }
}
