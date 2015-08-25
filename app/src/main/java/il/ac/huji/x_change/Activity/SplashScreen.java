package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        new checkConnection().execute();

    }

    private class checkConnection extends AsyncTask<Void, Void, Class> {

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
            Intent i = new Intent(getApplicationContext(), result);
            startActivity(i);
        }

    }

}
