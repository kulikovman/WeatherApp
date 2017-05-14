package ru.kulikovman.weather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import ru.kulikovman.weather.Common.Common;
import ru.kulikovman.weather.Helper.Helper;
import ru.kulikovman.weather.Model.OpenWeatherMap;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;
    ProgressBar loadindCircle;

    //SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    LocationManager locationManager;
    Location location;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Control
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);
        loadindCircle = (ProgressBar) findViewById(R.id.loadindCircle);

        //loadData();

        //Get Coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
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

    //кнопка для принудительной отправки запроса с координатами
    public void onClick(View view) {
        if (location != null) {
            new GetWeather().execute();
        }
    }

    //формируем запрос с координатами и получаем ответ от сервера
    private class GetWeather extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadindCircle.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String request = Common.apiRequest(String.valueOf(lat), String.valueOf(lng));
            return Helper.getHTTPData(request);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();

            openWeatherMap = gson.fromJson(response, mType);

            txtCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
            txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s / %s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),
                    Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("%.1f °C", openWeatherMap.getMain().getTemp()));

            Picasso.with(MainActivity.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            loadindCircle.setVisibility(View.INVISIBLE);
        }
    }

    public void saveData() {
        //SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        /*SharedPreferences.Editor ed = sPref.edit();
        ed.putString("txtCity", txtCity.getText().toString());
        ed.putString("txtLastUpdate", txtLastUpdate.getText().toString());
        ed.putString("txtDescription", txtDescription.getText().toString());
        ed.putString("txtHumidity", txtHumidity.getText().toString());
        ed.putString("txtTime", txtTime.getText().toString());
        ed.putString("txtCelsius", txtCelsius.getText().toString());
        ed.apply();*/
    }

    public void loadData() {
        //SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        //String savedText = sPref.getString("txtCity", "");
        /*if (sPref != null) {
            txtCity.setText(sPref.getString("txtCity", ""));
            txtLastUpdate.setText(sPref.getString("txtLastUpdate", ""));
            txtDescription.setText(sPref.getString("txtDescription", ""));
            txtHumidity.setText(sPref.getString("txtHumidity", ""));
            txtTime.setText(sPref.getString("txtTime", ""));
            txtCelsius.setText(sPref.getString("txtCelsius", ""));
        }*/
    }
}
