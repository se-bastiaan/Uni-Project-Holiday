package nl.teamone.projectholiday.api;

import java.util.Date;

import nl.teamone.projectholiday.api.objects.Location;
import nl.teamone.projectholiday.api.objects.PredictionType;
import nl.teamone.projectholiday.api.objects.WeatherDay;
import nl.teamone.projectholiday.api.objects.WeatherPeriod;
import nl.teamone.projectholiday.api.responses.openweathermap.Response;
import nl.teamone.projectholiday.api.services.OpenWeatherMapService;
import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

public class OpenWeatherMapApi extends DataRetriever {

    public static final String requiredMode = "json";
    public static final String baseURL = "http://api.openweathermap.org";

    public static Observable<WeatherPeriod> getWeatherData(final Location loc, final Date from, final Date to) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .build();

        OpenWeatherMapService apiRetriever = restAdapter.create(OpenWeatherMapService.class);

        return apiRetriever.getWeatherForecastByCoordinates(
                Double.toString(loc.latitude),
                Double.toString(loc.longitude),
                Integer.toString(getDuration(from, to)),
                requiredMode)
                .map(new Func1<Response, WeatherPeriod>() {
                    @Override
                    public WeatherPeriod call(Response response) {
                        return processResponse(response, from, to, loc);
                    }
                });
    }

    public static PredictionType getBestPredictionType(Date from, Date to) {
        Date now = new Date();
        if ((to.getTime() - (15* DAY_IN_MILLIS)) > now.getTime())
            return PredictionType.NODATA;
        return PredictionType.FORECAST;
    }

    private static String getQueryLocation(Location location) {
        return location.city.toLowerCase() + "," + location.countryISO.toLowerCase();
    }

    private static WeatherPeriod processResponse(Response response, Date from, Date to, Location loc) {
        WeatherPeriod period = new WeatherPeriod(from, to);
        if (response.cod != 200) {
            // Did not return with HTML status code 200 (OK)
            // Return the period immediately. period.bestPredictionType will be equal to NODATA
            return period;
        }

        // We got a valid response, let's populate the data:
        for (int i = 0; i < response.cnt && i < response.list.size(); i++) {
            Date dailyDate = new Date(from.getTime() + (i * DAY_IN_MILLIS));
            WeatherDay day = new WeatherDay(dailyDate, loc, PredictionType.FORECAST);

            // Set the data to match the day.
            day.setRainAmountInMillimeter((int) response.list.get(i).rain._3h);
            day.setRainPercentChance(0); //TODO: Figure this shit out.
            day.setTemperatureHigh((int) response.list.get(i).temp.max);
            day.setTemperatureLow((int) response.list.get(i).temp.min);
            day.setTemperatureMean((int) response.list.get(i).temp.day);
            day.setTemperatureFeelsLike(0); //TODO: Also figure this shit out.
            day.setWindSpeed((int) response.list.get(i).wind.speed);

            period.addWeatherDay(day);
        }

        period.calculateTotalDays();
        return period;
    }
}
