package ctk43.phantrungkien.app_weather;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import ctk43.phantrungkien.app_weather.R;

public class Detail extends AppCompatActivity {
    TextView txtTemp,txtStatus,txtTempFeel,txtWind,txtGust,txtHumi,txtPres,txtCloud,txtVis;
    ImageView imgIcon;
    String name="";

    DecimalFormat df = new DecimalFormat("#.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_details);
        AnhXa();
        Intent intent = getIntent();
        String query = intent.getStringExtra("city");
        Log.d("ketqua", "Dư lieu truyen qua: " + query);
        if (query.equals("")) {
            name = "Dalat";
            getDetailWeather(name);
        } else {
            name = query;
            getDetailWeather(name);
        }
    }

    private void getDetailWeather(String data) {
        String url="https://api.openweathermap.org/data/2.5/weather?q="+data+"&lang=vi&units=metric&appid=331951ae05f0dec57a67c06074cd1db2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String status=jsonObjectWeather.getString("description");
                    txtStatus.setText(status);
                    String icon = jsonObjectWeather.getString("icon");
                    String urlIcon = "https://raw.githubusercontent.com/trungkien0209/picture/main/"+icon+".png";
                    Picasso.get().load(urlIcon).into(imgIcon);

                    JSONObject jsonObjectMain=jsonResponse.getJSONObject("main");
                    String temp=jsonObjectMain.getString("temp");
                    Double a = Double.valueOf(temp);
                    String Temp = String.valueOf(a.intValue());
                    txtTemp.setText(Temp+"°C");

                    String tempFeel=jsonObjectMain.getString("feels_like");
                    Double b=Double.valueOf(tempFeel);
                    String TempFeel=String.valueOf(b.intValue());
                    txtTempFeel.setText(TempFeel+"°C");
                    String pressure=jsonObjectMain.getString("pressure");
                    txtPres.setText(pressure+" mb");
                    String humidity=jsonObjectMain.getString("humidity");
                    txtHumi.setText(humidity+" %");

                    JSONObject jsonObjectWind=jsonResponse.getJSONObject("wind");
                    String wind=jsonObjectWind.getString("speed");
                    float x=Float.valueOf(wind)*36/10;
                    String Wind=df.format(x);
                    txtWind.setText(Wind+" km/h");

                    String gust=jsonObjectWind.getString("gust");
                    float y=Float.valueOf(gust)*36/10;
                    String GUST=df.format(y);
                    txtGust.setText(GUST+" km/h");

                    String visibility=jsonResponse.getString("visibility");
                    int vis=Integer.valueOf(visibility)/1000;
                    String Vis=String.valueOf(vis);
                    txtVis.setText(Vis+" km");

                    JSONObject jsonObjectCloud=jsonResponse.getJSONObject("cloud");
                    String cloud=jsonObjectCloud.getString("all");
                    txtCloud.setText(cloud+" %");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        txtTemp=findViewById(R.id.txtTempDetail);
        txtTempFeel=findViewById(R.id.txtFeelDetail);
        txtStatus=findViewById(R.id.txtStatusDetail);
        txtWind=findViewById(R.id.txtWindDetail);
        txtHumi=findViewById(R.id.txtHumidityDetail);
        txtGust=findViewById(R.id.txtGustDetail);
        txtPres=findViewById(R.id.txtPressureDetail);
        txtCloud=findViewById(R.id.txtCloudDetail);
        txtVis=findViewById(R.id.txtVisibilityDetail);
        imgIcon=findViewById(R.id.imgIconDetail);
    }
}
