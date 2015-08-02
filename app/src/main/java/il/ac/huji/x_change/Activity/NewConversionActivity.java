package il.ac.huji.x_change.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonRectangle;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import il.ac.huji.x_change.Model.CurrencyDataSource;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class NewConversionActivity extends AppCompatActivity {

    private static final int PICK_FROM_CURRENCY_REQ = 1;
    private static final int PICK_TO_CURRENCY_REQ = 2;
    private final BigDecimal ERROR = new BigDecimal("-1");

    private BigDecimal rate = null;
    private CurrencyDataSource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversion);

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

        EditText fromAmount = (EditText) findViewById(R.id.currency_amount_from);
        fromAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText toAmount = (EditText) findViewById(R.id.currency_amount_to);
                if (rate.compareTo(ERROR) != 0 && !s.toString().equals("")) {
                    toAmount.setText(rate.multiply(new BigDecimal(s.toString())).toString());
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
                    yourRate.setText(new BigDecimal(s.toString()).divide(new BigDecimal(from), 4, BigDecimal.ROUND_HALF_UP).toString());
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

        ButtonRectangle okButton = (ButtonRectangle) findViewById(R.id.new_conversion_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get from
                EditText fromAmountET = (EditText) findViewById(R.id.currency_amount_from);
                String fromAmount = fromAmountET.getText().toString();
                TextView fromCodeTV = (TextView) findViewById(R.id.currency_code_from);
                String fromCode = fromCodeTV.getText().toString();
                CurrencyItem fromCurrency = db.getCurrencyByCode(fromCode);

                //Get to
                EditText toAmountET = (EditText) findViewById(R.id.currency_amount_to);
                String toAmount = toAmountET.getText().toString();
                TextView toCodeTV = (TextView) findViewById(R.id.currency_code_to);
                String toCode = toCodeTV.getText().toString();
                CurrencyItem toCurrency = db.getCurrencyByCode(toCode);

                if (fromAmount.isEmpty() || toAmount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseObject parseObj = new ParseObject("Suggestions");
                parseObj.put("user", ParseUser.getCurrentUser().getObjectId());
                parseObj.put("fromAmount", new BigDecimal(fromAmount));
                parseObj.put("fromCurrency", fromCurrency.getCode());
                parseObj.put("toAmount", new BigDecimal(toAmount));
                parseObj.put("toCurrency", toCurrency.getCode());
                parseObj.saveInBackground();
                finish();
            }
        });

        ButtonRectangle cancelButton = (ButtonRectangle) findViewById(R.id.new_conversion_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    private void getRateFor(String from, String to, final boolean swap) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + from + to + "%22)&format=json&env=store://datatables.org/alltableswithkeys";

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
                                        toAmount.setText(rate.multiply(new BigDecimal(from)).toString());
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

    @Override
    public void onResume() {
        super.onResume();
        db = new CurrencyDataSource(this);
        db.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

}
