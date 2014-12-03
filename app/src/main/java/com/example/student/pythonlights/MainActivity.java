package com.example.student.pythonlights;

//Joshua Angeley, Alex Aberman, Patrick McGee

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.location.*;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.lang.Thread;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	public static final String EXTRA_MESSAGE = "blah";
	public static boolean isConnected = false;


	// Global constants
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Global variable to hold the current location
	LocationClient mLocationClient;
	Location mCurrentLocation;
	Double currentTemp;
	ArrayList<Double> lowTemps = new ArrayList<Double>();
	ArrayList<Double> hiTemps = new ArrayList<Double>();
	ArrayList<String> movieTitles = new ArrayList<String>();
	ArrayList<String> movieRatings = new ArrayList<String>();
	ArrayList<String> movieSummaries = new ArrayList<String>();
	boolean sunny = false;


	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		isConnected = true;
	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(), this, 2);
			Log.w("test", "connection failed" + connectionResult.getErrorCode());
		}
	}


	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
			//...
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
				switch (resultCode) {
					case Activity.RESULT_OK:
					/*
					 * Try the request again
					 */
						//...
						break;
				}
				//...
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason.
			// resultCode holds the error code.
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode,
					this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =
						new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] DAYS = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday",
				"Thursday", "Friday", "Saturday"};

		setContentView(R.layout.activity_main);

