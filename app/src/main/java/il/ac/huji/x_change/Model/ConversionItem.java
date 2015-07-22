package il.ac.huji.x_change.Model;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class ConversionItem {

    private CurrencyItem from, to;
    private int distance, rating;

    public ConversionItem(CurrencyItem from, CurrencyItem to, int distance, int rating) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.rating = rating;
    }

    public CurrencyItem getFrom() {
        return from;
    }

    public void setFrom(CurrencyItem from) {
        this.from = from;
    }

    public CurrencyItem getTo() {
        return to;
    }

    public void setTo(CurrencyItem to) {
        this.to = to;
    }

    public int getDistance() {
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
}
