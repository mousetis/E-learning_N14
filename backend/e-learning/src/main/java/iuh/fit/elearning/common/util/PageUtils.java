package iuh.fit.elearning.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageUtils() {
    }

    /** page bắt đầu từ 0; size mặc định 20, tối đa 100 (SDS 6.1). */
    public static PageRequest of(int page, int size) {
        return PageRequest.of(Math.max(page, 0), clampSize(size));
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(page, 0), clampSize(size), sort);
    }

    public static int clampSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
