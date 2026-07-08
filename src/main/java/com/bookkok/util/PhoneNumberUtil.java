package com.bookkok.util;

public class PhoneNumberUtil {

    public static String normalizePhone(String phone) {
        if (phone == null) return null;

        // 숫자만 추출 (일단 정리)
        phone = phone.replaceAll("[^0-9]", "");

        // 한국 +82 처리
        if (phone.startsWith("82")) {
            phone = phone.substring(2);
            if (!phone.startsWith("0")) {
                phone = "0" + phone;
            }
        }

        return phone;
    }
}