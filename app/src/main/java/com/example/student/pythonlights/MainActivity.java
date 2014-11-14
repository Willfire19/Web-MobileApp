package com.example.student.pythonlights;

//Joshua Angeley, Alex Aberman, Patrick McGee

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.location.*;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
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
        setContentView(R.layout.activity_main);
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

        }
        catch(Exception e) {

        }
        //editLocality.setText(Location.convert(mCurrentLocation.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(mCurrentLocation.getLongitude(), Location.FORMAT_DEGREES));
    }

    public void getLocation(View view){
        if (isConnected) {
            try {
                mCurrentLocation = mLocationClient.getLastLocation();
                Log.w("test", "mCurrentLocation is: " + mCurrentLocation);
            }
            catch(Exception e) {
                Log.w("test", e);
            }
        }
    }

    /*Called when green lights button is pressed*/
    public void greenLightsOn(View view){
        Runnable runnable = new Runnable() {
            public void run() {

                EditText editIp = (EditText) findViewById(R.id.editText);
                String ip_address = editIp.getText().toString();

                Log.w("test", "Before HttpClient");
                DefaultHttpClient client = new DefaultHttpClient();
                Log.w("test", "httpclient is successfully made");
//                HttpPost post = new HttpPost("http://172.27.98.94/rpi");
                HttpPost post = new HttpPost("http://" + ip_address + "/rpi");

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
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
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
}
