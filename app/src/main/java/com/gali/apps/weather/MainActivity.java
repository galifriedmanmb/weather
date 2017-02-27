package com.gali.apps.weather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_APP_ID = "c62d635c22411446d98d8e56b491bb2c";
    TextView weatherTV;
    ImageView weatherIV;

    //http://api.openweathermap.org/data/2.5/weather?q=london&APPID=c62d635c22411446d98d8e56b491bb2c&units=metric

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTV = (TextView)findViewById(R.id.weatherTV);
        weatherIV = (ImageView)findViewById(R.id.weatherIV);
        findViewById(R.id.goBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = ((EditText)findViewById(R.id.placeET)).getText().toString();
                if (place.length()>0) {
                    Downloader d = new Downloader();
                    String weatherStr = "http://api.openweathermap.org/data/2.5/weather?q=" + place + "&APPID=" + WEATHER_APP_ID;
                    d.execute(weatherStr);
                }

            }
        });
    }

    public class Downloader extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                while ((line=input.readLine())!=null) {
                    response.append(line+"\n");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input!=null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } if (connection!=null) {
                    connection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String jsonText) {
            Gson gson = new Gson();
            JSON json = gson.fromJson(jsonText, JSON.class);
            if (json!=null) {
                weatherTV.setText(json.weather[0].description);
                String picassoStr = "http://openweathermap.org/img/w/" + json.weather[0].icon + ".png";
                Log.d("icon", picassoStr);
                Picasso.with(MainActivity.this).load(picassoStr).into(weatherIV);
            }
        }
    }
}
