package il.ac.huji.x_change.Activity;

import android.content.Intent;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.x_change.Adapter.ConversionItemAdapter;
import il.ac.huji.x_change.Model.ConversionItem;
import il.ac.huji.x_change.Model.CurrencyDataSource;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private List<ConversionItem> data;
    private ConversionItemAdapter adapter;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CurrencyDataSource db;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private Location location = new Location("");

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        data = new ArrayList<ConversionItem>();
        adapter = new ConversionItemAdapter(data);
        rv.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        //Open DB
        db = new CurrencyDataSource(getActivity());
        db.open();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSuggestions();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchSuggestions();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewConversionActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void fetchSuggestions() {
        data.clear();
        swipeRefreshLayout.setRefreshing(true);
        ParseQuery query = ParseQuery.getQuery("Suggestions");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> suggestions, ParseException e) {
                if (e == null) {
                    Log.d("suggestions", "Retrieved " + suggestions.size() + " scores");
                    for (ParseObject obj : suggestions) {
                        String fromAmount = obj.get("fromAmount").toString();
                        CurrencyItem fromCurrency = db.getCurrencyByCode(obj.get("fromCurrency").toString());
                        String toAmount = obj.get("toAmount").toString();
                        CurrencyItem toCurrency = db.getCurrencyByCode(obj.get("toCurrency").toString());
                        Location locationOffer = new Location("");
                        locationOffer.setLatitude(obj.getParseGeoPoint("location").getLatitude());
                        locationOffer.setLongitude(obj.getParseGeoPoint("location").getLongitude());
                        float distance = location.distanceTo(locationOffer);
                        int rating = obj.getInt("rating");

                        ConversionItem item = new ConversionItem(fromAmount, fromCurrency, toAmount, toCurrency, distance, rating);
                        data.add(item);
                    }

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.e("suggestions", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
//            Toast.makeText(getActivity(), "Latitude: " + latitude + ", Longitude: " + longitude,
//                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        db = new CurrencyDataSource(getActivity());
        db.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
