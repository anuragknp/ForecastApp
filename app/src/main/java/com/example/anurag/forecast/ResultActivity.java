package com.example.anurag.forecast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.hamweather.aeris.communication.AerisEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private JSONObject result;
    private JSONObject daily;
    private JSONObject currently;
    private JSONObject hourly;
    private String state;
    private String city;
    private String degree_unit;
    private String unit;
    private ForecastUtils utils = new ForecastUtils();
    CallbackManager callbackManager;
    ShareDialog shareDialog;



    private void setSummaryImage() {
        try {
            ImageView summary_img = (ImageView) findViewById(R.id.summary_img);
            Context context = summary_img.getContext();
            String icon = utils.getIcon(currently.getString("icon"));
            int id = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
            summary_img.setImageResource(id);
        } catch (Exception e) {
            //todo: handle this error
        }
    }

    private void setSummary() {
        TextView summary = (TextView) findViewById(R.id.summary);
        try {
            String temp = currently.getString("summary");
            temp += " in " + city + ", " + state;
            summary.setText(temp);
        } catch (Exception e) {
            summary.setText("N.A");
        }
    }

    private void setTemperature() {
        TextView temperature = (TextView) findViewById(R.id.temperature);
        TextView temperature_unit = (TextView) findViewById(R.id.temperatureunit);
        try {
            int temp = currently.getInt("temperature");
            temperature.setText("" + temp);
            temperature_unit.setText(degree_unit);
        } catch (Exception e) {
            temperature_unit.setText("N.A");
        }
    }

    private void setMinMaxTemperature() {
        TextView minmax = (TextView) findViewById(R.id.minmax);
        try {
            String temp;
            JSONArray data = daily.getJSONArray("data");
            int mint = ((JSONObject)data.get(0)).getInt("temperatureMin");
            int maxt = ((JSONObject)data.get(0)).getInt("temperatureMax");
            temp = utils.getLowHighTemperature(mint, maxt);
            minmax.setText(temp);
        } catch (Exception e) {
            minmax.setText("N.A");
        }
    }

    private void setPrecipitation() {
        TextView preci = (TextView) findViewById(R.id.precipitation);
        try {
            double precipitation = currently.getDouble("precipIntensity");
            String temp;
            if (unit.equals("si")) {
                precipitation = precipitation/25.4;
            }
            if (precipitation < 0.002) {
                temp = "None";
            } else if (precipitation < 0.017) {
                temp = "Very Light";
            } else if (precipitation < 0.1) {
                temp = "Light";
            } else if (precipitation < 0.4) {
                temp = "Moderate";
            } else {
                temp = "Heavy";
            }

            preci.setText(temp);
        } catch (Exception e) {
            preci.setText("N.A");
        }
    }

    private void setPrecipitationProbability() {
        TextView rain = (TextView) findViewById(R.id.rain);
        try {
            double precipProbability = currently.getDouble("precipProbability");

            rain.setText("" + Math.round(precipProbability*100)+"%");
        } catch (Exception e) {
            rain.setText("N.A");
        }
    }

    private void setWindSpeed() {
        TextView windspeed = (TextView) findViewById(R.id.windspeed);
        try {
            double speed = currently.getDouble("windSpeed");
            String temp;
            if (unit.equals("si")) {
                temp = "" + Math.round(speed*100.0)/100.0 + " mpsec";
            } else {
                temp = "" + Math.round(speed*100.0)/100.0 + " mph";
            }
            windspeed.setText(temp);
        } catch (Exception e) {
            windspeed.setText("N.A");
        }
    }

    private void setDewPoint() {
        TextView dew = (TextView) findViewById(R.id.dewpoint);
        try {
            String temp = Math.round(currently.getDouble("dewPoint"))+degree_unit;
            dew.setText(temp);
        } catch (Exception e) {
            dew.setText("N.A");
        }
    }

    private void setHumidity() {
        TextView humidity = (TextView) findViewById(R.id.humidity);
        try {
            String temp = Math.round(currently.getDouble("humidity")*100)+"%";
            humidity.setText(temp);
        } catch (Exception e) {
            humidity.setText("N.A");
        }
    }

    private void setVisibility() {
        TextView visibility = (TextView) findViewById(R.id.visibility );
        try {
            String temp;
            if (unit.equals("si")) {
                temp = "" + Math.round(currently.getDouble("visibility")*100.0)/100.0 +" kms";
            } else {
                temp = "" + Math.round(currently.getDouble("visibility")*100.0)/100.0 +" mi";
            }
            visibility.setText(temp);
        } catch (Exception e) {
            visibility.setText("N.A");
        }
    }

    private void setSunrise() {
        TextView sunrise = (TextView) findViewById(R.id.sunrise);
        try {
            String temp;
            JSONArray data = daily.getJSONArray("data");
            temp = ((JSONObject)data.get(0)).getString("sunriseTime");
            sunrise.setText(temp);
        } catch (Exception e) {
            sunrise.setText("N.A");
        }
    }

    private void setSunset() {
        TextView sunset = (TextView) findViewById(R.id.sunset);
        try {
            String temp;
            JSONArray data = daily.getJSONArray("data");
            temp = ((JSONObject)data.get(0)).getString("sunsetTime");
            sunset.setText(temp);
        } catch (Exception e) {
            sunset.setText("N.A");
        }
    }

    private void setResult() {
        setSummaryImage();
        setSummary();
        setTemperature();
        setMinMaxTemperature();
        setPrecipitation();
        setPrecipitationProbability();
        setWindSpeed();
        setDewPoint();
        setHumidity();
        setVisibility();
        setSunrise();
        setSunset();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_result);
        try {
            result = new JSONObject(getIntent().getExtras().getString("result"));
            currently = (JSONObject) result.get("currently");
            daily = (JSONObject) result.get("daily");
            hourly = (JSONObject) result.get("hourly");
            state = getIntent().getExtras().getString("state");
            city = getIntent().getExtras().getString("city");
            degree_unit = getIntent().getExtras().getString("degree_unit");
            unit = getIntent().getExtras().getString("unit");
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);
            AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);
            setResult();

        } catch (JSONException e) {
            //todo: handle this case
        }



    }

    public void open_details(View view) {
        Intent details_activity = new Intent(getApplicationContext(), DetailsActivity.class);
        details_activity.putExtra("daily", daily.toString());
        details_activity.putExtra("hourly", hourly.toString());
        details_activity.putExtra("state", state);
        details_activity.putExtra("city", city);
        details_activity.putExtra("degree_unit", degree_unit);
        details_activity.putExtra("unit", unit);
        startActivity(details_activity);
    }

    public void open_map(View view) {
        Intent map_activity = new Intent(getApplicationContext(), MapActivity.class);
        try {
            map_activity.putExtra("lat", ""+result.getDouble("latitude"));
            map_activity.putExtra("lng", ""+result.getDouble("longitude"));
        } catch (Exception e) {
            //todo
        }
        startActivity(map_activity);
    }

    public void fb_share(View view) {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                String icon = "http://cs-server.usc.edu:45678/hw/hw8/images/" + utils.getIcon(currently.getString("icon"))+".png";
                String desc = currently.getString("summary") + ", " + currently.getInt("temperature") + degree_unit;
                String title = "Current Weather in " + city + ", " + state;
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(title)
                        .setContentDescription(desc)
                        .setImageUrl(Uri.parse(icon))
                        .setContentUrl(Uri.parse("http://forecast.io"))
                        .build();
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        if (result.getPostId() == null) {
                            Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getApplicationContext(), "Facebook Post Successful", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Post Failed", Toast.LENGTH_SHORT).show();

                    }});
                shareDialog.show(linkContent);
            } catch(Exception e) {
                //todo
            }

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

