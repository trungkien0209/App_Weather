package ctk43.phantrungkien.history;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ctk43.phantrungkien.app_weather.R;

public class History extends AppCompatActivity {

    ImageView ivBack;

    String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        ImageView back = (ImageView) findViewById(R.id.back_history);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dataInsert();
    }

    private void dataInsert() {
        DataHelper helper=new DataHelper(History.this);
        for (int i=0;i<30;i++){

        }
    }


}
