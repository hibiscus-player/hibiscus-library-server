package me.mrgazdag.hibiscus.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public static String readPath(Path path) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream stream = Files.newInputStream(path);
        stream.transferTo(baos);
        stream.close();
        return baos.toString(StandardCharsets.UTF_8);
    }
}
