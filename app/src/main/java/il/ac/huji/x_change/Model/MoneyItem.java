package il.ac.huji.x_change.Model;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class MoneyItem {

    private String currency;
    private double amount;

    public MoneyItem(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.valueOf(amount) + " " + currency;
    }
}
