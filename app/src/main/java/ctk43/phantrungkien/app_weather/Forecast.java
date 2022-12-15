package ctk43.phantrungkien.app_weather;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Forecast extends AppCompatActivity {
    TextView txtCountry;
    ImageView ivBack;
    ListView lv5Day;
    CustomAdapter customAdapter;
    List<Weather> weatherList;

    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_screen);

        AnhXa();

        //Lấy dữ liệu tên thành phố từ class Home
        Intent intent = getIntent();
        String query = intent.getStringExtra("city");
        Log.d("ketqua", "Dư lieu truyen qua: " + query);
        if (query.equals("")) {
            name = "Dalat";
            Get5DaysData(name);
        } else {
            name = query;
            Get5DaysData(name);
        } if (name.equals("Tinh Binh GJinh")) {
            txtCountry.setText("Bình Định");
        } else {
            txtCountry.setText(name);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        customAdapter = new CustomAdapter(Forecast.this, R.layout.it_weather_detail, weatherList);
        lv5Day.setAdapter(customAdapter);
    }

    private void AnhXa() {
        txtCountry = findViewById(R.id.txtCountrylv);
        ivBack = findViewById(R.id.back_forecast);
        lv5Day = findViewById(R.id.lv5Days);
        weatherList = new ArrayList<>();

    }

    private void Get5DaysData(String data) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + data + "&units=metric&cnt=20&lang=vi&appid=331951ae05f0dec57a67c06074cd1db2";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArrayList = response.getJSONArray("list");
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String sNgay = jsonObjectList.getString("dt");
                                long l = Long.parseLong(sNgay);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("\tEEEE,"
                                        + System.getProperty("line.separator")
                                        + " dd/MM/yyyy"
                                        + System.getProperty("line.separator")
                                        + "\tHH:mm:ss");
                                Date date = new Date(l * 1000);
                                String day = dateFormat.format(date);

                                JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("main");
                                String maxTemp = jsonObjectTemp.getString("temp_max");
                                String minTemp = jsonObjectTemp.getString("temp_min");
                                Double a = Double.valueOf(maxTemp);
                                Double b = Double.valueOf(minTemp);
                                String Temp1 = String.valueOf(a.intValue());
                                String Temp2 = String.valueOf(b.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String status = jsonObjectWeather.getString("main");
                                String icon = jsonObjectWeather.getString("icon");

                                weatherList.add(new Weather(day, status, icon, Temp1, Temp2));
                            }
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(stringRequest);
    }
}
