package il.ac.huji.x_change.Model;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by ulamadm on 25/05/2015.
 */
public class CurrencyItem {

    private BigDecimal amount;
    private String name;
    private String code;
    private String countryName;
    private String flagCode;
    private Currency curr;

    public CurrencyItem(BigDecimal amount, String name, String code, String countryName, String flagCode) {
        this.name = name;
        this.code = code;
        this.countryName = countryName;
        this.flagCode = flagCode;
        this.amount = amount;
        //this.curr = Currency.getInstance(code);
    }

    public String getCode() {
        return code;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getFlagCode() {
        return flagCode;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.valueOf(amount) + " " + code;
    }
}
