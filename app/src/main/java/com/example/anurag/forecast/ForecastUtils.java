package com.example.anurag.forecast;

import android.os.Build;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anurag on 7/12/15.
 */
public class ForecastUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static final Map<String, String> icon_map;
    static
    {
        icon_map = new HashMap<String, String>();
        icon_map.put("clear-day", "clear");
        icon_map.put("clear-night", "clear_night");
        icon_map.put("rain", "rain");
        icon_map.put("snow", "snow");
        icon_map.put("sleet", "sleet");
        icon_map.put("wind", "wind");
        icon_map.put("fog", "fog");
        icon_map.put("cloudy", "cloudy");
        icon_map.put("partly-cloudy-day", "cloud_day");
        icon_map.put("partly-cloudy-night", "cloud_night");
        icon_map.put("storm", "Storm");
    }

    public String getIcon(String name) {
        return icon_map.get(name);
    }

    public String getLowHighTemperature(int mintemp, int maxtemp) {
        String temp;
        temp = "L: "+ mintemp + (char) 0x00B0 + " | H: " + maxtemp + (char) 0x00B0;
        return temp;
    }

    public String getMinMaxTemperature(int mintemp, int maxtemp) {
        String temp;
        temp = "Min: "+ mintemp + (char) 0x00B0 + " | Max: " + maxtemp + (char) 0x00B0;
        return temp;
    }

    public String getFormattedDate(int time) {
        Date d = new Date(time*1000L);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd");
        return format.format(d);
    }

    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }
}
