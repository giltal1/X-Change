package il.ac.huji.x_change.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class CurrencyItemAdapter extends RecyclerView.Adapter<CurrencyItemAdapter.Holder> {

    private List<CurrencyItem> currencyList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;

    public CurrencyItemAdapter(Activity activity, Context context, List<CurrencyItem> currencyList) {
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
        this.currencyList = currencyList;
    }

    public CurrencyItemAdapter(List<CurrencyItem> currencyList) {
        this.currencyList = currencyList;
    }

    public void add(CurrencyItem item) {
        currencyList.add(item);
        notifyDataSetChanged();
    }

    public void delete(int position) {
        currencyList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        Holder holder = new Holder(v, activity);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CurrencyItem currency = currencyList.get(position);
        String flag = currency.getFlagCode().toLowerCase();
        int resID = context.getResources().getIdentifier(flag, "drawable", context.getPackageName());
        if (resID != 0) {
            holder.currencyImage.setImageResource(resID);
        }
        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView currencyImage;
        protected TextView currencyCode;
        protected TextView currencyName;
        protected Activity activity;

        public Holder(View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            currencyImage = (ImageView) itemView.findViewById(R.id.currency_img);
            currencyCode = (TextView) itemView.findViewById(R.id.currency_code);
            currencyName = (TextView) itemView.findViewById(R.id.currency_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TextView currencyCode = (TextView) v.findViewById(R.id.currency_code);
            ImageView currencyImage = (ImageView) v.findViewById(R.id.currency_img);
            currencyImage.buildDrawingCache();
            Bitmap image = currencyImage.getDrawingCache();

            Intent intent = new Intent();
            intent.putExtra("currencyCode", currencyCode.getText().toString());
            intent.putExtra("currencyImage", image);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }
}
