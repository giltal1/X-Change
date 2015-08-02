package il.ac.huji.x_change.Model;

import java.util.Currency;


/**
 * Created by ulamadm on 25/05/2015.
 */
public class CurrencyItem {

    private String name;
    private String code;
    private String flagCode;
    private Currency curr; //TODO: check if needed

    public CurrencyItem(String code, String name, String flagCode) {
        this.code = code;
        this.name = name;
        this.flagCode = flagCode;
        //this.curr = Currency.getInstance(code);
    }

    public String getCode() {
        return code;
    }

    public String getFlagCode() {
        return flagCode;
    }

    public String getName() {
        return name;
    }

}
