package com.example.Weather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import dme.forecastiolib.FIOCurrently;
import dme.forecastiolib.FIODaily;
import dme.forecastiolib.ForecastIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {

    public  static  final double k =  0.75008;

	public static View page;
	public static List<View> pages = new ArrayList<View>();
	public static LayoutInflater inflater;

	public static ForecastIO forecastIO_data(String Latitude, String Longitude) throws IOException {
		ForecastIO fio = new ForecastIO("2d8543d8b1e828512669fd42b4492c30");
		fio.setUnits(ForecastIO.UNITS_SI);
		fio.setExcludeURL("hourly,minutely,flags,alerts");
		String url = fio.getUrl(Latitude,Longitude);
		URL weather = new URL(url);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						weather.openStream()));

		String inputLine;
		String r = "";

		while ((inputLine = in.readLine()) != null)
			r += inputLine;

		in.close();
		fio.getForecast(r);
		return fio;
	}

	public static int icon_weather(String icon, String precipType)  {
		if (precipType.equals("hail")) {
			return R.drawable.strong_snow;
		}

		if (precipType.equals("fog")) {
			return R.drawable.fog;
		}

		if (precipType == null) {
			return R.drawable.sun;
		}

		if (icon.equals("cloudy")) {
			if (precipType == null) {
				return R.drawable.could;
			} else if (precipType.equals("rain")) {
				return R.drawable.rain;
			} else if (precipType.equals("snow")) {
				return R.drawable.show;
			} else if (precipType.equals("sleet")) {
				return R.drawable.strong_snow;
			}
		}

		if (icon.equals("partly-cloudy-day")) {
			if (precipType.equals("rain"))
				return R.drawable.sun_could_rain;
			else if (precipType.equals("snow"))
				return R.drawable.sun_could_show;
			return R.drawable.sun_could;

		}

		if (icon.equals("partly-cloudy-night")) {
			if (precipType.equals("rain"))
				return R.drawable.night_could_rain;
			else if (precipType.equals("snow"))
				return R.drawable.night_could_show;
			return R.drawable.night_could;
		}

		if (icon.equals("clear-night")) {
			return R.drawable.night;
		}

		if (icon.equals("clear-day")) {
			return R.drawable.sun;
		}

		return R.drawable.sun_red;
	}

	public static int num_daily_data(FIODaily daily, String time)  {
		String [] a = time.split(" ");
		for(int i = 0; i<daily.days(); i++){
			String [] s =  daily.getDay(i).getByKey("time").split(" ");
			if (!a[0].equals(s[0])) {
				return i;
			}
		}
		return -1;
	}

	public static void make_weather (String Latitude, String Longitude, String city) {
		ForecastIO fio = null;
		try {
			fio = forecastIO_data(Latitude,Longitude);
		} catch (IOException e) {
			e.printStackTrace();
		}

		FIOCurrently currently = new FIOCurrently(fio);
		FIODaily daily = new FIODaily(fio);
		page = inflater.inflate(R.layout.page, null);

		ImageView Icon = (ImageView) page.findViewById(R.id.imageView);
		TextView City =  (TextView) page.findViewById(R.id.textView);
		TextView Temperature =  (TextView) page.findViewById(R.id.textView1);
		TextView Pressure  =  (TextView) page.findViewById(R.id.textView2) ;
		TextView PrecipProbability = (TextView) page.findViewById(R.id.textView3) ;
		TextView Humidity  = (TextView) page.findViewById(R.id.textView4) ;
		TextView Temperature_1 =  (TextView) page.findViewById(R.id.textView8);
		TextView Temperature_2 =  (TextView) page.findViewById(R.id.textView9);
		TextView Temperature_3 =  (TextView) page.findViewById(R.id.textView10);
		TextView Date_1 =  (TextView) page.findViewById(R.id.textView5);
		TextView Date_2 =  (TextView) page.findViewById(R.id.textView6);
		TextView Date_3 =  (TextView) page.findViewById(R.id.textView7);
		ImageView Icon_1 = (ImageView) page.findViewById(R.id.imageView1);
		ImageView Icon_2 = (ImageView) page.findViewById(R.id.imageView2);
		ImageView Icon_3 = (ImageView) page.findViewById(R.id.imageView3);
		City.setText(city);
		Icon.setImageResource(icon_weather(currently.get().getByKey("icon").replaceAll("\"", ""), currently.get().getByKey("precipType").replaceAll("\"", "")));
		PrecipProbability.setText("Вероятность осадков: " + (int) (Double.parseDouble(currently.get().getByKey("precipProbability")) * 100) + " %");
		Humidity.setText("Отн. влажность: " + Double.parseDouble(currently.get().getByKey("humidity").substring(0,4)) * 100 + " %");
		Temperature.setText(currently.get().getByKey("temperature").substring(0,4) + " °C");
		Pressure.setText(String.valueOf(Double.parseDouble(currently.get().getByKey("pressure")) * k).substring(0,6) + " мм.  рт. стлб");

		int num_day_begin = num_daily_data(daily,currently.get().getByKey("time"));
		Temperature_1.setText(daily.getDay(num_day_begin).getByKey("temperatureMin").substring(0,4) + " °C");
		Temperature_2.setText(daily.getDay(num_day_begin + 1).getByKey("temperatureMin").substring(0,4) + " °C");
		Temperature_3.setText(daily.getDay(num_day_begin + 2).getByKey("temperatureMin").substring(0,4) + " °C");
		String [] s;
		s = daily.getDay(num_day_begin).getByKey("time").split(" ");
		Date_1.setText(s[0]);
		s = daily.getDay(num_day_begin+1).getByKey("time").split(" ");
		Date_2.setText(s[0]);
		s = daily.getDay(num_day_begin+2).getByKey("time").split(" ");
		Date_3.setText(s[0]);
		Icon_1.setImageResource(icon_weather(daily.getDay(num_day_begin).getByKey("icon").replaceAll("\"", ""), daily.getDay(num_day_begin).getByKey("precipType").replaceAll("\"", "")));
		Icon_2.setImageResource(icon_weather(daily.getDay(num_day_begin+1).getByKey("icon").replaceAll("\"",""),daily.getDay(num_day_begin+1).getByKey("precipType").replaceAll("\"","")));
		Icon_3.setImageResource(icon_weather(daily.getDay(num_day_begin+2).getByKey("icon").replaceAll("\"",""),daily.getDay(num_day_begin+2).getByKey("precipType").replaceAll("\"","")));

		pages.add(page);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		inflater = LayoutInflater.from(this);

		make_weather("59.95", "30.316667","Санкт-Петербург");
		make_weather("55.751667", "37.617778","Москва");
		make_weather("50.4505", "30.523", "Киев");
		SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(1);
		setContentView(viewPager);
	}
}
