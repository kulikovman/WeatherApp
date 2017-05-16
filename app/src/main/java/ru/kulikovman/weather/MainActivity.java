package ru.kulikovman.weather;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    TextView tvTemperature, tvCity, tvLastUpdate, weatherIcon, tvDescription, tvHumidity, tvSunSetRise;
    ProgressBar pbLoading;

    /*OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    LocationManager locationManager;
    Location location;
    double lat, lng;*/

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
        weatherIcon = (TextView) findViewById(R.id.weatherIcon);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvSunSetRise = (TextView) findViewById(R.id.tvSunSetRise);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*@Override
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
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 100, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            new GetWeather().execute();
        }
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

    //кнопка обновления погоды
    public void onClick(View view) {
        if (location != null) {
            new GetWeather().execute();
        }
    }

    //получение погоды с сервера
    private class GetWeather extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String request = Common.apiRequest(String.valueOf(lat), String.valueOf(lng));
            return Helper.getHTTPData(request);

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response == null) {
                pbLoading.setVisibility(View.INVISIBLE);
                return;
            }

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();

            openWeatherMap = gson.fromJson(response, mType);

            tvCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            tvLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
            tvDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            tvHumidity.setText(String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            tvSunSetRise.setText(String.format("%s / %s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),
                    Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            tvTemperature.setText(String.format("%.1f °C", openWeatherMap.getMain().getTemp()));

            Picasso.with(MainActivity.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            pbLoading.setVisibility(View.INVISIBLE);

            Log.d("myLog", "Строка Gson успешно обработана");
        }
    }*/
}
