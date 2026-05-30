package com.re.weathernow.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    public List<ForecastItem> list;

    public static class ForecastItem {
        @SerializedName("dt_txt")
        public String dateTime;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;
    }

    public static class Main {
        @SerializedName("temp")
        public float temp;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }
}
