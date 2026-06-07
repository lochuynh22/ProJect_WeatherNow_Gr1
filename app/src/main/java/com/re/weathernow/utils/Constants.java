package com.re.weathernow.utils;

import com.re.weathernow.BuildConfig;

public class Constants {
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = BuildConfig.OPENWEATHER_API_KEY;
    public static final String PREFS_NAME = "WeatherPrefs";
    public static final String LAST_CITY = "last_city";

    public static final String UNIT_PREF = "unit_pref";     // key lưu đơn vị
    public static final String UNIT_METRIC = "metric";       // °C
    public static final String UNIT_IMPERIAL = "imperial";   // °F

    public static final String SEARCH_HISTORY = "search_history"; // key lưu lịch sử tìm kiếm
    public static final int MAX_HISTORY = 10;                      // tối đa 10 thành phố
}

