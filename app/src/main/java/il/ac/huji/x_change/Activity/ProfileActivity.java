package il.ac.huji.x_change.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import il.ac.huji.x_change.Model.Constants;
import il.ac.huji.x_change.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        TextView name = (TextView) findViewById(R.id.profile_user_name);
        TextView email = (TextView) findViewById(R.id.profile_email);
        TextView rating = (TextView) findViewById(R.id.profile_rating);

        if (currentUser != null) {
            name.append(" " + currentUser.get("Name").toString());
            email.append(" " + currentUser.getUsername());
            rating.append(" " + currentUser.get("rating").toString());
            if (currentUser.get("Image") != null) {
                ParseFile file =  (ParseFile) currentUser.get("Image");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            CircleImageView image = (CircleImageView) findViewById(R.id.profile_image);
                            image.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        } else {
            // show the signup or login screen
        }

        ImageView image = (ImageView) findViewById(R.id.profile_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.choose_action);
                builder.setPositiveButton(R.string.btn_add, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int position) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, SELECT_PHOTO);
                            }
                        }

                );
                builder.setNegativeButton(R.string.btn_remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                currentUser.remove("Image");
                                currentUser.saveInBackground();
                                CircleImageView image = (CircleImageView) findViewById(R.id.profile_image);
                                image.setImageResource(R.drawable.ic_account_circle_black_48dp);
                                dialog.dismiss();
                            }
                        }
                );
                builder.show();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ImageView image = (ImageView) findViewById(R.id.profile_image);
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        image.setImageBitmap(bitmap);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        final ParseFile file = new ParseFile(currentUser.getUsername() + ".jpg", byteArray);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    currentUser.put("Image", file);
                                    currentUser.saveInBackground();
                                }
                                else {
                                    Log.e("image not saved", e.getMessage());
                                }
                            }
                        });

                    }
                    catch (FileNotFoundException e) {
                        Log.e("FileNotFoundException", e.getMessage());
                    }

                }
        }
    }

}
