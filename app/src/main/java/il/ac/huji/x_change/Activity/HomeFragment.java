package il.ac.huji.x_change.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.x_change.Adapter.RecyclerAdapter;
import il.ac.huji.x_change.Model.ConversionItem;
import il.ac.huji.x_change.Model.MoneyItem;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true); //TODO: check if needed
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        data = new ArrayList<ConversionItem>();
        data.add(new ConversionItem(new MoneyItem(100, "USD"), new MoneyItem(400, "NIS"), 2500, 50));
        data.add(new ConversionItem(new MoneyItem(10, "EURO"), new MoneyItem(50, "NIS"), 700, 100));
        data.add(new ConversionItem(new MoneyItem(40, "NIS"), new MoneyItem(400, "BHT"), 250, 0));
        data.add(new ConversionItem(new MoneyItem(50, "STR"), new MoneyItem(600, "NIS"), 10000, 300));
        data.add(new ConversionItem(new MoneyItem(30, "EURO"), new MoneyItem(150, "NIS"), 3500, 1));
        adapter = new RecyclerAdapter(data);
        rv.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);

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
