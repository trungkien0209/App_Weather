package ctk43.phantrungkien.app_weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ctk43.phantrungkien.history.History;


public class Home extends AppCompatActivity implements LocationListener {

    private static final String url_gif = "https://github.com/trungkien0209/picture/blob/main/20211226_110011.gif?raw=true";
    private static final String url_gif1 = "https://github.com/trungkien0209/picture/blob/main/20211226_190541.gif?raw=true";
    private static final String url_gif2 = "https://github.com/trungkien0209/picture/blob/main/20211226_085649.gif?raw=true";
    private static final String url_gif3 = "https://media.giphy.com/media/xT0xetq8fErCoByquk/giphy.gif";
    private static final String url_gif4 = "https://i.pinimg.com/originals/84/04/6d/84046da36518327ffe0ee437fe7f1af9.gif";
    private static final String LOG_TAG = "OptionMenuExample";
    private SearchView searchView;
    private final int REQ_CODE_SPEECH_OUTPUT = 143;

    public String Temp;
    public String wind;

    public String soA;
    public String soB;

    private String lastQuery;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "331951ae05f0dec57a67c06074cd1db2";
    String City = "";

    String urlIcon;
    String urlIconApi1;
    String urlIconApi2;

    LocationManager location;

    DecimalFormat df = new DecimalFormat("#.##");

    TextView txt_name, txt_country, txt_tem, txt_humidity, txt_clouds, txt_winds, txt_day,
            txt_status, txtDetails, txt_temperature1, txt_status1, txt_temperature2, txt_status2;
    ImageView icon_weather, gif, icon_weather1, icon_weather2;
    Button btn_forecast;
    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        AnhXa();
        //getWeatherDetails("Dalat");

