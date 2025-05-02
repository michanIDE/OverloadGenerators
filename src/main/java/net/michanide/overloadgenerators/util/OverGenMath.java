package net.michanide.overloadgenerators.util;

public class OverGenMath {
    public static Long pow(Long base, Long exp) {
        if (exp == 0) {
            return 1L;
        }
        Long result = base;
        for (Long i = 1L; i < exp; i++) {
            result *= base;
        }
        return result;
    }

    public static int pow(int base, int exp) {
        if (exp == 0) {
            return 1;
        }
        int result = base;
        for (int i = 1; i < exp; i++) {
            result *= base;
        }
        return result;
    }

    public static Double pow(Double base, Long exp) {
        if (exp == 0) {
            return 1.0;
        }
        Double result = base;
        for (Long i = 1L; i < exp; i++) {
            result *= base;
        }
        return result;
    }
}