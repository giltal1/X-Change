package il.ac.huji.x_change.Adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import il.ac.huji.x_change.R;

/**
 * Created by Gil on 06/09/2015.
 */
public class MyListingAdapter extends ArrayAdapter<Pair<String, String>> {

    public MyListingAdapter(Context context, int resource, List<Pair<String,String>> listings) {
        super(context, resource, listings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        Pair<String, String> listing = getItem(position);

        View view = inflater.inflate(R.layout.item_my_listing, null);
        TextView tv = (TextView) view.findViewById(R.id.my_listing_txt);
        tv.setText(listing.second);

        return view;
    }
}
