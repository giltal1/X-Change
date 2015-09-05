package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import il.ac.huji.x_change.Adapter.PlaceArrayAdapter;
import il.ac.huji.x_change.Adapter.SpinnerAdapter;
import il.ac.huji.x_change.Model.CurrencyDataSource;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class NewListingActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG = "NewListingActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private static final int CURRENT_LOCATION = 0;
    private static final int CUSTOM_LOCATION = 1;
    private static final int NO_LOCATION = 2;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private static final int PICK_FROM_CURRENCY_REQ = 1;
    private static final int PICK_TO_CURRENCY_REQ = 2;
    private final BigDecimal ERROR = new BigDecimal("-1");

    private BigDecimal rate = null;
    private Location location = new Location("");
    private CurrencyDataSource db;

    private AutoCompleteTextView actv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //Initiate rate
        rate = new BigDecimal("1");
        getRateFor("USD", "ILS", false);

        //Open DB
        db = new CurrencyDataSource(this);
        db.open();

        //build google api client
        mGoogleApiClient = new GoogleApiClient.Builder(NewListingActivity.this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        EditText fromAmount = (EditText) findViewById(R.id.currency_amount_from);
        fromAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText toAmount = (EditText) findViewById(R.id.currency_amount_to);
                if (rate.compareTo(ERROR) != 0 && !s.toString().equals("")) {
                    toAmount.setText(rate.multiply(new BigDecimal(s.toString())).setScale(0, RoundingMode.HALF_UP).toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText toAmount = (EditText) findViewById(R.id.currency_amount_to);
        toAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText fromAmount = (EditText) findViewById(R.id.currency_amount_from);
                String from = fromAmount.getText().toString();
                TextView yourRate = (TextView) findViewById(R.id.your_rate);
                Log.d("from.equals(\"\")", Boolean.toString(from.equals("")));
                Log.d("s.equals(\"\")", Boolean.toString(s.toString().trim().equals("")));
                Log.d("s is", s.toString().trim());
                if (!from.equals("") && !s.toString().equals("")) {
                    Log.d("From", "From not empty!");
                    yourRate.setText(new BigDecimal(s.toString()).divide(new BigDecimal(from), 4, RoundingMode.HALF_UP).toString());
                } else {
                    Log.d("From", "From is empty!");
                    yourRate.setText("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayout fromCurrency = (LinearLayout)findViewById(R.id.from_pick);
        LinearLayout toCurrency = (LinearLayout)findViewById(R.id.to_pick);

        fromCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrencyActivity.class);
                startActivityForResult(intent, PICK_FROM_CURRENCY_REQ);
            }
        });

        toCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrencyActivity.class);
                startActivityForResult(intent, PICK_TO_CURRENCY_REQ);
            }
        });


        ImageView swapImage = (ImageView) findViewById(R.id.swap);
        swapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fromCode = (TextView) findViewById(R.id.currency_code_from);
                ImageView fromFlag = (ImageView) findViewById(R.id.currency_flag_from);
                TextView toCode = (TextView) findViewById(R.id.currency_code_to);
                ImageView toFlag = (ImageView) findViewById(R.id.currency_flag_to);

                String tempCode = toCode.getText().toString();
                toCode.setText(fromCode.getText().toString());
                fromCode.setText(tempCode);

                Drawable tempFlag = toFlag.getDrawable();
                toFlag.setImageDrawable(fromFlag.getDrawable());
                fromFlag.setImageDrawable(tempFlag);

                fromCode = (TextView) findViewById(R.id.currency_code_from);
                String from = fromCode.getText().toString();
                toCode = (TextView) findViewById(R.id.currency_code_to);
                String to = toCode.getText().toString();
                getRateFor(from, to, true);

            }
        });

        String[] items = getResources().getStringArray(R.array.location_spinner_labels);
        List<String> data = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            data.add(items[i]);
        }

        actv = (AutoCompleteTextView) findViewById(R.id.actv_location);
        actv.setVisibility(View.GONE);
        actv.setThreshold(3);
        actv.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        actv.setAdapter(mPlaceArrayAdapter);

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_location);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == CUSTOM_LOCATION) {
                    actv.setVisibility(View.VISIBLE);
                } else {
                    actv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button okButton = (Button) findViewById(R.id.new_conversion_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get from
                TextInputLayout fromAmountTIL = (TextInputLayout) findViewById(R.id.currency_amount_from_wrapper);
                String fromAmount = fromAmountTIL.getEditText().getText().toString();
                TextView fromCodeTV = (TextView) findViewById(R.id.currency_code_from);
                String fromCode = fromCodeTV.getText().toString();
                CurrencyItem fromCurrency = db.getCurrencyByCode(fromCode);

                //Get to
                TextInputLayout toAmountTIL = (TextInputLayout) findViewById(R.id.currency_amount_to_wrapper);
                String toAmount = toAmountTIL.getEditText().getText().toString();
                TextView toCodeTV = (TextView) findViewById(R.id.currency_code_to);
                String toCode = toCodeTV.getText().toString();
                CurrencyItem toCurrency = db.getCurrencyByCode(toCode);

                Spinner spinner = (Spinner) findViewById(R.id.spinner_location);

                boolean valid = true;
                if (fromAmount.isEmpty()) {
                    fromAmountTIL.setError(getResources().getString(R.string.empty_amount));
                    valid = false;
                }
                if (toAmount.isEmpty()) {
                    toAmountTIL.setError(getResources().getString(R.string.empty_amount));
                    valid = false;
                }

                if (spinner.getSelectedItemPosition() == NO_LOCATION) {
                    Snackbar.make(findViewById(android.R.id.content),
                            getResources().getString(R.string.empty_location),
                            Snackbar.LENGTH_LONG).show();
                    valid = false;
                }

                if (!valid) {
                    return;
                }

                ParseObject parseObj = new ParseObject("Suggestions");
                parseObj.put("createdBy", ParseUser.getCurrentUser());
                parseObj.put("fromAmount", new BigDecimal(fromAmount));
                parseObj.put("fromCurrency", fromCurrency.getCode());
                parseObj.put("toAmount", new BigDecimal(toAmount));
                parseObj.put("toCurrency", toCurrency.getCode());
                ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                parseObj.put("location", point);
                parseObj.saveInBackground();
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.new_conversion_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            //Get extras
            String code = data.getStringExtra("currencyCode");
            Bitmap flag = data.getParcelableExtra("currencyImage");
            TextView currencyCode = null;
            ImageView currencyFlag = null;
            // Check which request we're responding to
            if (requestCode == PICK_FROM_CURRENCY_REQ) {
                currencyCode = (TextView) findViewById(R.id.currency_code_from);
                currencyFlag = (ImageView) findViewById(R.id.currency_flag_from);
            }
            if (requestCode == PICK_TO_CURRENCY_REQ) {
                currencyCode = (TextView) findViewById(R.id.currency_code_to);
                currencyFlag = (ImageView) findViewById(R.id.currency_flag_to);
            }

            currencyCode.setText(code);
            currencyFlag.setImageBitmap(flag);

            TextView convertFrom = (TextView) findViewById(R.id.currency_code_from);
            String from = convertFrom.getText().toString();
            TextView convertTo = (TextView) findViewById(R.id.currency_code_to);
            String to = convertTo.getText().toString();
            getRateFor(from, to, false);
        }
    }

    private void getRateFor(final String from, final String to, final boolean swap) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + from + to + "%22)&format=json&env=store://datatables.org/alltableswithkeys";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        //String url = "http://apilayer.net/api/historical?access_key=" + getResources().getString(R.string.currencylayer_key) + "&currencies=" + from +  "," + to + "&date=" + dateFormat.format(cal.getTime()) + "format=1";
        //String url = "https://openexchangerates.org/api/historical/" + dateFormat.format(cal.getTime()) + ".json?app_id=" + getResources().getString(R.string.openexchangerates_key);
        // Request a response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Display rate
                        Log.d("Response", "Response is: " + response);
                        try {
                            JSONObject query = response.getJSONObject("query");
                            JSONObject results = query.getJSONObject("results");
                            JSONObject rates = results.getJSONObject("rate");
                            String newRate = rates.getString("Rate");
//                            JSONObject quotes = response.getJSONObject("quotes");
//                            String newRate = quotes.getString(from + to);

                            TextView officialRate = (TextView) findViewById(R.id.official_rate);
                            TextView yourRate = (TextView) findViewById(R.id.your_rate);
                            if (!newRate.equals("N/A")) {
                                rate = new BigDecimal(newRate);
                                officialRate.setText(rate.toString());
                                yourRate.setText(rate.toString());
                                if (swap) {
                                    EditText fromAmount = (EditText) findViewById(R.id.currency_amount_from);
                                    String from = fromAmount.getText().toString();
                                    if (!from.isEmpty()) {
                                        EditText toAmount = (EditText) findViewById(R.id.currency_amount_to);
                                        toAmount.setText(rate.multiply(new BigDecimal(from).setScale(0, RoundingMode.HALF_UP)).toString());
                                    }
                                }
                            }
                            else {
                                rate = ERROR;
                                officialRate.setText("N/A");
                                yourRate.setText("0.00");
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", "Response didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mGetPlaceGeoPointCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mGetPlaceGeoPointCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
    };
    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
        getLocation();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        mGoogleApiClient.connect();
        Log.e(LOG_TAG, "Google Places API connection suspended.");
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

}