        gif = findViewById(R.id.gif_image);
        Glide.with(this).load(url_gif).into(gif);


        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        getWeatherDetailsAuto();


        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getWeatherDetailsAuto();
                getWeatherDetails(City);
                pullToRefresh.setRefreshing(false);
            }
        });

        btn_forecast = findViewById(R.id.btn_forecast);
        btn_forecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Forecast.class);
                intent.putExtra("city", City);
                startActivity(intent);
            }
        });
        txtDetails = findViewById(R.id.btnDetails);
        txtDetails.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Detail.class);
                intent.putExtra("city", City);
                startActivity(intent);
            }
        });
    }


    private void AnhXa() {
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_country = (TextView) findViewById(R.id.txt_Country);
        icon_weather = (ImageView) findViewById(R.id.icon_weather);
        txt_tem = (TextView) findViewById(R.id.txt_temperature);
        txt_humidity = (TextView) findViewById(R.id.txt_humidity);
        txt_clouds = (TextView) findViewById(R.id.txt_clouds);
        txt_winds = (TextView) findViewById(R.id.txt_wind);
        txt_day = (TextView) findViewById(R.id.txt_day);
        txt_status = (TextView) findViewById(R.id.txt_status);
        icon_weather1 = (ImageView) findViewById(R.id.icon_weather1);
        txt_temperature1 = (TextView) findViewById(R.id.txt_temperature1);
        txt_status1 = (TextView) findViewById(R.id.txt_status1);
        icon_weather2 = (ImageView) findViewById(R.id.icon_weather_api2);
        txt_temperature2 = (TextView) findViewById(R.id.txt_temperature2);
        txt_status2 = (TextView) findViewById(R.id.txt_status2);
    }

    public void getWeatherDetails(String data) {
        String tempUrl = "";
        String city = data.toString().trim();
        if (city.equals("")) {
            txt_name.setText("City field can not be empty!");
        } else {
            tempUrl = url + "?q=" + city + "&lang=vi&units=metric&appid=" + appid;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String cityName = jsonResponse.getString("name");
                        txt_name.setText(cityName);
                        GetOpenMapApi(cityName);
                        GetWeatherBitApi(cityName);
                        if (cityName.equals("Tinh Binh GJinh")) {
                            txt_name.setText("Bình Định");
                        } else {
                            if (cityName.equals("Dalat")){
                                txt_name.setText("Đà Lạt");
                            }else {
                                txt_name.setText(cityName);
                            }
                        }

                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        txt_country.setText(countryName);

                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String icon = jsonObjectWeather.getString("icon");
                        urlIcon = "https://raw.githubusercontent.com/trungkien0209/picture/main/" + icon + ".png";
                        Picasso.get().load(urlIcon).into(icon_weather);

                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        String temp = jsonObjectMain.getString("temp");
                        Double a = Double.valueOf(temp);
                        Temp = String.valueOf(a.intValue());
                        txt_tem.setText(Temp + "°C");

                        String status = jsonObjectWeather.getString("description");
                        String result = "";
                        status.toLowerCase();
                        status.split(" ");
                        result += String.valueOf(status.charAt(0)).toUpperCase() + status.substring(1) + " ";
                        if (!result.equals("") && result.equals(null)) {
                            result = result.substring(0, result.length() - 1);
                        }
                        txt_status.setText(result);
                        if (status.equals("mây đen u ám")) {
                            gif = findViewById(R.id.gif_image);
                            Glide.with(Home.this).load(url_gif1).into(gif);
                        } else {
                            if (status.equals("mây cụm") || status.equals("mây rải rác") || status.equals("mây thưa")) {
                                gif = findViewById(R.id.gif_image);
                                Glide.with(Home.this).load(url_gif2).into(gif);
                            } else {
                                if (status.equals("sương mù") || status.equals("sương mờ") ) {
                                    gif = findViewById(R.id.gif_image);
                                    Glide.with(Home.this).load(url_gif3).into(gif);
                                } else {
                                    if (status.equals("mưa cường độ nặng") || status.equals("ánh sáng cường độ mưa") || status.equals("mưa vừa")) {
                                        gif = findViewById(R.id.gif_image);
                                        Glide.with(Home.this).load(url_gif4).into(gif);
                                    } else {
                                        if (status.equals("bầu trời quang đãng")) {
                                            gif = findViewById(R.id.gif_image);
                                            Glide.with(Home.this).load(url_gif).into(gif);
                                        }
                                    }
                                }
                            }
                        }

                        String humidity = jsonObjectMain.getString("humidity");
                        txt_humidity.setText(humidity + "%");

                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        txt_clouds.setText(clouds + "%");

                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        wind = jsonObjectWind.getString("speed");
                        Double g = Double.valueOf(wind);
                        wind = String.valueOf(g.intValue());
                        txt_winds.setText(wind + "m/s");

                        String Day = jsonResponse.getString("dt");
                        long l = Long.parseLong(Day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
                        Date date = new Date(l * 1000);
                        String day = dateFormat.format(date);
                        txt_day.setText(day);

                        if (cityName.equals("")) {
                            City = "Saigon";
                            getWeatherDetails(City);
                        } else {
                            City = cityName;
                        }

                        btn_forecast = (Button) findViewById(R.id.btn_forecast);
                        btn_forecast.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Home.this, Forecast.class);
                                intent.putExtra("city", City);
                                startActivity(intent);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menuItem_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Nhập vị trí");
        // Configure the search info and add any event listeners...

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getWeatherDetails(query);

                txtDetails = findViewById(R.id.btnDetails);
                txtDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home.this, Detail.class);
                        intent.putExtra("city", City);
                        startActivity(intent);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_open:
                startActivity(new Intent(Home.this, History.class));
                break;
            case R.id.item_Voice:
                btnToOpenMic();
                break;
            case R.id.it_TempC:
                Double g = Double.valueOf(Temp);
                String tempC = String.valueOf(g.intValue());
                txt_tem.setText(tempC + "°C");
                break;
            case R.id.it_TempF:
                Double h = Double.valueOf(Temp) * 1.8 + 32;
                String tempF = String.valueOf(h.intValue());
                txt_tem.setText(tempF + "°F");
                break;
            case R.id.it_windK:
                Double k = Double.valueOf(wind) * 3.6;
                String windK = String.valueOf(k.intValue());
                txt_winds.setText(windK + "km/h");
                break;
            case R.id.it_windM:
                Double m = Double.valueOf(wind);
                String windM = String.valueOf(m.intValue());
                txt_winds.setText(windM + "m/s");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void btnToOpenMic() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Vị trí bạn muốn xem thời tiết là .....");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_OUTPUT);
        } catch (ActivityNotFoundException tim) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_OUTPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> voiceIntent = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getWeatherDetails(voiceIntent.get(0));
                }
                break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getWeatherDetailsAuto() {
        try {
            location = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 500, Home.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        soA = "" + location.getLatitude();
        soB = "" + location.getLongitude();

        String tempUrl = "";
        tempUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + soA + "&lon=" + soB + "&lang=vi&units=metric&appid=331951ae05f0dec57a67c06074cd1db2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String cityName = jsonResponse.getString("name");
                    if (cityName.equals("Tinh Binh GJinh")) {
                        txt_name.setText("Bình Định");
                        GetWeatherBitApi("binhdinh");
                    } else {
                        txt_name.setText(cityName);
                    }
                    GetOpenMapApi(cityName);



                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    txt_country.setText(countryName);

                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String icon = jsonObjectWeather.getString("icon");
                    urlIcon = "https://raw.githubusercontent.com/trungkien0209/picture/main/" + icon + ".png";
                    Picasso.get().load(urlIcon).into(icon_weather);

                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    String temp = jsonObjectMain.getString("temp");
                    Double a = Double.valueOf(temp);
                    Temp = String.valueOf(a.intValue());
                    txt_tem.setText(Temp + "°C");

                    String status = jsonObjectWeather.getString("description");
                    String result = "";
                    status.toLowerCase();
                    status.split(" ");
                    result += String.valueOf(status.charAt(0)).toUpperCase() + status.substring(1) + " ";
                    if (!result.equals("") && result.equals(null)) {
                        result = result.substring(0, result.length() - 1);
                    }
                    txt_status.setText(result);
                    if (status.equals("mây đen u ám")) {
                        gif = findViewById(R.id.gif_image);
                        Glide.with(Home.this).load(url_gif1).into(gif);
                    } else {
                        if (status.equals("mây cụm") || status.equals("mây rải rác") || status.equals("mây thưa")) {
                            gif = findViewById(R.id.gif_image);
                            Glide.with(Home.this).load(url_gif2).into(gif);
                        } else {
                            if (status.equals("sương mù") || status.equals("sương mờ") ) {
                                gif = findViewById(R.id.gif_image);
                                Glide.with(Home.this).load(url_gif3).into(gif);
                            } else {
                                if (status.equals("mưa cường độ nặng") || status.equals("ánh sáng cường độ mưa") || status.equals("mưa vừa")) {
                                    gif = findViewById(R.id.gif_image);
                                    Glide.with(Home.this).load(url_gif4).into(gif);
                                } else {
                                    if (status.equals("bầu trời quang đãng")) {
                                        gif = findViewById(R.id.gif_image);
                                        Glide.with(Home.this).load(url_gif).into(gif);
                                    }
                                }
                            }
                        }
                    }

                    String humidity = jsonObjectMain.getString("humidity");
                    txt_humidity.setText(humidity + "%");

                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    txt_clouds.setText(clouds + "%");

                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    wind = jsonObjectWind.getString("speed");
                    Double g = Double.valueOf(wind);
                    wind = String.valueOf(g.intValue());
                    txt_winds.setText(wind + "m/s");

                    String Day = jsonResponse.getString("dt");
                    long l = Long.parseLong(Day);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
                    Date date = new Date(l * 1000);
                    String day = dateFormat.format(date);
                    txt_day.setText(day);

                    if (cityName.equals("")) {
                        City = "Saigon";
                        getWeatherDetails(City);
                    } else {
                        City = cityName;
                    }

                    btn_forecast = (Button) findViewById(R.id.btn_forecast);
                    btn_forecast.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Home.this, Forecast.class);
                            intent.putExtra("city", City);
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void GetWeatherBitApi(String data1) {
        String city1 = data1.toString().trim();
        String tempUrl1 = "";
        tempUrl1 = "https://api.weatherbit.io/v2.0/forecast/daily?city="+ city1 + "&key=86ac8ce2e250452b9734644f1899be58";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl1, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

                    JSONObject jsonObjectIcon = jsonObjectWeather.getJSONObject("weather");
                    String icon2 = jsonObjectIcon.getString("icon");
                    urlIconApi2 = "https://raw.githubusercontent.com/trungkien0209/picture/main/" + icon2 + ".png";
                    Picasso.get().load(urlIconApi2).into(icon_weather2);


                    String temp = jsonObjectWeather.getString("temp");
                    Double a = Double.valueOf(temp);
                    Temp = String.valueOf(a.intValue());
                    txt_temperature2.setText(Temp + "°C");

                    JSONObject jsonObjectDes = jsonObjectWeather.getJSONObject("weather");
                    String des = jsonObjectDes.getString("description");
                    if(des.equals("Overcast clouds") ){
                        txt_status2.setText("Mây u ám");
                    }else{
                        if(des.equals("Unknown Precipitation") ) {
                            txt_status2.setText("Lượng mưa không xác định");
                        }else{
                            if(des.equals("Clear Sky") ) {
                                txt_status2.setText("Bầu trời quang đãng");
                            }
                            else{
                                if(des.equals("Thunderstorm with light rain") ) {
                                    txt_status2.setText("Giông (Mưa nhỏ)");
                                }else{
                                    if(des.equals("Thunderstorm with rain") || des.equals("Thunderstorm with light drizzle") || des.equals("Thunderstorm with drizzle") || des.equals("Thunderstorm with heavy drizzle")) {
                                        txt_status2.setText("Giông bão");
                                    }else{
                                        if(des.equals("Thunderstorm with heavy rain") || des.equals("Thunderstorm with Hail") ) {
                                            txt_status2.setText("Giông lốc");
                                        }else{
                                            if(des.equals("Light Drizzle") ||des.equals("Drizzle") || des.equals("Heavy Drizzle")) {
                                                txt_status2.setText("Mưa phùn");
                                            }else{
                                                if(des.equals("Light Rain") || des.equals("Moderate rain") || des.equals("Freezing rain")) {
                                                    txt_status2.setText("Mưa");
                                                }else{
                                                    if(des.equals("Heavy Rain") ) {
                                                        txt_status2.setText("Mưa lớn");
                                                    }else{
                                                        if(des.equals("Light shower rain") ||des.equals("Shower rain") || des.equals("Heavy shower rain")) {
                                                            txt_status2.setText("Mưa rào");
                                                        }else{
                                                            if(des.equals("Light snow") || des.equals("Snow") || des.equals("Heavy Snow") || des.equals("Snow shower") || des.equals("Heavy snow shower") || des.equals("Flurries")) {
                                                                txt_status2.setText("Tuyết rơi");
                                                            }else{
                                                                if(des.equals("Mix snow/rain")) {
                                                                    txt_status2.setText("Tuyết rơi + mưa");
                                                                }else{
                                                                    if(des.equals("Heavy sleet") || des.equals("Sleet")) {
                                                                        txt_status2.setText("Mưa đá");
                                                                    }else{
                                                                        if(des.equals("Mist") || des.equals("Smoke") ||  des.equals("Haze") || des.equals("Sand/dust") || des.equals("Fog") ||des.equals("Freezing Fog")) {
                                                                            txt_status2.setText("Sương mù");
                                                                        }
                                                                        else{
                                                                            if(des.equals("Few clouds") || des.equals("Scattered clouds") ) {
                                                                                txt_status2.setText("Mây rải rác");
                                                                            }else{
                                                                                if(des.equals("Broken clouds") ) {
                                                                                    txt_status2.setText("Mây cụm");
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void GetOpenMapApi(String data1) {
        String city1 = data1.toString().trim();
        String tempUrl1 = "";
        tempUrl1 = "http://api.openweathermap.org/data/2.5/forecast?q="+ city1 +"&units=metric&cnt=20&lang=vi&appid=331951ae05f0dec57a67c06074cd1db2";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl1, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("list");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

                    JSONArray jsonObjectIcon = jsonObjectWeather.getJSONArray("weather");
                    JSONObject ic = jsonObjectIcon.getJSONObject(0);
                    String icon1 = ic.getString("icon");
                    urlIconApi1 = "https://raw.githubusercontent.com/trungkien0209/picture/main/" + icon1 + ".png";
                    Picasso.get().load(urlIconApi1).into(icon_weather1);

                    JSONObject jsonObjectMain = jsonObjectWeather.getJSONObject("main");
                    String temp = jsonObjectMain.getString("temp");
                    Double a = Double.valueOf(temp);
                    Temp = String.valueOf(a.intValue());
                    txt_temperature1.setText(Temp + "°C");

                    JSONArray jsonObjectDes = jsonObjectWeather.getJSONArray("weather");
                    JSONObject bv = jsonObjectDes.getJSONObject(0);
                    String des = bv.getString("description");
                    String result = "";
                    des.toLowerCase();
                    des.split(" ");
                    result += String.valueOf(des.charAt(0)).toUpperCase() + des.substring(1) + " ";
                    if (!result.equals("") && result.equals(null)) {
                        result = result.substring(0, result.length() - 1);
                    }
                    txt_status1.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

}