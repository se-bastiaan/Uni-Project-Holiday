package nl.teamone.projectholiday.api;

import java.util.Date;

import nl.teamone.projectholiday.api.objects.Location;
import nl.teamone.projectholiday.api.objects.PredictionType;
import nl.teamone.projectholiday.api.objects.WeatherDay;
import nl.teamone.projectholiday.api.objects.WeatherPeriod;
import rx.Observable;
import rx.functions.Action1;

public abstract class DataRetriever {

    public static Observable<WeatherPeriod> getWeatherData(Location loc, Date from, Date to, Action1<WeatherPeriod> subscriber) {
        throw new NoSuchMethodError();
    }

    public static WeatherDay getCurrentWeather(Location loc) {
        throw new NoSuchMethodError();
    }

    public static PredictionType getBestPredictionType(Date from, Date to) {
        throw new NoSuchMethodError();
    }

}
