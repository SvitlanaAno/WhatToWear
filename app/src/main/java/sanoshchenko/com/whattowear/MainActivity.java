package sanoshchenko.com.whattowear;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather currentWeather;

    private TextView timeLabel;
    private TextView temperatureLabel;
    private ImageView iconImageView;
    private ImageView imageCloseView1;
    private ImageView imageCloseView2;
    private ImageView imageCloseView3;
    private ImageView imageCloseView4;
    private ImageView imageRefresh;
    private ImageSwitcher Switch;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final double latitude = 50.394368;
        final double longitude = 30.619696;
        //ButterKnife.bind(this);

        timeLabel = (TextView)findViewById(R.id.timeLabel);
        temperatureLabel = (TextView)findViewById(R.id.temperatureLabel);
        iconImageView = (ImageView)findViewById(R.id.iconImageView);
        imageRefresh = (ImageView)findViewById(R.id.imageRefresh);
        imageCloseView1 = (ImageView)findViewById(R.id.imageCloses1);
        imageCloseView2 = (ImageView)findViewById(R.id.imageCloses2);
       // imageCloseView3 = (ImageView)findViewById(R.id.imageCloses3);
       // imageCloseView4 = (ImageView)findViewById(R.id.imageCloses4);
        Switch = (ImageSwitcher)findViewById(R.id.imageSwitcher);

        imageRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });
        getForecast(latitude, longitude);
    }

    private void getForecast(double latitude, double longitude) {

        String apiKey = "6cebf3df5759f15aa40332217e2e7581";
        String host = "api.forecast.io";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("forecast")
                .addPathSegment(apiKey)
                .addPathSegment(latitude + "," + longitude)
                .addQueryParameter("units", "si")
                .build();

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            currentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught", e);
                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        timeLabel.setText("At " + currentWeather.getFormattedTime() +  " it will be");
        temperatureLabel.setText(String.valueOf(currentWeather.getTemperature()));
        Drawable drawable = ContextCompat.getDrawable(this, currentWeather.getIconId());
        int intImage1[] =  currentWeather.getImageCloses();
        Drawable drawableImage1 = ContextCompat.getDrawable(this, intImage1[0]);
        Drawable drawableImage2 = ContextCompat.getDrawable(this, intImage1[1]);
       // Drawable drawableImage3 = ContextCompat.getDrawable(this, intImage1[2]);
       // Drawable drawableImage4 = ContextCompat.getDrawable(this, intImage1[3]);
        iconImageView.setImageDrawable(drawable);
        imageCloseView1.setImageDrawable(drawableImage1);
        imageCloseView2.setImageDrawable(drawableImage2);
     //   imageCloseView3.setImageDrawable(drawableImage3);
      //  imageCloseView4.setImageDrawable(drawableImage4);
    }


    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather1 = new CurrentWeather();

        currentWeather1.setHumidity(currently.getDouble("humidity"));
        currentWeather1.setTime(currently.getLong("time"));
        currentWeather1.setIcon(currently.getString("icon"));
        currentWeather1.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather1.setSummary(currently.getString("summary"));
        currentWeather1.setTemperature(currently.getDouble("temperature"));
        currentWeather1.setTimeZone(timezone);

        return currentWeather1;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo !=null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment alert = new AlertDialogFragment();
        alert.show(getFragmentManager(), "error_dialog");

    }

    public void onSwitcherClick(View view) {
        Switch.showNext();
    }
}
