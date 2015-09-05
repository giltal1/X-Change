package il.ac.huji.x_change.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseUser;

import il.ac.huji.x_change.R;
import il.ac.huji.x_change.Service.MessageService;


public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (checkConnection() && checkLocation()) {
            new checkUserLogin().execute();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.connect_dialog_title);
            builder.setMessage(R.string.connect_dialog_msg);
            builder.setPositiveButton(R.string.connect_dialog_btn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
            return false;
        }
        return true;
    }

    private boolean checkLocation() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isLocationOn = gpsEnabled || networkEnabled;
        if (!isLocationOn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.location_dialog_title);
            builder.setMessage(R.string.location_dialog_msg);
            builder.setPositiveButton(R.string.location_dialog_btn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
            return false;
        }
        return true;
    }

    private class checkUserLogin extends AsyncTask<Void, Void, Class> {

        @Override
        protected Class doInBackground(Void... arg0) {
            Class result;
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // user exist
                result = MainActivity.class;
            } else {
                // show the signup or login screen
                result = LoginActivity.class;
            }
            try {
                Thread.sleep(SPLASH_TIME_OUT);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Class result) {
            super.onPostExecute(result);
            Intent intent = new Intent(getApplicationContext(), result);
            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
            startActivity(intent);
            startService(serviceIntent);
        }

    }

}
