package ru.kulikovman.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

import ru.kulikovman.weather.Common.Common;
import ru.kulikovman.weather.Helper.Helper;
import ru.kulikovman.weather.Model.OpenWeatherMap;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView tvTemperature, tvCity, tvLastUpdate, tvDescription, tvHumidity, tvSunSetRise, tvWind,
            tvInternetConnection, weatherIcon, windIcon, humidityIcon, sunsetIcon, sunriseIcon;
    ProgressBar pbLoading;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    LocationManager locationManager;
    Location location;
    double lat, lng, windDeg;
    String provider, idWeather;
    long currentTime, sunsetTime, sunriseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Подключаем библиотеку Calligraphy и устанавливаем шрифт по умолчанию
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BebasNeue_Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_main);


        // Подключаем необходимые view элементы
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvLastUpdate = (TextView) findViewById(R.id.tvLastUpdate);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvWind = (TextView) findViewById(R.id.tvWind);
        tvSunSetRise = (TextView) findViewById(R.id.tvSunSetRise);
        tvInternetConnection = (TextView) findViewById(R.id.tvInternetConnection);

        weatherIcon = (TextView) findViewById(R.id.weatherIcon);
        humidityIcon = (TextView) findViewById(R.id.humidityIcon);
        windIcon = (TextView) findViewById(R.id.windIcon);
        sunriseIcon = (TextView) findViewById(R.id.sunriseIcon);
        sunsetIcon = (TextView) findViewById(R.id.sunsetIcon);

        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);


        // Получаем координаты телефона
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }
        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d("myLog", "Переменные lat1 | lng1 = " + lat + " | " + lng);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        Log.d("myLog", "Запущен GetWeather() из onResume");
        new GetWeather().execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("myLog", "Вызван метод onLocationChanged");
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d("myLog", "Переменные lat2 | lng2 = " + lat + " | " + lng);
        }

        Log.d("myLog", "Запущен GetWeather() из onLocationChanged");
        new GetWeather().execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    // Получаем погоду с сервера openweathermap.org
    private class GetWeather extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weatherIcon.setVisibility(View.INVISIBLE);
            pbLoading.setVisibility(View.VISIBLE);
            tvInternetConnection.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String request = Common.apiRequest(String.valueOf(lat), String.valueOf(lng));
            Log.d("myLog", "Сформировали запрос - " + request);
            return Helper.getHTTPData(request);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);


            // Если нет интернета, то выводим сообщение
            if (response == null) {
                Log.d("myLog", "При отправке запроса произошла ошибка");

                if (tvDescription.getText().toString().equals(" ")) {
                    tvInternetConnection.setVisibility(View.VISIBLE);
                } else weatherIcon.setVisibility(View.VISIBLE);

                pbLoading.setVisibility(View.INVISIBLE);
                return;
            }


            // Обрабатываем ответ сервера
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();

            openWeatherMap = gson.fromJson(response, mType);
            Log.d("myLog", "Ответ сервера успешно обработан");


            // Делаем иконку погоды видимой
            weatherIcon.setVisibility(View.VISIBLE);


            // Заносим полученные данные в текстовые поля
            tvCity.setText(String.format("%s", openWeatherMap.getName()));
            tvLastUpdate.setText(String.format("%s", Common.getDateNow()));
            tvDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            tvHumidity.setText(String.format("%d", openWeatherMap.getMain().getHumidity()));
            tvWind.setText(String.format("%.2f", openWeatherMap.getWind().getSpeed()));
            tvSunSetRise.setText(String.format("%s  |  %s",
                    Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),
                    Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            tvTemperature.setText(String.format("%.0f °C", openWeatherMap.getMain().getTemp()));


            // Устанавливаем иконку с направлением ветра
            windDeg = openWeatherMap.getWind().getDeg();
            if (windDeg >= 0 && windDeg <= 23) windIcon.setText(R.string.wi_direction_up);
            else if (windDeg >= 24 && windDeg <= 68)
                windIcon.setText(R.string.wi_direction_up_right);
            else if (windDeg >= 69 && windDeg <= 113) windIcon.setText(R.string.wi_direction_right);
            else if (windDeg >= 114 && windDeg <= 158)
                windIcon.setText(R.string.wi_direction_down_right);
            else if (windDeg >= 159 && windDeg <= 203) windIcon.setText(R.string.wi_direction_down);
            else if (windDeg >= 204 && windDeg <= 248)
                windIcon.setText(R.string.wi_direction_down_left);
            else if (windDeg >= 249 && windDeg <= 293) windIcon.setText(R.string.wi_direction_left);
            else if (windDeg >= 294 && windDeg <= 338)
                windIcon.setText(R.string.wi_direction_up_left);
            else windIcon.setText(R.string.wi_direction_up);


            // Устанавливаем иконку с изображением погоды
            currentTime = System.currentTimeMillis();
            sunriseTime = (long) openWeatherMap.getSys().getSunrise() * 1000;
            sunsetTime = (long) openWeatherMap.getSys().getSunset() * 1000;
            Log.d("myLog", String.valueOf(currentTime + ": " + sunriseTime + " | " + sunsetTime));

            idWeather = String.valueOf(openWeatherMap.getWeather().get(0).getId());
            Log.d("myLog", "idWeather = " + idWeather);
            pbLoading.setVisibility(View.INVISIBLE);

            if (currentTime > sunriseTime && currentTime < sunsetTime) {
                int nameIcon = getResources().getIdentifier("wi_owm_day_" + idWeather, "string", getPackageName());
                weatherIcon.setText(nameIcon);
            } else {
                int nameIcon = getResources().getIdentifier("wi_owm_night_" + idWeather, "string", getPackageName());
                weatherIcon.setText(nameIcon);
            }


            // Устанавливаем значки для оставшихся иконок
            humidityIcon.setText(R.string.wi_humidity);
            sunriseIcon.setText(R.string.wi_sunrise);
            sunsetIcon.setText(R.string.wi_sunset);


            Log.d("myLog", "Ответ расшифрован и раскидан по полям");
        }
    }
}
