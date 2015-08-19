package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

import il.ac.huji.x_change.R;
import il.ac.huji.x_change.Service.MessageService;


public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //initialize Parse
        Resources resources = this.getResources();
        String appID = resources.getString(R.string.parse_app_id);
        String clientID = resources.getString(R.string.parse_client_id);
        Parse.initialize(this, appID, clientID);

        new PrefetchData().execute();

    }

    private class PrefetchData extends AsyncTask<Void, Void, Class> {

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // before making http calls
//
//        }

        @Override
        protected Class doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */
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
            // After completing http call
            // will close this activity and launch main activity
            Intent i = new Intent(SplashScreen.this, result);
            startActivity(i);
            // close this activity
            finish();
        }

    }

}
