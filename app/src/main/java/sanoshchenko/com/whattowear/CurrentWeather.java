package sanoshchenko.com.whattowear;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sanoshchenko on 5/7/16.
 */
public class CurrentWeather {
    private String icon;
    private long time;
    private double temperature;
    private double humidity;
    private double precipChance;
    private String summary;
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconId(){
        //  clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
        int iconId = R.drawable.clear_day;

        if(this.icon.equals("clear-day")){
            iconId = R.drawable.clear_day;
        }
        else if(this.icon.equals("clear-night")){
            iconId = R.drawable.clear_night;
        }
        else if(this.icon.equals("rain")){
            iconId = R.drawable.rain;
        }
        else if(this.icon.equals("snow")){
            iconId = R.drawable.snow;
        }
        else if (this.icon.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (this.icon.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (this.icon.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (this.icon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (this.icon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (this.icon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }


    public int[] getImageCloses(){
        int imageCloses1 = R.drawable.close_sunny_20;
        int imageCloses2 = R.drawable.close_sunny_21;
        int imageCloses3 = R.drawable.close_sunny_22;
        int imageCloses4 = R.drawable.close_sunny_23;


        if(this.temperature >= 10 && this.temperature <20) {
            if (this.icon.equals("rain") || this.icon.equals("fog") || this.icon.equals("cloudy") ||
                    this.icon.equals("partly-cloudy-night") || this.icon.equals("snow")) {
                imageCloses1 = R.drawable.close_rain_16;
                imageCloses2 = R.drawable.close_rain_17;
                imageCloses3 = R.drawable.close_rain_18;
                imageCloses4 = R.drawable.close_rain_19;
            } else if (this.icon.equals("clear-day") || this.icon.equals("clear-night") || this.icon.equals("wind") ||
                    this.icon.equals("partly-cloudy-day")) {
                imageCloses1 = R.drawable.close_sunny_16;
                imageCloses2 = R.drawable.close_sunny_17;
                imageCloses3 = R.drawable.close_sunny_18;
                imageCloses4 = R.drawable.close_sunny_19;
            } else if (this.temperature >= 20 && this.temperature <24) {
                if (this.icon.equals("rain") || this.icon.equals("fog") || this.icon.equals("cloudy") ||
                        this.icon.equals("partly-cloudy-night") || this.icon.equals("snow")) {
                    imageCloses1 = R.drawable.close_rain_20;
                    imageCloses2 = R.drawable.close_rain_21;
                    imageCloses3 = R.drawable.close_rain_22;
                    imageCloses4 = R.drawable.close_rain_23;
                } else if (this.icon.equals("clear-day") || this.icon.equals("clear-night") || this.icon.equals("wind") ||
                        this.icon.equals("partly-cloudy-day")) {
                    imageCloses1 = R.drawable.close_sunny_20;
                    imageCloses2 = R.drawable.close_sunny_21;
                    imageCloses3 = R.drawable.close_sunny_22;
                    imageCloses4 = R.drawable.close_sunny_23;
                }
            } else if (this.temperature >= 24 && this.temperature < 29) {
                if (this.icon.equals("rain") || this.icon.equals("fog") || this.icon.equals("cloudy") ||
                        this.icon.equals("partly-cloudy-night") || this.icon.equals("snow")) {
                    imageCloses1 = R.drawable.close_rain_24;
                    imageCloses2 = R.drawable.close_rain_25;
                    imageCloses3 = R.drawable.close_rain_26;
                    imageCloses4 = R.drawable.close_rain_27;
                } else if (this.icon.equals("clear-day") || this.icon.equals("clear-night") || this.icon.equals("wind") ||
                        this.icon.equals("partly-cloudy-day")) {
                    imageCloses1 = R.drawable.close_sunny_24;
                    imageCloses2 = R.drawable.close_sunny_25;
                    imageCloses3 = R.drawable.close_sunny_26;
                    imageCloses4 = R.drawable.close_sunny_27;
                }
            }
        }

        int[] imagesCloses = {imageCloses1, imageCloses2, imageCloses3, imageCloses4};
        return imagesCloses;
    }



    public long getTime() {
        return time;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(this.timeZone));
        Date dateTime = new Date(this.time * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTemperature() {
        return (int)Math.round(temperature);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
