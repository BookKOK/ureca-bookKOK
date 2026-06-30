package com.bookkok.util;

public class PhoneNumberUtil {

    public static String normalize(String phone) {
        if (phone == null) return null;

        phone = phone.replaceAll("[^0-9]", "");

        if (phone.startsWith("0")) {
            phone = "82" + phone.substring(1);
        }

        return "+" + phone;
    }
}