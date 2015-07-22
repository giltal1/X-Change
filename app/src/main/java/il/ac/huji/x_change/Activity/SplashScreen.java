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

//        ParseUser user = new ParseUser();
//        user.setUsername("my name");
//        user.setPassword("my pass");
//        user.setEmail("email@example.com");
//
//        // other fields can be set just like with ParseObject
//        user.put("phone", "650-555-0000");
//
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//                }
//            }
//        });

        new PrefetchData().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
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
