package il.ac.huji.x_change.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.x_change.Adapter.CurrencyItemAdapter;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class CurrencyActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog pDialog;

    private ArrayList<CurrencyItem> currencyList;
    private CurrencyItemAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setTitle("Choose Currency");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        rv = (RecyclerView) findViewById(R.id.currency_rv);
        rv.setHasFixedSize(true); //TODO: check if needed
        layoutManager = new LinearLayoutManager(CurrencyActivity.this);
        rv.setLayoutManager(layoutManager);
        currencyList = new ArrayList<CurrencyItem>();
        adapter = new CurrencyItemAdapter(getApplicationContext(), currencyList);
        rv.setAdapter(adapter);

        new GetCurrencies().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetCurrencies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CurrencyActivity.this, "Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("currencies");
            query.setLimit(300);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> currencies, ParseException e) {
                    if (e == null) {
                        Log.d("query", "Retrieved " + currencies.size() + " results");
                        for (int i = 0; i < currencies.size(); i++) {
                            ParseObject obj = currencies.get(i);
                            CurrencyItem curr = new CurrencyItem(new BigDecimal(0), obj.getString("currency_name"), obj.getString("currency_code"), obj.getString("country_name"), obj.getString("flag_code2"));
                            adapter.add(curr);
                            sortList(currencyList);
                        }
                    } else {
                        Log.d("query", "Error: " + e.getMessage());
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        private void sortList(ArrayList<CurrencyItem> currencyList) {
            String USA = "USA";
            String UK = "UK";
            String germany = "Germany";
            String israel = "Israel";
            for (int i = 0; i < currencyList.size(); i++) {
                CurrencyItem curr = currencyList.get(i);
                replace(USA, i, 0, curr);
                replace(germany, i, 1, curr);
                replace(UK, i, 2, curr);
                replace(israel, i, 3, curr);
            }
        }

        private void replace(String country, int iBefore, int iAfter, CurrencyItem curr) {
            if (curr.getCountryName().equals(country)) {
                currencyList.remove(iBefore);
                currencyList.add(iAfter, curr);
            }
        }

    }

}
