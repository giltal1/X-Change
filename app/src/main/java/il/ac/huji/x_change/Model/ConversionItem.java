package il.ac.huji.x_change.Model;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class ConversionItem {

    private BigDecimal amountFrom, amountTo;
    private CurrencyItem from, to;
    private float distance;
    private int rating;

    public ConversionItem(String amountFrom, CurrencyItem from, String amountTo, CurrencyItem to, float distance, int rating) {
        this.amountFrom = new BigDecimal(amountFrom);
        this.from = from;
        this.amountTo = new BigDecimal(amountTo);
        this.to = to;
        this.distance = distance;
        this.rating = rating;

    }

    public BigDecimal getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(BigDecimal amountFrom) {
        this.amountFrom = amountFrom;
    }

    public CurrencyItem getFrom() {
        return from;
    }

    public void setFrom(CurrencyItem from) {
        this.from = from;
    }

    public BigDecimal getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(BigDecimal amountTo) {
        this.amountTo = amountTo;
    }

    public CurrencyItem getTo() {
        return to;
    }

    public void setTo(CurrencyItem to) {
        this.to = to;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static Comparator<ConversionItem> Distance
            = new Comparator<ConversionItem>() {

        public int compare(ConversionItem item1, ConversionItem item2) {

            float distance1 = item1.getDistance();
            float distance2 = item2.getDistance();

            //ascending order
            return (int) (distance1 - distance2);
        }
    };

}
