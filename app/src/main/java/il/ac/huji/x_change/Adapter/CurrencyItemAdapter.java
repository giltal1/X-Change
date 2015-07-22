package il.ac.huji.x_change.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import il.ac.huji.x_change.Model.CurrencyItem;
import il.ac.huji.x_change.R;

public class CurrencyItemAdapter extends RecyclerView.Adapter<CurrencyItemAdapter.Holder> {

    private List<CurrencyItem> currencyList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public CurrencyItemAdapter(Context context, List<CurrencyItem> currencyList) {
        this.context = context;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CurrencyItem currency = currencyList.get(position);
        String flag = currency.getFlagCode().toLowerCase();
        int resID = context.getResources().getIdentifier(flag, "drawable", context.getPackageName());
        if (resID != 0) {
            holder.img.setImageResource(resID);
        }
        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());
        holder.countryName.setText(currency.getCountryName());
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

        protected ImageView img;
        protected TextView currencyCode;
        protected TextView currencyName;
        protected TextView countryName;

        public Holder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.currency_img);
            currencyCode = (TextView) itemView.findViewById(R.id.currency_code);
            currencyName = (TextView) itemView.findViewById(R.id.currency_name);
            countryName = (TextView) itemView.findViewById(R.id.country_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v.findViewById(R.id.country_name);
            Toast.makeText(context, tv.getText().toString() + " is chosen", Toast.LENGTH_SHORT).show();
        }
    }
}
