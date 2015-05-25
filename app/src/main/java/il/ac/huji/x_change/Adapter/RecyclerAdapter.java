package il.ac.huji.x_change.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ConversionItem current = data.get(position);
        //holder.title.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        //        public TextView title;
//        public ImageView icon;
        public String test;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.title);
//            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
