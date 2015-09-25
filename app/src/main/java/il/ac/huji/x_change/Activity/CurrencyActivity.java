package il.ac.huji.x_change.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.View;

import java.util.ArrayList;

import il.ac.huji.x_change.Adapter.CurrencyItemAdapter;
import il.ac.huji.x_change.DB.CurrencyDataSource;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class CurrencyActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ArrayList<CurrencyItem> currencyList;
    private CurrencyItemAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    private CurrencyDataSource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //Open DB
        db = new CurrencyDataSource(this);
        db.open();

        rv = (RecyclerView) findViewById(R.id.currency_rv);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(CurrencyActivity.this);
        rv.setLayoutManager(layoutManager);
        currencyList = db.getAllCurrencies();
        adapter = new CurrencyItemAdapter(this, getApplicationContext(), currencyList);
        rv.setAdapter(adapter);
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
