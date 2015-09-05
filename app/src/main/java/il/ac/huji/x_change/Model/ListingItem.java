package il.ac.huji.x_change.Model;

import com.parse.ParseUser;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by Gil on 25/05/2015.
 */
public class ListingItem {

    private ParseUser user;
    private BigDecimal amountFrom, amountTo;
    private CurrencyItem from, to;
    private float distance;

    public ListingItem(ParseUser user, String amountFrom, CurrencyItem from, String amountTo, CurrencyItem to, float distance) {
        this.user = user;
        this.amountFrom = new BigDecimal(amountFrom);
        this.from = from;
        this.amountTo = new BigDecimal(amountTo);
        this.to = to;
        this.distance = distance;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getUserName() {
        return user.getString("Name");
    }

    public BigDecimal getAmountFrom() {
        return amountFrom;
    }

    public CurrencyItem getFrom() {
        return from;
    }

    public BigDecimal getAmountTo() {
        return amountTo;
    }

    public CurrencyItem getTo() {
        return to;
    }

    public float getDistance() {
        return distance;
    }

    public int getUserRating() {
        return user.getInt("rating");
    }

    public String getUserID() {
        return user.getObjectId();
    }

    public static Comparator<ListingItem> Default
            = new Comparator<ListingItem>() {

        public int compare(ListingItem item1, ListingItem item2) {

            float distance1 = item1.getDistance();
            float distance2 = item2.getDistance();

            int rating1 = item1.getUserRating();
            int rating2 = item2.getUserRating();

            int score1 = rating1 * 2 - (int) distance1;
            int score2 = rating2 * 2 - (int) distance2;

            //descending order
            return score2 - score1;
        }
    };

    public static Comparator<ListingItem> Distance
            = new Comparator<ListingItem>() {

        public int compare(ListingItem item1, ListingItem item2) {

            float distance1 = item1.getDistance();
            float distance2 = item2.getDistance();

            //ascending order
            return (int) (distance1 - distance2);
        }
    };

}
