package il.ac.huji.x_change.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

<<<<<<< HEAD
import java.math.BigDecimal;
=======
>>>>>>> origin/master
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.x_change.Adapter.RecyclerAdapter;
import il.ac.huji.x_change.Model.ConversionItem;
import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class HomeFragment extends Fragment {

    private List<ConversionItem> data;
    private RecyclerAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true); //TODO: check if needed

        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        data = new ArrayList<ConversionItem>();
<<<<<<< HEAD
        data.add(new ConversionItem(new CurrencyItem(new BigDecimal(100), "United States Dollar", "USD", "USA", null), new CurrencyItem(new BigDecimal(400), "New Israeli Shekel", "ILS", "Israel", null), 2500, 50));
        //data.add(new ConversionItem(new CurrencyItem(10, "EURO"), new CurrencyItem(50, "NIS"), 700, 100));
        //data.add(new ConversionItem(new CurrencyItem(40, "NIS"), new CurrencyItem(400, "BHT"), 250, 0));
        //data.add(new ConversionItem(new CurrencyItem(50, "STR"), new CurrencyItem(600, "NIS"), 10000, 300));
        //data.add(new ConversionItem(new CurrencyItem(30, "EURO"), new CurrencyItem(150, "NIS"), 3500, 1));
=======
        data.add(new ConversionItem(new MoneyItem(100, "USD"), new MoneyItem(400, "NIS"), 2500, 50));
        data.add(new ConversionItem(new MoneyItem(10, "EURO"), new MoneyItem(50, "NIS"), 700, 100));
        data.add(new ConversionItem(new MoneyItem(40, "NIS"), new MoneyItem(400, "BHT"), 250, 0));
        data.add(new ConversionItem(new MoneyItem(50, "STR"), new MoneyItem(600, "NIS"), 10000, 300));
        data.add(new ConversionItem(new MoneyItem(30, "EURO"), new MoneyItem(150, "NIS"), 3500, 1));
>>>>>>> origin/master
        adapter = new RecyclerAdapter(data);
        rv.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewConversionActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
