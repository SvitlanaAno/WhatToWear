package sanoshchenko.com.whattowear;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
    private ImageView imageRefresh;
    private ProgressBar progressBar;
    private ImageSwitcher Switch;
    private int mCurIndex;
    private int[] mImageIds = {R.drawable.close_sunny_20, R.drawable.close_sunny_21,R.drawable.close_sunny_22,
            R.drawable.close_sunny_23};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final double latitude = 50.394368;
        final double longitude = 30.619696;
        timeLabel = (TextView)findViewById(R.id.timeLabel);
        temperatureLabel = (TextView)findViewById(R.id.temperatureLabel);
        iconImageView = (ImageView)findViewById(R.id.iconImageView);
        imageRefresh = (ImageView)findViewById(R.id.imageRefresh);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        Switch = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        progressBar.setVisibility(View.INVISIBLE);

        YoYo.with(Techniques.FlipInX)
                .duration(1000)
                .playOn(findViewById(R.id.imageSwitcher));

        Switch.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                FrameLayout.LayoutParams params = new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                imageView.setLayoutParams(params);
                return imageView;
            }
        });

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
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
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
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (progressBar.getVisibility() == View.INVISIBLE){
            progressBar.setVisibility(View.VISIBLE);
            imageRefresh.setVisibility(View.INVISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            imageRefresh.setVisibility(View.VISIBLE);
        }
        }

    private void updateDisplay() {
        timeLabel.setText("At " + currentWeather.getFormattedTime() +  " it will be");
        temperatureLabel.setText(String.valueOf(currentWeather.getTemperature()));
        Drawable drawable = ContextCompat.getDrawable(this, currentWeather.getIconId());
        mImageIds =  currentWeather.getImageCloses();

        iconImageView.setImageDrawable(drawable);

        mCurIndex = 0;
        Switch.setImageResource(mImageIds[mCurIndex]);
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
        if (mCurIndex == mImageIds.length - 1) {
            mCurIndex = 0;
            Switch.setImageResource(mImageIds[mCurIndex]);
        } else {
            Switch.setImageResource(mImageIds[++mCurIndex]);
        }
    }
}
