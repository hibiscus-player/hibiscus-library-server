package me.mrgazdag.hibiscus.library;

import java.util.Random;

public class Util {
    public static final char[] azAZ09 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final Random random = new Random();
    public static String generateRandomString(int length, char[] chars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }
}
