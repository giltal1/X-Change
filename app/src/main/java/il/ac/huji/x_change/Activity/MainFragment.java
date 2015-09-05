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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import il.ac.huji.x_change.Adapter.ListingItemAdapter;
import il.ac.huji.x_change.Dialog.SortDialogFragment;
import il.ac.huji.x_change.Model.Constants;
import il.ac.huji.x_change.Model.ListingItem;
import il.ac.huji.x_change.Model.CurrencyDataSource;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static int SORT_PARAM = Constants.SORT_DEFAULT;
    private static int FILTER_PARAM = Constants.FILTER_NONE;
    private static int FILTER_VALUE = 0;

    private List<ListingItem> data;
    private ListingItemAdapter adapter;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CurrencyDataSource db;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private int distanceRandomFactor;
    private Location location = new Location("");

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        data = new ArrayList<ListingItem>();
        adapter = new ListingItemAdapter(data, getActivity());
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

        Random random = new Random();
        distanceRandomFactor = random.nextInt(Constants.DISTANCE_FACTOR) + 1;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchListings(SORT_PARAM, FILTER_PARAM, FILTER_VALUE);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchListings(SORT_PARAM, FILTER_PARAM, FILTER_VALUE);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewListingActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void fetchListings(final int order, final int filter, final int filterValue) {
        data.clear();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.yellow, R.color.green, R.color.blue);
        swipeRefreshLayout.setRefreshing(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suggestions");
        query.whereNotEqualTo("createdBy", ParseUser.getCurrentUser());
//        ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(),location.getLongitude());
//        query.whereNear("location", userLocation);
        if (order == Constants.SORT_RATING) {
            query.addDescendingOrder("rating");
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listings, ParseException e) {
                if (e == null) {
                    Log.d("suggestions", "Retrieved " + listings.size() + " scores");
                    for (ParseObject obj : listings) {
                        ParseUser user = null;
                        try {
                            user = obj.getParseUser("createdBy").fetchIfNeeded();
                        }
                        catch (ParseException ex) {
                            Log.e(getClass().getSimpleName(), "Error: " + ex.getMessage());
                        }
                        String fromAmount = obj.get("fromAmount").toString();
                        CurrencyItem fromCurrency = db.getCurrencyByCode(obj.get("fromCurrency").toString());
                        String toAmount = obj.get("toAmount").toString();
                        CurrencyItem toCurrency = db.getCurrencyByCode(obj.get("toCurrency").toString());
                        Location locationOffer = new Location("");
                        locationOffer.setLatitude(obj.getParseGeoPoint("location").getLatitude());
                        locationOffer.setLongitude(obj.getParseGeoPoint("location").getLongitude());
                        float distance = location.distanceTo(locationOffer) + distanceRandomFactor;

                        ListingItem item = new ListingItem(user, fromAmount, fromCurrency, toAmount, toCurrency, distance);
                        data.add(item);
                    }

                    switch (order) {
                        case Constants.SORT_DEFAULT:
                            Collections.sort(data, ListingItem.Default);
                            break;
                        case Constants.SORT_DISTANCE:
                            Collections.sort(data, ListingItem.Distance);
                            break;
                    }


//                    switch (filter) {
//                        case Constants.FILTER_NONE:
//                            break;
//                        case Constants.FILTER_BY_DISTANCE:
//                            for (Iterator<ListingItem> iter = data.listIterator(); iter.hasNext();) {
//                                ListingItem item = iter.next();
//                                if (item.getDistance() > FILTER_VALUE)
//                                    iter.remove();
//                            }
//                            break;
//                        case Constants.FILTER_BY_RATING:
//                            for (Iterator<ListingItem> iter = data.listIterator(); iter.hasNext();) {
//                                ListingItem item = iter.next();
//                                if (item.getUserRating() < FILTER_VALUE)
//                                    iter.remove();
//                            }
//                            break;
//                        default:
//                            return;
//                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.e("listings", "Error: " + e.getMessage());
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
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortDialogFragment sortDialog = new SortDialogFragment();
                sortDialog.setTargetFragment(MainFragment.this, Constants.REQ_SORT_CODE);
                sortDialog.show(getFragmentManager(), "");
                sortDialog.setCancelable(false);
                return true;
//            case R.id.action_filter:
//                FilterDialogFragment filterDialog = new FilterDialogFragment();
//                filterDialog.setTargetFragment(MainFragment.this, Constants.REQ_FILTER_CODE);
//                filterDialog.show(getFragmentManager(), "");
//                filterDialog.setCancelable(false);
//                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_SORT_CODE) {
            switch (resultCode) {
                case Constants.SORT_DEFAULT:
                    SORT_PARAM = resultCode;
                    break;
                case Constants.SORT_DISTANCE:
                    SORT_PARAM = resultCode;
                    break;
                case Constants.SORT_RATING:
                    SORT_PARAM = resultCode;
                    break;
                default:
                    return;
            }
            fetchListings(SORT_PARAM, FILTER_PARAM, FILTER_VALUE);
            return;
        }
//        if (requestCode == Constants.REQ_FILTER_CODE) {
//            TextDialog textDialog;
//            switch (resultCode) {
//                case Constants.FILTER_NONE:
//                    FILTER_PARAM = requestCode;
//                    fetchListings(SORT_PARAM, FILTER_PARAM, FILTER_VALUE);
//                    return;
//                case Constants.SORT_DISTANCE:
//                    FILTER_PARAM = requestCode;
//                    textDialog = new TextDialog();
//                    textDialog.setTargetFragment(MainFragment.this, Constants.REQ_FILTER_DISTANCE_CODE);
//                    textDialog.show(getFragmentManager(), "");
//                    textDialog.setCancelable(false);
//                    break;
//                case Constants.FILTER_BY_RATING:
//                    FILTER_PARAM = requestCode;
//                    textDialog = new TextDialog();
//                    textDialog.setTargetFragment(MainFragment.this, Constants.REQ_FILTER_RATING_CODE);
//                    textDialog.show(getFragmentManager(), "");
//                    textDialog.setCancelable(false);
//                    break;
//                default:
//                    return;
//            }
//        }
//        if (requestCode == Constants.REQ_FILTER_DISTANCE_CODE ||
//                requestCode == Constants.REQ_FILTER_RATING_CODE) {
//            FILTER_VALUE = resultCode;
//            fetchListings(SORT_PARAM, FILTER_PARAM, FILTER_VALUE);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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
        Log.i(getClass().getSimpleName(), "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
