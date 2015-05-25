package il.ac.huji.x_change.Model;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class ConversionItem {

    private MoneyItem from, to;
    private int distance, rating;

    public ConversionItem(MoneyItem from, MoneyItem to, int distance, int rating) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.rating = rating;
    }

    public MoneyItem getFrom() {
        return from;
    }

    public void setFrom(MoneyItem from) {
        this.from = from;
    }

    public MoneyItem getTo() {
        return to;
    }

    public void setTo(MoneyItem to) {
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
