package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import il.ac.huji.x_change.R;
import il.ac.huji.x_change.Service.MessageService;


public class LoginActivity extends AppCompatActivity {

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinner = (ProgressBar) findViewById(R.id.login_spinner);
        spinner.setVisibility(View.GONE);
        spinner.setIndeterminate(true);

        Button loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                spinner.setVisibility(View.VISIBLE);

                EditText editText = (EditText) findViewById(R.id.login_email);
                final String email = editText.getText().toString();

                editText = (EditText) findViewById(R.id.login_password);
                final String password = editText.getText().toString();

                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        spinner.setVisibility(View.GONE);
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Toast toast = Toast.makeText(getApplicationContext(), "log in success",
                                    Toast.LENGTH_SHORT);
                            //toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                            toast.show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startActivity(intent);
                            startService(serviceIntent);
                        } else {
                            // Sign in failed. Look at the ParseException to see what happened.
                            e.printStackTrace();
                            //TODO: handle different exceptions
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_SHORT);
                            //toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                            toast.show();
                        }
                    }
                });
            }
        });


        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Switching to Register screen
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
