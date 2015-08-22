package il.ac.huji.x_change.Activity;

import android.app.Application;

import com.parse.Parse;

import il.ac.huji.x_change.R;

/**
 * Created by Gil on 22/08/2015.
 */
public class ParseInitialize extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize Parse
        String appID = getResources().getString(R.string.parse_app_id);
        String clientID = getResources().getString(R.string.parse_client_id);
        Parse.initialize(this, appID, clientID);
    }
}
