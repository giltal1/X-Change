package il.ac.huji.x_change.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import il.ac.huji.x_change.Activity.MessagingActivity;
import il.ac.huji.x_change.Model.Constants;
import il.ac.huji.x_change.Model.ListingItem;
import il.ac.huji.x_change.R;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class ListingItemAdapter extends RecyclerView.Adapter<ListingItemAdapter.RecyclerViewHolder>
        implements View.OnClickListener {

    private List<ListingItem> data = Collections.emptyList();
    private Context context;

    public ListingItemAdapter(List<ListingItem> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing_card, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        ListingItem current = data.get(position);
        holder.name.setText(current.getUserName());
        holder.titleFrom.setText(current.getAmountFrom() + " " + current.getFrom().getCode());
        holder.titleTo.setText(current.getAmountTo() + " " + current.getTo().getCode());
        if (current.getDistance() > 1000) {
            holder.distance.setText(" " + (int) current.getDistance() / 1000 + " km");
        } else {
            holder.distance.setText(" " + (int) current.getDistance() + " meters");
        }
        String ratingTag = context.getResources().getString(R.string.profile_rating);
        holder.rating.setText(ratingTag + " " + current.getUserRating());
        holder.id = current.getUserID();
        holder.icon.setBackgroundResource(R.drawable.ic_profile);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(current.getUserID(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    ParseFile file = (ParseFile) object.get("Image");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                if (e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    holder.icon.setImageBitmap(bitmap);
                                } else {
                                    Log.e(getClass().getSimpleName(), e.getMessage());
                                    holder.icon.setBackgroundResource(R.drawable.ic_profile);
                                }
                            }
                        });
                    }
                    else {
                        holder.icon.setBackgroundResource(R.drawable.ic_profile);
                    }
                }
                else {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                    holder.icon.setBackgroundResource(R.drawable.ic_profile);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View view) {


    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CircleImageView icon;
        public TextView name;
        public TextView titleFrom;
        public TextView titleTo;
        public TextView distance;
        public TextView rating;
        public String id;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            icon = (CircleImageView) itemView.findViewById(R.id.conversion_user_photo);
            name = (TextView) itemView.findViewById(R.id.conversion_user_name);
            titleFrom = (TextView) itemView.findViewById(R.id.conversion_title_from);
            titleTo = (TextView) itemView.findViewById(R.id.conversion_title_to);
            distance = (TextView) itemView.findViewById(R.id.conversion_distance);
            rating = (TextView) itemView.findViewById(R.id.conversion_rating);
            id = "";
        }

        @Override
        public void onClick(View view) {
            final Context context = view.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.chat_dialog_title) + " " + name.getText().toString());
            builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            Intent intent = new Intent(context, MessagingActivity.class);
                            intent.putExtra(Constants.RECIPIENT_ID, id);
                            intent.putExtra(Constants.RECIPIENT_NAME, name.getText().toString());
                            context.startActivity(intent);
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
    }
}
