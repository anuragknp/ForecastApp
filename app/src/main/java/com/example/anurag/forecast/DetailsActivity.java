package com.example.anurag.forecast;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class DetailsActivity extends AppCompatActivity {
    private JSONObject daily;
    private JSONArray data;
    private JSONObject hourly;
    private JSONArray hourly_data;
    private String state;
    private String city;
    private Context context;
    private String degree_unit;
    private String unit;
    private ToggleButton daybtn;
    private ToggleButton hourbtn;
    private ForecastUtils utils  = new ForecastUtils();
    private RelativeLayout hour;
    private RelativeLayout day;

    public void setDayData() {
        try {
            for(int i = 0; i < 7; i++) {
                JSONObject day = (JSONObject) data.get((i+1)%8);
                Context context = getApplicationContext();
                int text1 = context.getResources().getIdentifier("day" + i + "0", "id", context.getPackageName());
                int text2 = context.getResources().getIdentifier("day"+i+"2", "id", context.getPackageName());
                int img = context.getResources().getIdentifier("day"+i+"1", "id", context.getPackageName());
                TextView t1 = (TextView) findViewById(text1);
                TextView t2 = (TextView) findViewById(text2);
                ImageView image = (ImageView) findViewById(img);

                t1.setText(utils.getFormattedDate(day.getInt("time")));

                String icon = utils.getIcon(day.getString("icon"));
                int imgid = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
                image.setImageResource(imgid);

                t2.setText(utils.getMinMaxTemperature(day.getInt("temperatureMin"), day.getInt("temperatureMax")));
            }
            TextView data = (TextView) findViewById(R.id.data);
            data.setText("More Details for " + city + ", " + state);
        } catch (Exception e) {
            //todo handle here
            TextView i = (TextView)findViewById(R.id.fahrenheit);

        }
    }

    public void setHourData(int start, int end) {
        try {
            TableLayout table = (TableLayout) findViewById(R.id.hours);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            for (int i = start; i < end; i++) {
                View row= inflater.inflate(R.layout.hour_row, null, false);
                TextView t1 = (TextView) row.findViewById(R.id.text1);
                TextView t2 = (TextView) row.findViewById(R.id.text2);
                ImageView img1 = (ImageView) row.findViewById(R.id.image1);
                t1.setId(utils.generateViewId());
                t1.setText(((JSONObject) hourly_data.get(i)).getString("time"));
                t2.setId(utils.generateViewId());
                t2.setText("" + ((JSONObject) hourly_data.get(i)).getInt("temperature"));
                img1.setId(utils.generateViewId());
                String icon = utils.getIcon(((JSONObject) hourly_data.get(i)).getString("icon"));
                int id = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
                img1.setImageResource(id);
                if(i%2 == 0) {
                    row.setBackgroundColor(getResources().getColor(R.color.grey));
                }
                table.addView(row);
            }
            if (start == 0) {
                View row= inflater.inflate(R.layout.hour_more, null, false);
                Button btn = (Button) row.findViewById(R.id.morebtn);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow row = (TableRow) findViewById(R.id.morebtnview);
                        row.setVisibility(View.GONE);
                        setHourData(24, 48);
                    }});
                row.setBackgroundColor(getResources().getColor(R.color.grey));
                table.addView(row);
            }
        } catch(Exception e) {
            //todo
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        try {
            daily = new JSONObject(getIntent().getExtras().getString("daily"));
            data = daily.getJSONArray("data");
            hourly = new JSONObject(getIntent().getExtras().getString("hourly"));
            hourly_data = hourly.getJSONArray("data");
            state = getIntent().getExtras().getString("state");
            city = getIntent().getExtras().getString("city");
            degree_unit = getIntent().getExtras().getString("degree_unit");
            unit = getIntent().getExtras().getString("unit");
            hourbtn = (ToggleButton) findViewById(R.id.hourbtn);
            daybtn = (ToggleButton) findViewById(R.id.daybtn);
            hour = (RelativeLayout) findViewById(R.id.hour);
            day = (RelativeLayout) findViewById(R.id.day);
            hour.setVisibility(View.VISIBLE);
            day.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.deg_unit)).setText("Temp(" + degree_unit + ")");
            context = getApplicationContext();

            setDayData();
            setHourData(0, 24);

        } catch (Exception e) {
            //todo: handle this
        }
    }

    public void hourbtn_click(View view) {
        if (daybtn.isChecked()) {
            daybtn.setChecked(false);
            day.setVisibility(View.GONE);
            hourbtn.setChecked(true);
            hour.setVisibility(View.VISIBLE);
        } else {
            hourbtn.setChecked(true);
        }
    }

    public void daybtn_click(View view) {
        if (hourbtn.isChecked()) {
            hourbtn.setChecked(false);
            hour.setVisibility(View.GONE);
            daybtn.setChecked(true);
            day.setVisibility(View.VISIBLE);
        } else {
            daybtn.setChecked(true);
        }
    }
}
