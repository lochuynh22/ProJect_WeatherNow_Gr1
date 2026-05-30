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

        @SerializedName("wind")
        public Wind wind;

        @SerializedName("pop")
        public float pop;

        @SerializedName("clouds")
        public Clouds clouds;
    }

    public static class Main {
        @SerializedName("temp")
        public float temp;

        @SerializedName("temp_min")
        public float tempMin;

        @SerializedName("temp_max")
        public float tempMax;

        @SerializedName("humidity")
        public int humidity;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }

    public static class Wind {
        @SerializedName("speed")
        public float speed;
    }

    public static class Clouds {
        @SerializedName("all")
        public int all;
    }
}
