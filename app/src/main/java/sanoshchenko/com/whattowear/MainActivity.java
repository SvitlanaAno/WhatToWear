package sanoshchenko.com.whattowear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class  MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather currentWeather;
    // Defining Permission codes.
    private static final int LOCATION_PERMISSION_CODE = 100;
    private TextView timeLabel;
    private TextView temperatureLabel;
    private TextView locationLabel;
    private ImageView iconImageView;
    private ImageView imageRefresh;
    private ProgressBar progressBar;
    private ImageSwitcher Switch;
    private int mCurIndex;
    private int[] mImageIds = {R.drawable.close_sunny_20, R.drawable.close_sunny_21, R.drawable.close_sunny_22,
            R.drawable.close_sunny_23};

    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 50, wayLongitude = 30;
    private Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        timeLabel = (TextView) findViewById(R.id.timeLabel);
        temperatureLabel = (TextView) findViewById(R.id.temperatureLabel);
        locationLabel = (TextView) findViewById(R.id.locationLabel);
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        imageRefresh = (ImageView) findViewById(R.id.imageRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Switch = (ImageSwitcher) findViewById(R.id.imageSwitcher);
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

        imageRefresh.setOnClickListener(v -> {
            getCity(wayLatitude, wayLongitude);
            getForecast(wayLatitude, wayLongitude);
        });
        getForecast(wayLatitude, wayLongitude);
    }
    private void getLocation() {
        // Checking if permission is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] Permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, Permissions, LOCATION_PERMISSION_CODE);
            }
        }else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                wayLatitude =  mLastLocation.getLatitude();
                                wayLongitude = mLastLocation.getLongitude();
                                locationLabel.setText(getCity(wayLatitude, wayLongitude));
                            } else {
                                locationLabel.setText("Mountain V");
                            }
                        }
        });
        }
    }

    // Get Address from latitude and longitude //
    private String getCity(double latitude, double longitude) {

        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                //  fullAdd = address.getAddressLine(0);
                fullAdd = address.getLocality();
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fullAdd;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                requestCode);
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                }
            });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
    }
*/



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
        locationLabel.setText(getCity(wayLatitude,wayLongitude));
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
