package il.ac.huji.x_change.Model;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class MoneyItem {

    private String currency;
    private int amount;

    public MoneyItem(String currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
