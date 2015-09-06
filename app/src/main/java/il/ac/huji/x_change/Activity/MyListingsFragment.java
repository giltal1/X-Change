package il.ac.huji.x_change.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.x_change.Adapter.MyListingAdapter;
import il.ac.huji.x_change.R;

public class MyListingsFragment extends Fragment {


    private List<Pair<String, String>> data;
    private ArrayAdapter<Pair<String, String>> adapter;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_listings, container, false);

        listView = (ListView) rootView.findViewById(R.id.my_list_view);

        data = new ArrayList<>();
        adapter = new MyListingAdapter(getActivity(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMyListings();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchMyListings();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Context context = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.delete));
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int pos) {
                                String objectId = adapter.getItem(position).first;
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Suggestions");
                                query.whereEqualTo("objectId", objectId);
                                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null)
                                            object.deleteInBackground();

                                        else
                                            Log.e(getClass().getSimpleName(), "can't find object to delete");
                                    }
                                });
                                adapter.remove(adapter.getItem(position));
                                adapter.notifyDataSetChanged();
                            }
                        }

                );
                builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );
                builder.show();
            }
        });

        return rootView;
    }

    private void fetchMyListings() {
        data.clear();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.yellow, R.color.green, R.color.blue);
        swipeRefreshLayout.setRefreshing(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suggestions");
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> listings, ParseException e) {
                if (e == null) {
                    Log.d("suggestions", "Retrieved " + listings.size() + " scores");
                    for (ParseObject obj : listings) {

                        String fromAmount = obj.get("fromAmount").toString();
                        String fromCurrency = obj.get("fromCurrency").toString();
                        String toAmount = obj.get("toAmount").toString();
                        String toCurrency = obj.get("toCurrency").toString();

                        String id = obj.getObjectId();
                        String txt = getResources().getString(R.string.from) + " " + fromAmount + " " + fromCurrency +
                                " " + getResources().getString(R.string.to) + " " + toAmount + " " + toCurrency;
                        data.add(new Pair<>(id, txt));
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Log.e("listings", "Error: " + e.getMessage());
                }
            }
        });
    }

}
