package com.re.weathernow.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentWeatherResponse {
    @SerializedName("name")
    public String cityName;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public List<Weather> weather;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("visibility")
    public int visibility;

    @SerializedName("sys")
    public Sys sys;

    @SerializedName("clouds")
    public Clouds clouds;

    @SerializedName("dt")
    public long dt;

    @SerializedName("timezone")
    public long timezone;

    public static class Main {
        @SerializedName("temp")
        public float temp;

        @SerializedName("humidity")
        public int humidity;

        @SerializedName("feels_like")
        public float feelsLike;

        @SerializedName("temp_min")
        public float tempMin;

        @SerializedName("temp_max")
        public float tempMax;

        @SerializedName("pressure")
        public int pressure;
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

        @SerializedName("deg")
        public int deg;
    }

    public static class Sys {
        @SerializedName("sunrise")
        public long sunrise;

        @SerializedName("sunset")
        public long sunset;

        @SerializedName("country")
        public String country;
    }

    public static class Clouds {
        @SerializedName("all")
        public int all;
    }
}
