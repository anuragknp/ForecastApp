package com.example.anurag.forecast;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.hamweather.aeris.communication.AerisEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText street;
    private EditText city;
    private Spinner state;
    private RadioGroup degree;
    private TextView error;
    private RadioButton fahrenheit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        street = (EditText)findViewById(R.id.street);
        city = (EditText)findViewById(R.id.city);
        state = (Spinner)findViewById(R.id.state);
        degree = (RadioGroup)findViewById(R.id.degree);
        error = (TextView)findViewById(R.id.error);
        fahrenheit = (RadioButton)findViewById(R.id.fahrenheit);
        AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private boolean validate() {
        error.setText("");
        if (street.getText().toString().trim().isEmpty()) {
            error.setText("Please enter the street name");
            return false;
        }
        if (city.getText().toString().trim().isEmpty()) {
            error.setText("Please enter a city");
            return false;
        }
        if (state.getSelectedItemPosition() == 0) {
            error.setText("Please select a state");
            return false;
        }
        return true;
    }

    private String getUnit() {
        String unit = "us";
        if (degree.getCheckedRadioButtonId() != fahrenheit.getId()) {
            unit = "si";
        }
        return unit;
    }

    private String getDegreeUnit() {
        String unit = "\u2109";
        if (degree.getCheckedRadioButtonId() != fahrenheit.getId()) {
            unit = "\u2103";
        }
        return unit;
    }

    public void search(View view) {
        if (!validate()) {return;}

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            GetForecastAsyncTask forecast = new GetForecastAsyncTask(getApplicationContext());
            forecast.execute(street.getText().toString().trim(), city.getText().toString().trim(),
                    state.getSelectedItem().toString(), getUnit());
        } else {
            error.setText("No network connection available.");
        }
    }

    public void clear(View view) {
        street.setText("");
        city.setText("");
        state.setSelection(0);
        degree.check(fahrenheit.getId());
        error.setText("");
    }

    public void about(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void forecast(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forecast.io"));
        startActivity(browserIntent);
    }

    private class GetForecastAsyncTask extends AsyncTask<String, Void, String> {

        Context context;
        private GetForecastAsyncTask(Context context) {
            super();
            this.context = context.getApplicationContext();
        }

        private String getData(String street,String city,String state,String unit) throws IOException, Exception {
            InputStream is = null;
            int len = 500000;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("forecast-hw8.elasticbeanstalk.com")
                        .appendPath("index.php")
                        .appendQueryParameter("street", street)
                        .appendQueryParameter("city", city)
                        .appendQueryParameter("state", state)
                        .appendQueryParameter("degree", unit);

                URL url = new URL(builder.build().toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int http_code = conn.getResponseCode();
                if (http_code != 200) {
                    throw new Exception("Server Error; HTTP CODE: "+http_code);
                }
                String contentAsString = readIt(conn.getInputStream());
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            StringBuilder out = new StringBuilder();
            char[] buffer = new char[1000];
            for (;;) {
                int rsz = reader.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                return getData(data[0], data[1], data[2], data[3]);
            } catch (IOException e) {
                return new String(e.toString());
            } catch (Exception e) {
                return new String(e.toString());
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject result = new JSONObject(response);
                Intent result_activity = new Intent(context.getApplicationContext(), ResultActivity.class);
                result_activity.putExtra("result", response);
                result_activity.putExtra("state", state.getSelectedItem().toString());
                result_activity.putExtra("city", city.getText().toString().trim());
                result_activity.putExtra("degree_unit", getDegreeUnit());
                result_activity.putExtra("unit", getUnit());
                startActivity(result_activity);
            } catch (JSONException e) {
                error.setText(response);
            }
        }
    }
}
