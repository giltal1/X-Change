package il.ac.huji.x_change;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends ActionBarActivity {

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[A-Za-z]).{6,20})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regButton = (Button) findViewById(R.id.btnRegister);
        regButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Resources res = getResources();

                EditText editText = (EditText) findViewById(R.id.reg_fullname);
                String fullName = editText.getText().toString();

                editText = (EditText) findViewById(R.id.reg_email);
                String email = editText.getText().toString();

                if (!isEmailValid(email)) {
                    //display Toast
                    String emailNotValid = res.getString(R.string.email_not_valid);
                    Toast toast = Toast.makeText(getApplicationContext(), emailNotValid,
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                    toast.show();
                    return;
                }

                editText = (EditText) findViewById(R.id.reg_password);
                String password = editText.getText().toString();

                if (!isPasswordValid(password)) {
                    String passNotValid = res.getString(R.string.password_not_valid);
                    Toast toast = Toast.makeText(getApplicationContext(), passNotValid,
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                    toast.show();
                    return;
                }

                ParseUser user = new ParseUser();
                user.put("Full Name", fullName);
                user.setUsername(email);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            // close this activity
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            //TODO: learn to parse exception
                        }
                    }
                });
            }
        });



        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Closing registration screen & switching to Login Screen
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid(final String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
