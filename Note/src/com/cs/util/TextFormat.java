package com.cs.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat") public class TextFormat {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static Date parseText(String text) throws Exception {
        return dateFormat.parse(text);
    }

    public static String getNoteSummary(String content) {
        if (content.length() > 10) {
            StringBuilder sb = new StringBuilder(content.substring(0, 10));
            sb.append("...");
            return sb.toString();
        }
        return content;
    }
}
