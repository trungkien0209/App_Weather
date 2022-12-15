package ctk43.phantrungkien.app_weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<Weather> weatherList;

    public CustomAdapter(Context context, int layout, List<Weather> weatherList) {
        this.context = context;
        this.layout = layout;
        this.weatherList = weatherList;
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder {
        Weather weather;
        TextView tvDay;
        TextView tvStatus;
        ImageView ivIcon;
        TextView maxTemp;
        TextView minTemp;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.it_weather_detail, null);

            viewHolder.weather = weatherList.get(i);
            viewHolder.tvDay = view.findViewById(R.id.txtDateTimelv);
            viewHolder.tvStatus = view.findViewById(R.id.txtStatuslv);
            viewHolder.ivIcon = (ImageView) view.findViewById(R.id.ivIconlv);
            viewHolder.maxTemp = view.findViewById(R.id.txtMaxTemp);
            viewHolder.minTemp = view.findViewById(R.id.txtMinTemp);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Weather weather = weatherList.get(i);
        viewHolder.tvDay.setText(weather.Day);
        viewHolder.tvStatus.setText(weather.Status);
        viewHolder.maxTemp.setText(weather.MaxTemp + "°C");
        viewHolder.minTemp.setText(weather.MinTemp + "°C");
        String urlIcon = "https://raw.githubusercontent.com/trungkien0209/picture/main/" + weather.Image + ".png";
        Picasso.get().load(urlIcon).into(viewHolder.ivIcon);
        return view;
    }
}
