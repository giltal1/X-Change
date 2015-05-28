package il.ac.huji.x_change.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import il.ac.huji.x_change.Model.ConversionItem;
import il.ac.huji.x_change.Model.NavDrawerItem;
import il.ac.huji.x_change.R;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private List<ConversionItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public RecyclerAdapter(Context context, List<ConversionItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public RecyclerAdapter(List<ConversionItem> data){
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversion_card_item, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ConversionItem current = data.get(position);
        holder.title.setText("From " + current.getFrom().toString() + "\nTo " + current.getTo().toString());
        if(current.getDistance() > 1000) {
            holder.distance.setText("Distance: " + current.getDistance()/1000 + " km away");
        }
        else {
            holder.distance.setText("Distance: " + current.getDistance() + " m away");
        }

        holder.rating.setText("Rating: " + current.getRating());
        holder.icon.setBackgroundResource(R.drawable.ic_profile);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView distance;
        public TextView rating;
        public ImageView icon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.conversion_title);
            distance = (TextView) itemView.findViewById(R.id.conversion_distance);
            rating = (TextView) itemView.findViewById(R.id.conversion_rating);
            icon = (ImageView) itemView.findViewById(R.id.conversion_icon);
        }
    }
}
