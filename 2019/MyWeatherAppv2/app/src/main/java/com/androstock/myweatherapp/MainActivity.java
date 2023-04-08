// Aditya Kendre
// January 2019 - March 2019
// MainActivity.java
// Imports and reads data

package com.androstock.myweatherapp;

// dependencies
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    // Importing TextView elements
    TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field,
            wind_field,weatherIcon, updatedField, rangeTemp_field, upperWear, bottomWear, background;
    Typeface weatherFont;
    String city = "Harrisburg, PA, US";
    //API KEY
    String OPEN_WEATHER_MAP_API = "4006ccd4a4004aee0d2e6b86b4e14b92";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // defines object with TextView in main activity
        background = (TextView) findViewById(R.id.backgroundParent);
        upperWear = (TextView) findViewById(R.id.upperWear);
        bottomWear = (TextView) findViewById(R.id.bottomWear);
        selectCity = (TextView) findViewById(R.id.selectCity);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        wind_field = (TextView) findViewById(R.id.wind_field);
        rangeTemp_field = (TextView) findViewById(R.id.rangeTemp_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        taskLoadUp(city);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Box for changing city
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change City");
                final EditText input = new EditText(MainActivity.this);
                input.setHint(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    // Calls API
    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    // "Opens" JASON data
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    double T = main.getDouble("temp")-273 * (9/5) + 32;

                    // Inputs Data into TextView in the app
                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTemperatureField.setText(String.format("%.0f", main.getDouble("temp")-273 * (9/5) + 32) + "°");
                    humidity_field.setText("Humidity\n"+main.getString("humidity"));
                    wind_field.setText("Wind\n"+wind.getString("speed"));
                    rangeTemp_field.setText("Range\n" + String.format("%.0f", main.getDouble("temp_min")-273 * (9/5) + 32) + "° - " + String.format("%.0f", main.getDouble("temp_max")-273) + "°" );
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    // Attire
                    if(T >= 60) {
                        upperWear.setBackgroundResource(R.drawable.shirt);
                        bottomWear.setBackgroundResource(R.drawable.shorts);
                    }
                    else if( T >= 50){
                        upperWear.setBackgroundResource(R.drawable.longsleeve);
                        bottomWear.setBackgroundResource(R.drawable.shorts);
                    }
                    else if( T >= 35) {
                        upperWear.setBackgroundResource(R.drawable.longsleeve);
                        bottomWear.setBackgroundResource(R.drawable.pants);
                    }
                    else{
                        upperWear.setBackgroundResource(R.drawable.hoodie);
                        bottomWear.setBackgroundResource(R.drawable.pants);
                    }
                }
            } catch (JSONException e) {
                // Error if city is not found
                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }


        }



    }



}