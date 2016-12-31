package com.myadridev.rememberall.helpers;

import android.os.Build;
import android.widget.TimePicker;

public class SdkHelper {

    public static int getMinute(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= 23) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

    public static int getHour(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= 23) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }

    public static void setHour(TimePicker timePicker, Integer currentHour) {
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(currentHour);
        } else {
            timePicker.setCurrentHour(currentHour);
        }
    }

    public static void setMinute(TimePicker timePicker, Integer currentMinute) {
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setMinute(currentMinute);
        } else {
            timePicker.setCurrentMinute(currentMinute);
        }
    }
}
