package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import il.ac.huji.x_change.R;
import il.ac.huji.x_change.Service.MessageService;


public class RegisterActivity extends ActionBarActivity {

    private ProgressBar spinner;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[A-Za-z]).{6,20})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = (ProgressBar) findViewById(R.id.register_spinner);
        spinner.setVisibility(View.GONE);
        spinner.setIndeterminate(true);

        Button regButton = (Button) findViewById(R.id.btnRegister);
        regButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                spinner.setVisibility(View.VISIBLE);
                Resources res = getResources();

                TextInputLayout nameTIL = (TextInputLayout) findViewById(R.id.reg_fullname_wrapper);
                String fullName = nameTIL.getEditText().getText().toString();

                if (fullName.isEmpty()) {
                    String emptyName = res.getString(R.string.name_is_empty);
                    Toast toast = Toast.makeText(getApplicationContext(), emptyName,
                            Toast.LENGTH_SHORT);
                    toast.show();
                    spinner.setVisibility(View.GONE);
                    return;
                }

                TextInputLayout emailTIL = (TextInputLayout) findViewById(R.id.reg_email_wrapper);
                String email = emailTIL.getEditText().getText().toString();

                if (!isEmailValid(email)) {
                    String emailNotValid = res.getString(R.string.email_not_valid);
                    Toast toast = Toast.makeText(getApplicationContext(), emailNotValid,
                            Toast.LENGTH_SHORT);
                    toast.show();
                    spinner.setVisibility(View.GONE);
                    return;
                }

                TextInputLayout passwordTIL = (TextInputLayout) findViewById(R.id.reg_password_wrapper);
                String password = passwordTIL.getEditText().getText().toString();

                if (!isPasswordValid(password)) {
                    String passNotValid = res.getString(R.string.password_not_valid);
                    Toast toast = Toast.makeText(getApplicationContext(), passNotValid,
                            Toast.LENGTH_SHORT);
                    toast.show();
                    spinner.setVisibility(View.GONE);
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    currentUser.logOut();
                }

                ParseUser user = new ParseUser();
                user.setUsername(email);
                user.setPassword(password);
                user.put("Name", fullName);
                user.put("rating", 0);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        spinner.setVisibility(View.GONE);
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Toast toast = Toast.makeText(getApplicationContext(), "sign up success",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                            startActivity(intent);
                            startService(serviceIntent);
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
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



        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Closing registration screen & switching to Login Screen
                finish();
            }
        });
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
