package com.cbt;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickByText {

    private static final String ADB_PATH = System.getProperty("user.dir") + "\\platform-tools\\adb.exe";

    /**
     * click vào màn hình
     *
     * @param x tọa độ x
     * @param y tọa độ y
     */
    public static void tap(double x, double y) {
        try {
            Runtime.getRuntime().exec(ADB_PATH + " -d shell input tap " + x + " " + y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dump screen
     */
    private static String dump() {
        java.util.Scanner s;
        try {
            s = new java.util.Scanner(
                    new InputStreamReader(
                            Runtime.getRuntime().exec(ADB_PATH + " -d exec-out uiautomator dump /dev/tty").getInputStream(),
                            StandardCharsets.UTF_8
                    )
            ).useDelimiter("\\A");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s.hasNext() ? s.next() : "";
    }

    /**
     * click by text
     * @param text từ cần nhấn
     * @return kết quả tìm kiếm
     */
    public static boolean clickByText(String text) {
        String s = dump();
        //tìm node mà thuộc tính text chứa từ đó
        Matcher matcher = Pattern.compile("text=\"[^\"]*" + text + "[^>]*").matcher(s);

        if (matcher.find()) {
            //tách string của node đó ra
            String tmp = s.substring(matcher.start(), matcher.end());

            //lấy tọa độ của node chứa text
            tmp = tmp.replaceAll("]\\[", ",");
            tmp = tmp.substring(tmp.indexOf("bounds=\"[") + 9, tmp.length() - 4);
            String[] coord = tmp.split(",");

            tap((Integer.parseInt(coord[2]) + Integer.parseInt(coord[0])) / 2.0,
                    (Integer.parseInt(coord[3]) + Integer.parseInt(coord[1])) / 2.0);
            return true;
        }
        return false;
    }
}
