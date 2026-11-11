package iuh.fit.elearning.common;

import iuh.fit.elearning.common.util.CodeGenerator;
import iuh.fit.elearning.common.util.HashUtils;
import iuh.fit.elearning.common.util.SlugUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void slugifyRemovesDiacriticsAndSymbols() {
        assertEquals("thiet-ke-ux-ui", SlugUtils.slugify("Thiết kế UX/UI"));
        assertEquals("duong-dua-don", SlugUtils.slugify("  Đường đưa đón!! "));
        assertEquals("", SlugUtils.slugify(null));
    }

    @Test
    void uniqueSlugHasRandomSuffix() {
        String a = SlugUtils.uniqueSlug("Java Spring Boot");
        String b = SlugUtils.uniqueSlug("Java Spring Boot");
        assertTrue(a.startsWith("java-spring-boot-"));
        assertNotEquals(a, b);
    }

    @Test
    void otpHasRequestedLengthAndOnlyDigits() {
        String otp = CodeGenerator.numericOtp(6);
        assertEquals(6, otp.length());
        assertTrue(otp.chars().allMatch(Character::isDigit));
    }

    @Test
    void codesFollowDocumentedFormats() {
        assertTrue(CodeGenerator.orderCode().matches("ORD\\d{12}[A-Z0-9]{4}"));
        assertTrue(CodeGenerator.certificateCode().matches("ELN-\\d{6}-[A-Z0-9]{8}"));
    }

    @Test
    void sha256AndConstantTimeEquals() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", HashUtils.sha256Hex("abc"));
        assertTrue(HashUtils.constantTimeEquals("abc", "abc"));
        assertFalse(HashUtils.constantTimeEquals("abc", "abd"));
    }
}
