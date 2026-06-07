package com.re.weathernow.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.re.weathernow.R;
import com.re.weathernow.adapter.ForecastAdapter;
import com.re.weathernow.adapter.HourlyAdapter;
import com.re.weathernow.api.ApiClient;
import com.re.weathernow.model.CurrentWeatherResponse;
import com.re.weathernow.model.ForecastResponse;
import com.re.weathernow.utils.Constants;

import com.re.weathernow.adapter.SearchHistoryAdapter;
import com.re.weathernow.adapter.CitySearchAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText edtCity;
    private Button btnSearch;
    private TextView tvCityName, tvTemperature, tvDescription, tvHumidity, tvWindSpeed;
    private TextView tvFeelsLike, tvVisibility, tvCountry, tvSunrise, tvSunset, tvUnitToggle;
    private ImageView imgWeatherIcon;
    private RecyclerView rvForecast;
    private RecyclerView rvHourly;
    private SwipeRefreshLayout swipeRefreshLayout;

    // History views
    private android.widget.LinearLayout layoutHistory;
    private RecyclerView rvHistory;
    private TextView tvClearHistory;
    private SearchHistoryAdapter historyAdapter;

    // Suggestion views
    private android.widget.LinearLayout layoutSuggestion;
    private RecyclerView rvSuggestion;
    private CitySearchAdapter suggestionAdapter;
    private List<String> allCities = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

    private String currentUnit = Constants.UNIT_METRIC;
    private String currentCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(R.layout.activity_main);

        initViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        // Đọc unit từ SharedPreferences
        currentUnit = sharedPreferences.getString(Constants.UNIT_PREF, Constants.UNIT_METRIC);
        tvUnitToggle.setText(currentUnit.equals(Constants.UNIT_METRIC) ? "°C" : "°F");

        // Setup rvHourly horizontal
        rvHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        btnSearch.setOnClickListener(v -> {
            String city = edtCity.getText().toString().trim();
            if (!city.isEmpty()) {
                currentCity = city;
                hideHistory();
                edtCity.clearFocus();
                getWeatherDataByCity(city);
                saveLastCity(city);
                saveSearchHistory(city);  // lưu vào lịch sử
            }
        });

        // Toggle đơn vị
        tvUnitToggle.setOnClickListener(v -> {
            if (currentUnit.equals(Constants.UNIT_METRIC)) {
                currentUnit = Constants.UNIT_IMPERIAL;
                tvUnitToggle.setText("°F");
            } else {
                currentUnit = Constants.UNIT_METRIC;
                tvUnitToggle.setText("°C");
            }
            sharedPreferences.edit().putString(Constants.UNIT_PREF, currentUnit).apply();
            if (!currentCity.isEmpty()) {
                getWeatherDataByCity(currentCity);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadLastOrCurrentLocation);

        // Lịch sử tìm kiếm
        setupHistory();

        // Gợi ý thành phố khi gõ
        setupSuggestion();

        loadLastOrCurrentLocation();
    }

    private void initViews() {
        edtCity = findViewById(R.id.edtCity);
        btnSearch = findViewById(R.id.btnSearch);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvCountry = findViewById(R.id.tvCountry);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvUnitToggle = findViewById(R.id.tvUnitToggle);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        rvForecast = findViewById(R.id.rvForecast);
        rvHourly = findViewById(R.id.rvHourly);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        rvForecast.setLayoutManager(new LinearLayoutManager(this));

        // History views
        layoutHistory = findViewById(R.id.layoutHistory);
        rvHistory = findViewById(R.id.rvHistory);
        tvClearHistory = findViewById(R.id.tvClearHistory);

        // Suggestion views
        layoutSuggestion = findViewById(R.id.layoutSuggestion);
        rvSuggestion = findViewById(R.id.rvSuggestion);

        // SwipeRefreshLayout màu sáng nổi bật trên nền tối
        swipeRefreshLayout.setColorSchemeColors(
            Color.parseColor("#64B5F6"),
            Color.parseColor("#80DEEA"),
            Color.parseColor("#FFD54F")
        );
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#302B63"));
    }

    // ===================== SEARCH HISTORY =====================

    private void setupHistory() {
        List<String> history = loadSearchHistory();
        historyAdapter = new SearchHistoryAdapter(history, new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onCityClick(String city) {
                // Bấm vào 1 city trong lịch sử → tìm kiếm luôn
                edtCity.setText(city);
                currentCity = city;
                hideHistory();
                getWeatherDataByCity(city);
                saveLastCity(city);
                // Đưa city lên đầu lịch sử
                saveSearchHistory(city);
            }

            @Override
            public void onDeleteClick(String city) {
                // Xóa 1 city khỏi lịch sử
                List<String> history = loadSearchHistory();
                history.remove(city);
                saveHistoryList(history);
                historyAdapter.updateList(history);
                if (history.isEmpty()) hideHistory();
            }
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        // Hiện dropdown khi focus vào ô search
        edtCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                List<String> h = loadSearchHistory();
                if (!h.isEmpty()) {
                    historyAdapter.updateList(h);
                    showHistory();
                }
            }
        });

        // Ẩn khi bấm ra ngoài
        swipeRefreshLayout.setOnTouchListener((v, event) -> {
            hideHistory();
            edtCity.clearFocus();
            return false;
        });

        // Xóa toàn bộ lịch sử
        tvClearHistory.setOnClickListener(v -> {
            saveHistoryList(new ArrayList<>());
            historyAdapter.updateList(new ArrayList<>());
            hideHistory();
        });
    }

    private void showHistory() {
        layoutHistory.setVisibility(android.view.View.VISIBLE);
    }

    private void hideHistory() {
        layoutHistory.setVisibility(android.view.View.GONE);
    }

    /** Lưu thành phố vào đầu danh sách lịch sử, tối đa MAX_HISTORY */
    private void saveSearchHistory(String city) {
        List<String> history = loadSearchHistory();
        history.remove(city);           // xóa nếu đã tồn tại (tránh trùng)
        history.add(0, city);           // thêm lên đầu
        if (history.size() > Constants.MAX_HISTORY) {
            history = history.subList(0, Constants.MAX_HISTORY);
        }
        saveHistoryList(history);
    }

    /** Đọc danh sách lịch sử từ SharedPreferences */
    private List<String> loadSearchHistory() {
        String raw = sharedPreferences.getString(Constants.SEARCH_HISTORY, "");
        List<String> list = new ArrayList<>();
        if (!raw.isEmpty()) {
            String[] parts = raw.split("\\|");
            for (String p : parts) {
                if (!p.trim().isEmpty()) list.add(p.trim());
            }
        }
        return list;
    }

    /** Ghi danh sách lịch sử vào SharedPreferences (ngăn cách bởi "|") */
    private void saveHistoryList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }
        sharedPreferences.edit().putString(Constants.SEARCH_HISTORY, sb.toString()).apply();
    }

    // ===================== END HISTORY =====================

    // ===================== CITY SUGGESTION =====================

    private void setupSuggestion() {
        // Load danh sách thành phố từ file assets (chạy 1 lần)
        loadCitiesFromAssets();

        suggestionAdapter = new CitySearchAdapter(new ArrayList<>(), city -> {
            // User bấm chọn 1 thành phố gợi ý
            edtCity.setText(city);
            currentCity = city;
            hideSuggestion();
            hideHistory();
            edtCity.clearFocus();
            getWeatherDataByCity(city);
            saveLastCity(city);
            saveSearchHistory(city);
        });
        rvSuggestion.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestion.setAdapter(suggestionAdapter);

        // TextWatcher: lắng nghe từng ký tự user gõ
        edtCity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    // Lọ danh sách theo từ khóa
                    List<String> filtered = filterCities(query);
                    suggestionAdapter.updateSuggestions(filtered);
                    if (!filtered.isEmpty()) {
                        hideHistory();       // ẩn lịch sử khi đang gõ gợi ý
                        showSuggestion();
                    } else {
                        hideSuggestion();
                    }
                } else {
                    hideSuggestion();
                    // Nếu xóa hết text và đang focus → hiện lại lịch sử
                    if (query.isEmpty() && edtCity.hasFocus()) {
                        List<String> h = loadSearchHistory();
                        if (!h.isEmpty()) {
                            historyAdapter.updateList(h);
                            showHistory();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    /** Lọn danh sách thành phố từ file assets/cities.txt */
    private void loadCitiesFromAssets() {
        try {
            InputStream is = getAssets().open("cities.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String city = line.trim();
                if (!city.isEmpty()) allCities.add(city);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Lọ danh sách thành phố theo từ khóa, tối đa 6 kết quả */
    private List<String> filterCities(String query) {
        List<String> result = new ArrayList<>();
        String lower = query.toLowerCase();
        for (String city : allCities) {
            if (city.toLowerCase().startsWith(lower)) {
                result.add(city);
                if (result.size() >= 6) break;  // chỉ hiện tối đa 6 gợi ý
            }
        }
        // Nếu startsWith không đủ  6 → tìm thêm các city chứa query
        if (result.size() < 6) {
            for (String city : allCities) {
                if (!city.toLowerCase().startsWith(lower)
                        && city.toLowerCase().contains(lower)) {
                    result.add(city);
                    if (result.size() >= 6) break;
                }
            }
        }
        return result;
    }

    private void showSuggestion() {
        layoutSuggestion.setVisibility(View.VISIBLE);
    }

    private void hideSuggestion() {
        layoutSuggestion.setVisibility(View.GONE);
    }

    // ===================== END SUGGESTION =====================

    private void loadLastOrCurrentLocation() {
        String lastCity = sharedPreferences.getString(Constants.LAST_CITY, "");
        if (!lastCity.isEmpty()) {
            currentCity = lastCity;
            getWeatherDataByCity(lastCity);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                getWeatherDataByLocation(location.getLatitude(), location.getLongitude());
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherDataByCity(String city) {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getClient().getCurrentWeatherByCity(city, Constants.API_KEY, currentUnit)
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        } else {
                            // HTTP 4xx/5xx: thành phố không tìm thấy hoặc lỗi server
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this,
                                    "Không tìm thấy thành phố: \"" + city + "\"",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        ApiClient.getClient().getForecastByCity(city, Constants.API_KEY, currentUnit)
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Fix 3: Truyền currentUnit vào HourlyAdapter để hiển thị đúng °C/°F
                            rvHourly.setAdapter(new HourlyAdapter(response.body().list, currentUnit));
                            // Daily: filter 1 item/ngày
                            rvForecast.setAdapter(new ForecastAdapter(filterDailyForecast(response.body().list), currentUnit));
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getWeatherDataByLocation(double lat, double lon) {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getClient().getCurrentWeatherByLocation(lat, lon, Constants.API_KEY, currentUnit)
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                            // Fix 5: null-safe trước khi lưu tên thành phố từ GPS
                            String gpsCity = response.body().cityName;
                            if (gpsCity != null && !gpsCity.isEmpty()) {
                                currentCity = gpsCity;
                                saveLastCity(gpsCity);
                            }
                        } else {
                            // HTTP 4xx/5xx: không lấy được thời tiết theo vị trí
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this,
                                    "Không thể lấy dữ liệu thời tiết cho vị trí này",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        ApiClient.getClient().getForecastByLocation(lat, lon, Constants.API_KEY, currentUnit)
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Hourly: toàn bộ 40 items, không filter
                            // Fix 3: Truyền currentUnit vào HourlyAdapter để hiển thị đúng °C/°F
                            rvHourly.setAdapter(new HourlyAdapter(response.body().list, currentUnit));
                            // Daily: filter 1 item/ngày
                            rvForecast.setAdapter(new ForecastAdapter(filterDailyForecast(response.body().list), currentUnit));
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void updateUI(CurrentWeatherResponse weather) {
        tvCityName.setText(weather.cityName);

        // Đơn vị nhiệt độ
        String tempUnit = currentUnit.equals(Constants.UNIT_METRIC) ? "°C" : "°F";

        // Nhiệt độ chính với đơn vị đúng
        tvTemperature.setText(Math.round(weather.main.temp) + tempUnit);
        tvDescription.setText(weather.weather.get(0).description);
        tvHumidity.setText(weather.main.humidity + "%");

        // Tốc độ gió
        tvWindSpeed.setText(weather.wind.speed + " m/s");

        // Cảm giác như
        tvFeelsLike.setText(Math.round(weather.main.feelsLike) + tempUnit);

        // Tầm nhìn (chuyển sang km)
        int visKm = weather.visibility / 1000;
        tvVisibility.setText(visKm + " km");

        // Quốc gia, Sunrise, Sunset
        if (weather.sys != null) {
            tvCountry.setText(weather.sys.country);
            String sunriseStr = formatUnixTime(weather.sys.sunrise, weather.timezone);
            String sunsetStr  = formatUnixTime(weather.sys.sunset,  weather.timezone);
            tvSunrise.setText(sunriseStr);
            tvSunset.setText(sunsetStr);
        }

        // Icon thời tiết
        String iconUrl = "https://openweathermap.org/img/wn/" + weather.weather.get(0).icon + "@4x.png";
        Glide.with(this).load(iconUrl).into(imgWeatherIcon);
    }

    /**
     * Chuyển Unix timestamp + timezone offset sang chuỗi HH:mm
     */
    private String formatUnixTime(long unixTime, long timezone) {
        java.util.Date date = new java.util.Date((unixTime + timezone) * 1000L);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    private void saveLastCity(String city) {
        sharedPreferences.edit().putString(Constants.LAST_CITY, city).apply();
    }

    /**
     * Lọc danh sách forecast từ 40 items (mỗi 3h) xuống còn 1 item/ngày.
     * Ưu tiên lấy mốc 12:00:00, nếu không có thì lấy item đầu tiên của ngày đó.
     */
    private List<ForecastResponse.ForecastItem> filterDailyForecast(List<ForecastResponse.ForecastItem> allItems) {
        List<ForecastResponse.ForecastItem> dailyList = new ArrayList<>();
        String lastDate = "";
        // Pass 1: lấy item 12:00:00 của mỗi ngày
        for (ForecastResponse.ForecastItem item : allItems) {
            if (item.dateTime != null && item.dateTime.contains("12:00:00")) {
                String date = item.dateTime.substring(0, 10);
                if (!date.equals(lastDate)) {
                    dailyList.add(item);
                    lastDate = date;
                }
            }
        }
        // Pass 2: với ngày chưa có item 12:00:00, lấy item đầu tiên của ngày đó
        if (dailyList.size() < 5) {
            lastDate = "";
            for (ForecastResponse.ForecastItem item : allItems) {
                if (item.dateTime == null) continue;
                String date = item.dateTime.substring(0, 10);
                boolean alreadyAdded = false;
                for (ForecastResponse.ForecastItem added : dailyList) {
                    if (added.dateTime != null && added.dateTime.startsWith(date)) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded && !date.equals(lastDate)) {
                    dailyList.add(item);
                    lastDate = date;
                }
            }
        }
        return dailyList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }
}
