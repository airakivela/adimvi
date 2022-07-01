package com.application.adimviandroid.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {
    public static Boolean isValideEmail(String email) {
        String emailPattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
        if (email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDate(String formatDate, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatDate);
        return format.format(date);
    }

    public static String convertThousand(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else {
            if (count % 1000 == 0) {
                return (count / 1000) + "K";
            } else {
                float value = (float) count / 1000;
                return String.format("%.1f", value) + "K";
            }
        }
    }

    public static String longToString(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur % 3600000) / 60000;
        int scs = ((dur % 360000) % 60000) / 1000;
        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }
}
