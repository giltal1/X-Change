package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import il.ac.huji.x_change.R;
import il.ac.huji.x_change.Service.MessageService;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MenuItem searchMenuItem, sortMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Load MainFragment as default fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new MainFragment());
        ft.commit();

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Fragment fragment = null;
                Intent intent = null;
                String title = getString(R.string.app_name);

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    case R.id.nav_item_main:
                        //setMenuItems(true);
                        fragment = new MainFragment();
                        title = getString(R.string.title_main);
                        break;
                    case R.id.nav_item_new_conversion:
                        intent = new Intent(getApplicationContext(), NewConversionActivity.class);
                        break;
                    case R.id.nav_item_messages:
                        intent = new Intent(getApplicationContext(), ListUsersActivity.class);
                        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                        startService(serviceIntent);
                        break;
                    case R.id.nav_item_profile:
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        break;
                    case R.id.nav_item_about:
                        //setMenuItems(false);
                        fragment = new AboutFragment();
                        title = getString(R.string.title_about);
                        break;
                    case R.id.nav_item_share:
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, "Check out \"X-change\", android app to exchange currency with no commissions!");
                        intent.setType("text/plain");
                        break;
                    case R.id.nav_item_logout:
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ParseUser.logOut();
                        break;
                    default:
                        return false;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                    // set the toolbar title
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title);
                    }
                }
                if (intent != null) {
                    startActivity(intent);
                }

                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //Set navigation drawer header
        ParseUser currentUser = ParseUser.getCurrentUser();

        TextView name = (TextView) findViewById(R.id.drawer_profile_name);
        name.setText(currentUser.get("Name").toString());

        TextView mail = (TextView) findViewById(R.id.drawer_profile_mail);
        mail.setText(currentUser.getUsername().toString());


        if (currentUser != null) {
            if (currentUser.get("Image") != null) {
                ParseFile file =  (ParseFile) currentUser.get("Image");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Log.d("in GetDataCallback", "load photo");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            CircleImageView image = (CircleImageView) findViewById(R.id.drawer_profile_image);
                            image.setImageBitmap(bitmap);
                        }
                        else {
                            Log.e("GetDataInBackground", e.getMessage());
                        }
                    }
                });
            }
            else {
                Log.d("current user", "image is null");
            }
        }
        else {
            Log.d("current user", "current user is null");
        }

        CircleImageView image = (CircleImageView) findViewById(R.id.drawer_profile_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "profile image");
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        searchMenuItem = menu.findItem(R.id.action_search);
//        sortMenuItem = menu.findItem(R.id.action_sort);
//
//        return true;
//    }
//
//    private void setMenuItems(boolean setter) {
//        searchMenuItem.setVisible(setter);
//        sortMenuItem.setVisible(setter);
//    }

}