//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.activity_main, DAYS);
//
//        ListView listView = (ListView) findViewById(R.id.day_list);
//        listView.setAdapter(adapter);
		Log.w("test", "this should start the app");
		/*
		 * Create a new location client, using the enclosing class to
		 * handle callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);


	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		Log.w("test", "Attempting to connect");
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	/*Called when ip address button is pressed*/
//    public void changeIpAddress(View view){
//        Intent intent = new Intent(this, IpAddressActivity.class);
//        EditText editIp = (EditText) findViewById(R.id.editText);
//        String ip_address = editIp.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, ip_address);
//        startActivity(intent);
//    }


	public void setLocation(View view) {
		mCurrentLocation = mLocationClient.getLastLocation();
		EditText editLocality = (EditText) findViewById(R.id.locality);
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		double lat = mCurrentLocation.getLatitude();
		double lng = mCurrentLocation.getLongitude();
		try {
			List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
			//if (addresses.size() > 0)
			//System.out.println(addresses.get(0).getLocality());
			editLocality.setText(addresses.get(0).getLocality());

		} catch (Exception e) {

		}
		//editLocality.setText(Location.convert(mCurrentLocation.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(mCurrentLocation.getLongitude(), Location.FORMAT_DEGREES));
	}

	public void getLocation(View view) {
		if (isConnected) {
			try {
				mCurrentLocation = mLocationClient.getLastLocation();
				Log.w("test", "mCurrentLocation is: " + mCurrentLocation);
			} catch (Exception e) {
				Log.w("test", e);
			}
		}
	}


	public String parseEntity(HttpEntity entity) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(entity.getContent()), 65728);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public double getTemperature(JSONObject body) {
		try {
			JSONObject mainVal = body.getJSONObject("main");
			return mainVal.getDouble("temp");
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public ArrayList<Double> getTemperatures(JSONArray body) {
		ArrayList<Double> temps = new ArrayList<Double>();
		try {
//            JSONObject mainVal = body.getJSONObject("list");
			for (int i = 0; i < body.length(); i++) {
				JSONObject mainVal = body.getJSONObject(i);
				JSONObject Objtemps = mainVal.getJSONObject("temp");
				temps.add(Objtemps.getDouble("day"));
			}

//            return temps.getDouble("day");
			return temps;

		} catch (JSONException e) {
			e.printStackTrace();
//            return -1;
		}
		return temps;
	}

	public void popTemperatures(JSONArray body) {
		ArrayList<Double> tempsLo = new ArrayList<Double>();
		ArrayList<Double> tempsHi = new ArrayList<Double>();

		try {
//            JSONObject mainVal = body.getJSONObject("list");
			for (int i = 0; i < body.length(); i++) {
				JSONObject mainVal = body.getJSONObject(i);
				JSONObject Objtemps = mainVal.getJSONObject("temp");
				tempsLo.add(Objtemps.getDouble("min"));
				tempsHi.add(Objtemps.getDouble("max"));
			}
			lowTemps = tempsLo;
			hiTemps = tempsHi;

//            return temps.getDouble("day");
//            return temps;

		} catch (JSONException e) {
			e.printStackTrace();
//            return -1;
		}
//        return temps;
	}

	public void sendWeatherLights(String ipAddress,boolean sunny){
		HttpPost post = new HttpPost("http://" + ipAddress + "/rpi");
		Log.w("test", "Before HttpClient");
		System.out.println(sunny);
		if (!sunny){

			for(int i = 1; i < 2; i ++){
				DefaultHttpClient client = new DefaultHttpClient();
				String lightString = "";
				for (int j = 1; j < 32; j += 2){
					if((j -1) % 4 != 2){
						// Blue
						if (i % 2 == 0){
							lightString += "{\"intensity\":0.75,\"red\":0,\"blue\":255,\"green\":0,\"lightId\":"+j%32+"},";
							Log.w("test", j+","+j%32+",Blue");
						}
						else{
							lightString += "{\"intensity\":0.75,\"red\":255,\"blue\":255,\"green\":255,\"lightId\":"+j%32+"},";
							Log.w("test", j+","+j%32+",White");
						}
					}
					else{
						//White
						if (i % 2 == 1){
							lightString += "{\"intensity\":0.75,\"red\":0,\"blue\":255,\"green\":0,\"lightId\":"+j%32+"},";
//                            Log.w("test", j+","+j%32+",Blue");
						}
						else{
							lightString += "{\"intensity\":0.75,\"red\":255,\"blue\":255,\"green\":255,\"lightId\":"+j%32+"},";
//                            Log.w("test", j+","+j%32+",White");
						}
//                        Log.w("test", ""+j%32);
					}
				}
				Log.v("test",lightString);
				lightString = lightString.substring(0,lightString.length()-1);
				StringEntity se = null;
				try {
					se = new StringEntity("{\"lights\":["+lightString+"],\"propagate\":true}");
					se.setContentType("application/json;charset=UTF-8");
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				post.setEntity(se);

				try {
					HttpResponse resp = client.execute(post);
					resp.getEntity();
					client.getConnectionManager().shutdown();
//                    Log.w("test", "POST data is sent to raspberry pi");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			DefaultHttpClient client = new DefaultHttpClient();
			StringEntity se = null;
			try {
				se = new StringEntity("{\"lights\":["+"{\"intensity\":0.75,\"red\":255,\"blue\":0,\"green\":102,\"lightId\":1}"+"],\"propagate\":true}");
				se.setContentType("application/json;charset=UTF-8");
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			post.setEntity(se);

			try {
				HttpResponse resp = client.execute(post);
				resp.getEntity();
				client.getConnectionManager().shutdown();
//                Log.w("test", "POST data is sent to raspberry pi");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void getMoviesFromJsonArray(JSONArray body) {
		ArrayList<String> tempMovieTitles = new ArrayList<String>();
		ArrayList<String> tempMovieRatings = new ArrayList<String>();
		ArrayList<String> tempMovieSummaries = new ArrayList<String>();

		try {
			for (int i = 0; i < body.length(); i++) {
				JSONObject mainVal = body.getJSONObject(i);
				Log.v("test", mainVal.toString());
				Log.v("test", mainVal.getString("title"));
				//JSONObject Objtitles = mainVal.getJSONObject("title");
				tempMovieTitles.add(mainVal.getString("title"));
				tempMovieSummaries.add(mainVal.getString("synopsis"));
				JSONObject ratings = mainVal.getJSONObject("ratings");
				tempMovieRatings.add(ratings.getString("critics_score"));
			}
			movieTitles = tempMovieTitles;
			movieRatings = tempMovieRatings;
			movieSummaries = tempMovieSummaries;


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*Called when update movies*/
	public void updateMovies(View view) {
		Runnable getMovies = new Runnable() {
			public void run(){
				String apikey = "a8hp9y82qahh6hsbq72xtpn3";
				HttpPost post = new HttpPost("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey="+apikey+"&page_limit=6");
				DefaultHttpClient client = new DefaultHttpClient();
				StringEntity se = null;
				try {
					HttpResponse resp = client.execute(post);
					HttpEntity entity = resp.getEntity();
					String body = parseEntity(entity);
					Log.v("test", body);
					try {
						JSONObject json = new JSONObject(body);
						Log.v("test", json.toString());
						JSONArray main = json.getJSONArray("movies");
						getMoviesFromJsonArray(main);
					}
					catch (JSONException j){
						j.printStackTrace();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}

			}
		};

		new Thread(getMovies).start();

		ArrayList<TextView> movieRenders = new ArrayList<TextView>();

		TextView movieTitle1 = (TextView) findViewById(R.id.title1);
		TextView movieRating1 = (TextView) findViewById(R.id.rating1);
		TextView movieSummary1 = (TextView) findViewById(R.id.summary1);
		TextView movieTitle2 = (TextView) findViewById(R.id.title2);
		TextView movieRating2 = (TextView) findViewById(R.id.rating2);
		TextView movieSummary2 = (TextView) findViewById(R.id.summary2);
		TextView movieTitle3 = (TextView) findViewById(R.id.title3);
		TextView movieRating3 = (TextView) findViewById(R.id.rating3);
		TextView movieSummary3 = (TextView) findViewById(R.id.summary3);
		TextView movieTitle4 = (TextView) findViewById(R.id.title4);
		TextView movieRating4 = (TextView) findViewById(R.id.rating4);
		TextView movieSummary4 = (TextView) findViewById(R.id.summary4);
		TextView movieTitle5 = (TextView) findViewById(R.id.title5);
		TextView movieRating5 = (TextView) findViewById(R.id.rating5);
		TextView movieSummary5 = (TextView) findViewById(R.id.summary5);
		TextView movieTitle6 = (TextView) findViewById(R.id.title6);
		TextView movieRating6 = (TextView) findViewById(R.id.rating6);
		TextView movieSummary6 = (TextView) findViewById(R.id.summary6);

		movieRenders.add(movieTitle1);
		movieRenders.add(movieRating1);
		movieRenders.add(movieSummary1);
		movieRenders.add(movieTitle2);
		movieRenders.add(movieRating2);
		movieRenders.add(movieSummary2);
		movieRenders.add(movieTitle3);
		movieRenders.add(movieRating3);
		movieRenders.add(movieSummary3);
		movieRenders.add(movieTitle4);
		movieRenders.add(movieRating4);
		movieRenders.add(movieSummary4);
		movieRenders.add(movieTitle5);
		movieRenders.add(movieRating5);
		movieRenders.add(movieSummary5);
		movieRenders.add(movieTitle6);
		movieRenders.add(movieRating6);
		movieRenders.add(movieSummary6);

//        if (!movies.isEmpty()) {
//            movieTitle1.setText(movies.get(0));
//
//            for(int i = 0; i < movies.size(); i++){
//                //set the movie title
//                movieRenders.get(i*3).setText(movies.get(i));
//            }
//            //movieTitle.setText(movies.get(0));
//        } else {
//            movieTitle1.setText("Updating");
//
//            for(int i = 0; i < 5; i++){
//                //set the movie title
//                movieRenders.get(i*3).setText("Updating");
//                //set the movie rating
//                movieRenders.get((i+1)*3).setText("Updating");
//                //set the movie summary
//                movieRenders.get((i+2)*3).setText("Updating");
//            }
//
//        }

	}

	public void processPost(HttpPost post){
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			String body = parseEntity(entity);
			try {
				JSONObject json = new JSONObject(body);
				JSONArray main = json.getJSONArray("list");
				popTemperatures(main);

				String[] outdoor = {"park","hike","Farmers Market"};
				String[] indoor = {"theater","cafe","bar","restaurant"};
				String consumerKey = "0VJ2QVBQYJqEBqNZ_0g6Xg";
				String consumerSecret = "oI4ReNB4OsjHYCnczq4J9_3HLQ8";
				String token = "_DUpeCYJqwWUjlXijdFIpNKvpQjAA_CV";
				String tokenSecret = "ljnwwyEaVkZyyuaR5fCket9RWyw";

				Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
				String response = yelp.search("burritos", 30.361471, -87.164326);

				System.out.println(response);
				boolean rainy = true;
				// If weather is rainy or chance of precipitation
				if (rainy){
					for (int i = 0; i < yelp.indoor.length; i++){
						String options = yelp.search(yelp.indoor[i],30.361471, -87.164326);
					}
				}
				else{
					for (int i = 0; i < yelp.outdoor.length; i++){
						String options = yelp.search(yelp.outdoor[i],30.361471, -87.164326);
					}
				}

				JSONObject day0W = (main.getJSONObject(0)).getJSONArray("weather").getJSONObject(0);
				JSONObject day1W = (main.getJSONObject(1)).getJSONArray("weather").getJSONObject(0);
				JSONObject day2W = (main.getJSONObject(2)).getJSONArray("weather").getJSONObject(0);
				JSONObject day3W = (main.getJSONObject(3)).getJSONArray("weather").getJSONObject(0);
				JSONObject day4W = (main.getJSONObject(4)).getJSONArray("weather").getJSONObject(0);

				System.out.println(day0W);
				System.out.println(day1W);
				System.out.println(day2W);
				System.out.println(day3W);
				System.out.println(day4W);

				String currentWeather = day0W.getString("main");
				sunny = currentWeather.equals("") || currentWeather.equals("Clear");
				System.out.println(currentWeather);
				String day0Icon = day0W.getString("icon");
				String day1Icon = day1W.getString("icon");
				String day2Icon = day2W.getString("icon");
				String day3Icon = day3W.getString("icon");
				String day4Icon = day4W.getString("icon");

				System.out.println(day0Icon);
				System.out.println(day1Icon);
				System.out.println(day2Icon);
				System.out.println(day3Icon);
				System.out.println(day4Icon);

				new DownloadImageTask((ImageView) findViewById(R.id.imageDay0))
						.execute("http://openweathermap.org/img/w/" + day0Icon + ".png");
				System.out.println("I got one!");

				new DownloadImageTask((ImageView) findViewById(R.id.imageDay1))
						.execute("http://openweathermap.org/img/w/" + day1Icon + ".png");

				new DownloadImageTask((ImageView) findViewById(R.id.imageDay2))
						.execute("http://openweathermap.org/img/w/" + day2Icon + ".png");

				new DownloadImageTask((ImageView) findViewById(R.id.imageDay3))
						.execute("http://openweathermap.org/img/w/" + day3Icon + ".png");

				new DownloadImageTask((ImageView) findViewById(R.id.imageDay4))
						.execute("http://openweathermap.org/img/w/" + day4Icon + ".png");

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fillMovie(final View view,boolean fill) {

		if (fill) {
            //Titles
            TextView titleRating = (TextView) findViewById(R.id.titleRating);
            titleRating.setText("Rating");
            TextView titleTitle = (TextView) findViewById(R.id.titleTitle);
            titleTitle.setText("Title");
            TextView titleSummary = (TextView) findViewById(R.id.titleSummary);
            titleSummary.setText("Summary");

            ArrayList<TextView> movieRenders = new ArrayList<TextView>();

            TextView movieTitle1 = (TextView) findViewById(R.id.title1);
            TextView movieRating1 = (TextView) findViewById(R.id.rating1);
            TextView movieSummary1 = (TextView) findViewById(R.id.summary1);
            TextView movieTitle2 = (TextView) findViewById(R.id.title2);
            TextView movieRating2 = (TextView) findViewById(R.id.rating2);
            TextView movieSummary2 = (TextView) findViewById(R.id.summary2);
            TextView movieTitle3 = (TextView) findViewById(R.id.title3);
            TextView movieRating3 = (TextView) findViewById(R.id.rating3);
            TextView movieSummary3 = (TextView) findViewById(R.id.summary3);
            TextView movieTitle4 = (TextView) findViewById(R.id.title4);
            TextView movieRating4 = (TextView) findViewById(R.id.rating4);
            TextView movieSummary4 = (TextView) findViewById(R.id.summary4);
            TextView movieTitle5 = (TextView) findViewById(R.id.title5);
            TextView movieRating5 = (TextView) findViewById(R.id.rating5);
            TextView movieSummary5 = (TextView) findViewById(R.id.summary5);
            TextView movieTitle6 = (TextView) findViewById(R.id.title6);
            TextView movieRating6 = (TextView) findViewById(R.id.rating6);
            TextView movieSummary6 = (TextView) findViewById(R.id.summary6);

            movieRenders.add(movieTitle1);
            movieRenders.add(movieRating1);
            movieRenders.add(movieSummary1);
            movieRenders.add(movieTitle2);
            movieRenders.add(movieRating2);
            movieRenders.add(movieSummary2);
            movieRenders.add(movieTitle3);
            movieRenders.add(movieRating3);
            movieRenders.add(movieSummary3);
            movieRenders.add(movieTitle4);
            movieRenders.add(movieRating4);
            movieRenders.add(movieSummary4);
            movieRenders.add(movieTitle5);
            movieRenders.add(movieRating5);
            movieRenders.add(movieSummary5);
            movieRenders.add(movieTitle6);
            movieRenders.add(movieRating6);
            movieRenders.add(movieSummary6);

            if (!movieTitles.isEmpty() && !movieRatings.isEmpty() && !movieSummaries.isEmpty()) {
    //                movieTitle1.setText(movies.get(0));

                for(int i = 0; i < movieTitles.size(); i++){
                    //set the movie title
                    movieRenders.get(i*3).setText(movieTitles.get(i));
                    //set the movie rating
                    movieRenders.get((i*3) + 1).setText(movieRatings.get(i));
                    //set the movie summary
                    movieRenders.get((i*3) + 2).setText(movieSummaries.get(i));
                }
                //movieTitle.setText(movies.get(0));
            } else {
    //                movieTitle1.setText("Updating");

                for (int i = 0; i < 6; i++) {
                    //set the movie title
                    movieRenders.get(i * 3).setText("Updating");
                    //set the movie rating
                    movieRenders.get((i * 3) + 1).setText("Updating");
                    //set the movie summary
                    movieRenders.get((i * 3) + 2).setText("Updating");
                }
            }
		}
        else {
            //Titles
            TextView titleRating = (TextView) findViewById(R.id.titleRating);
            titleRating.setText("");
            TextView titleTitle = (TextView) findViewById(R.id.titleTitle);
            titleTitle.setText("");
            TextView titleSummary = (TextView) findViewById(R.id.titleSummary);
            titleSummary.setText("");

            ArrayList<TextView> movieRenders = new ArrayList<TextView>();

            TextView movieTitle1 = (TextView) findViewById(R.id.title1);
            TextView movieRating1 = (TextView) findViewById(R.id.rating1);
            TextView movieSummary1 = (TextView) findViewById(R.id.summary1);
            TextView movieTitle2 = (TextView) findViewById(R.id.title2);
            TextView movieRating2 = (TextView) findViewById(R.id.rating2);
            TextView movieSummary2 = (TextView) findViewById(R.id.summary2);
            TextView movieTitle3 = (TextView) findViewById(R.id.title3);
            TextView movieRating3 = (TextView) findViewById(R.id.rating3);
            TextView movieSummary3 = (TextView) findViewById(R.id.summary3);
            TextView movieTitle4 = (TextView) findViewById(R.id.title4);
            TextView movieRating4 = (TextView) findViewById(R.id.rating4);
            TextView movieSummary4 = (TextView) findViewById(R.id.summary4);
            TextView movieTitle5 = (TextView) findViewById(R.id.title5);
            TextView movieRating5 = (TextView) findViewById(R.id.rating5);
            TextView movieSummary5 = (TextView) findViewById(R.id.summary5);
            TextView movieTitle6 = (TextView) findViewById(R.id.title6);
            TextView movieRating6 = (TextView) findViewById(R.id.rating6);
            TextView movieSummary6 = (TextView) findViewById(R.id.summary6);

            movieRenders.add(movieTitle1);
            movieRenders.add(movieRating1);
            movieRenders.add(movieSummary1);
            movieRenders.add(movieTitle2);
            movieRenders.add(movieRating2);
            movieRenders.add(movieSummary2);
            movieRenders.add(movieTitle3);
            movieRenders.add(movieRating3);
            movieRenders.add(movieSummary3);
            movieRenders.add(movieTitle4);
            movieRenders.add(movieRating4);
            movieRenders.add(movieSummary4);
            movieRenders.add(movieTitle5);
            movieRenders.add(movieRating5);
            movieRenders.add(movieSummary5);
            movieRenders.add(movieTitle6);
            movieRenders.add(movieRating6);
            movieRenders.add(movieSummary6);

            for (int i = 0; i < 6; i++) {
                //set the movie title
                movieRenders.get(i * 3).setText("");
                //set the movie rating
                movieRenders.get((i * 3) + 1).setText("");
                //set the movie summary
                movieRenders.get((i * 3) + 2).setText("");
            }
        }
	}

	/*Called when update weather*/
	public void updateWeather(View view) {
        TextView variousLo = (TextView) findViewById(R.id.LoDay0);
        CharSequence LoCurrent = variousLo.getText();
        TextView userHelp = (TextView) findViewById(R.id.userHelp);
        if(LoCurrent.equals("")) {
            userHelp.setText("Click once more to see updates");
        }
        else if(LoCurrent.equals("Updating")) {
            userHelp.setText("");
        }
        else {
            userHelp.setText("");
        }
		Runnable getForecast = new Runnable() {
			public void run() {
				Looper.prepare();
				HttpPost post;
				Switch gpsSwitch = (Switch) findViewById(R.id.GPS);
				if (gpsSwitch != null) {
					if (gpsSwitch.isChecked()) {
						//use latitude and longitude
						post = new HttpPost("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + mCurrentLocation.getLatitude() +
								"&lon=" + mCurrentLocation.getLongitude() + "&cnt=5&mode=json&units=imperial");
					} else {
						EditText editLocality = (EditText) findViewById(R.id.locality);
						String city = editLocality.getText().toString();
						if (city != null && !city.equals("")) {
							post = new HttpPost("http://api.openweathermap.org/data/2.5/forecast/daily?&cnt=5&mode=json&units=imperial&q=" + city);
						} else {
							//Todo: No city, so notify user
							Log.v("test", "No city given");
							return;
						}
					}
					Log.w("test", "Before HttpClient");
					Log.w("test", "httpclient is successfully made");
					StringEntity se = null;
					processPost(post);
				}

			}
		};

		new Thread(getForecast).start();

		//Now update lights
		Runnable updateLights = new Runnable() {
			public void run() {
				EditText editIp = (EditText) findViewById(R.id.editText);
				String ip_address = editIp.getText().toString();
				if (ip_address.equals("") || ip_address.equals("ip Address")) {
					Log.w("test", "an ip address was not inputted");
					sendWeatherLights("172.27.98.94",sunny);
				} else {

					sendWeatherLights(ip_address,sunny);
				}


			}
		};
		new Thread(updateLights).start();


				Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		String day0 = "";
		String day1 = "";
		String day2 = "";
		String day3 = "";
		String day4 = "";
		switch (day) {
			case Calendar.SUNDAY: {
				day0 = "Sunday";
				day1 = "Monday";
				day2 = "Tuesday";
				day3 = "Wednesday";
				day4 = "Thursday";
				break;
			}
			case Calendar.MONDAY: {
				day0 = "Monday";
				day1 = "Tuesday";
				day2 = "Wednesday";
				day3 = "Thursday";
				day4 = "Friday";
				break;
			}
			case Calendar.TUESDAY: {
				day0 = "Tuesday";
				day1 = "Wednesday";
				day2 = "Thursday";
				day3 = "Friday";
				day4 = "Saturday";
				break;
			}
			case Calendar.WEDNESDAY: {
				day0 = "Wednesday";
				day1 = "Thursday";
				day2 = "Friday";
				day3 = "Saturday";
				day4 = "Sunday";
				break;
			}
			case Calendar.THURSDAY: {
				day0 = "Thursday";
				day1 = "Friday";
				day2 = "Saturday";
				day3 = "Sunday";
				day4 = "Monday";
				break;
			}
			case Calendar.FRIDAY: {
				day0 = "Friday";
				day1 = "Saturday";
				day2 = "Sunday";
				day3 = "Monday";
				day4 = "Tuesday";
				break;
			}
			case Calendar.SATURDAY: {
				day0 = "Saturday";
				day1 = "Sunday";
				day2 = "Monday";
				day3 = "Tuesday";
				day4 = "Wednesday";
				break;
			}
		}

			TextView day0Temp = (TextView) findViewById(R.id.day0);
			TextView day1Temp = (TextView) findViewById(R.id.day1);
			TextView day2Temp = (TextView) findViewById(R.id.day2);
			TextView day3Temp = (TextView) findViewById(R.id.day3);
			TextView day4Temp = (TextView) findViewById(R.id.day4);
			day0Temp.setText(day0);
			day1Temp.setText(day1);
			day2Temp.setText(day2);
			day3Temp.setText(day3);
			day4Temp.setText(day4);
			TextView day0Low = (TextView) findViewById(R.id.LoDay0);
			TextView day1Low = (TextView) findViewById(R.id.LoDay1);
			TextView day2Low = (TextView) findViewById(R.id.LoDay2);
			TextView day3Low = (TextView) findViewById(R.id.LoDay3);
			TextView day4Low = (TextView) findViewById(R.id.LoDay4);
			if (!lowTemps.isEmpty() && lowTemps.size() >= 5) {
				day0Low.setText(Math.round(lowTemps.get(0)) + "°");
				day1Low.setText(Math.round(lowTemps.get(1)) + "°");
				day2Low.setText(Math.round(lowTemps.get(2)) + "°");
				day3Low.setText(Math.round(lowTemps.get(3)) + "°");
				day4Low.setText(Math.round(lowTemps.get(4)) + "°");
			} else {
				day0Low.setText("Updating");
				day1Low.setText("Updating");
				day2Low.setText("Updating");
				day3Low.setText("Updating");
				day4Low.setText("Updating");
				Log.v("test", "Low" + lowTemps.toString());

			}
			TextView day0Hi = (TextView) findViewById(R.id.HiDay0);
			TextView day1Hi = (TextView) findViewById(R.id.HiDay1);
			TextView day2Hi = (TextView) findViewById(R.id.HiDay2);
			TextView day3Hi = (TextView) findViewById(R.id.HiDay3);
			TextView day4Hi = (TextView) findViewById(R.id.HiDay4);
			if (!hiTemps.isEmpty() && hiTemps.size() >= 5) {
				day0Hi.setText(Math.round(hiTemps.get(0)) + "°");
				day1Hi.setText(Math.round(hiTemps.get(1)) + "°");
				day2Hi.setText(Math.round(hiTemps.get(2)) + "°");
				day3Hi.setText(Math.round(hiTemps.get(3)) + "°");
				day4Hi.setText(Math.round(hiTemps.get(4)) + "°");
			} else {
				day0Hi.setText("Updating");
				day1Hi.setText("Updating");
				day2Hi.setText("Updating");
				day3Hi.setText("Updating");
				day4Hi.setText("Updating");
				Log.v("test", "High" + hiTemps.toString());
			}

			Runnable getMovies = new Runnable() {
				public void run(){
					String apikey = "a8hp9y82qahh6hsbq72xtpn3";
					HttpGet post = new HttpGet("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey="+apikey+"&page_limit=6");
					DefaultHttpClient client = new DefaultHttpClient();
					StringEntity se = null;
					try {
						HttpResponse resp = client.execute(post);
						HttpEntity entity = resp.getEntity();
						String body = parseEntity(entity);
						Log.v("test", body);
						try {
							JSONObject json = new JSONObject(body);
							Log.v("test", json.toString());
							JSONArray main = json.getJSONArray("movies");
							getMoviesFromJsonArray(main);
						}
						catch (JSONException j){
							j.printStackTrace();
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}

				}
			};

			if(sunny) {
				//Go outside
				//handle clearing movies
				fillMovie(view,false);
			}
			else {
				// Movies
				//Handle resizing go outside and clearing it


				new Thread(getMovies).start();
				fillMovie(view,true);
			}


		}

	/*Called when green lights button is pressed*/

	public void greenLightsOn(View view) {
		Runnable runnable = new Runnable() {
			public void run() {

				EditText editIp = (EditText) findViewById(R.id.editText);
				String ip_address = editIp.getText().toString();
				HttpPost post;
				if (ip_address.equals("")) {
					Log.w("test", "an ip address was not inputted");
					post = new HttpPost("http://172.27.98.94/rpi");
				} else {
					post = new HttpPost("http://" + ip_address + "/rpi");
				}

				Log.w("test", "Before HttpClient");
				DefaultHttpClient client = new DefaultHttpClient();
				Log.w("test", "httpclient is successfully made");
//


				Log.w("test", "post is successfully made");

				ArrayList lights = new ArrayList();
				JSONObject header = new JSONObject();
				try {
					header.put("lightId", 1);
					header.put("red", 0);
					header.put("green", 255);
					header.put("blue", 0);
					header.put("intensity", 0.75);
					Log.w("test", "individual light is made");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				lights.add(header);


				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("lights", lights);
					jsonobj.put("propagate", "true");
					Log.w("test", "JSON data is constructed");
//                    jsonobj.put("lights", "" );
//                    jsonobj.put("propagate", "true" );
				} catch (JSONException e) {
					e.printStackTrace();
				}

				StringEntity se = null;
				try {
					//Todo: fix json to string
//                    se = new StringEntity(jsonobj.toString());
					se = new StringEntity("{\"lights\":[{\"intensity\":0.75,\"red\":0,\"blue\":0,\"green\":255,\"lightId\":1}],\"propagate\":true}");
					se.setContentType("application/json;charset=UTF-8");
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
					Log.w("test", "JSON data is strigified");
					Log.v("test", jsonobj.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				post.setEntity(se);

				try {
					HttpResponse resp = client.execute(post);
//                    client.execute(post);

					Log.w("test", "POST data is sent to raspberry pi");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(runnable).start();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);


	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			Handler mHandler = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}


}