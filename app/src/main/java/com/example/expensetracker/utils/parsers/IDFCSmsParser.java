package com.example.expensetracker.utils.parsers;

import android.util.Log;

import com.example.expensetracker.data.model.Expense;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDFCSmsParser implements SmsParser {
    private static final Pattern IDFC_CARD_PATTERN = Pattern.compile(
            "INR\\s*([\\d,]+\\.\\d{2})\\s+spent.*?at\\s+([A-Za-z0-9*\\s.&'\\-/]+?)\\s+on",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    @Override
    public Expense parse(String msg, String bank) {
        msg=msg.toLowerCase();
        if(msg.contains("card") && !msg.contains("otp"))
            return parseCard(msg,bank);
        else if (msg.contains("upi"))
            return  parseUPI(msg,bank);
        else
            return null;
    }
    private Expense parseUPI(String sms,String bank) {
       return null;
    }
    private Expense parseCard(String msg, String bank)
    {
        Matcher matcher = IDFC_CARD_PATTERN.matcher(msg);
        if (matcher.find()) {
            double amount = Double.parseDouble( matcher.group(1).replace(",", ""));
            String merchant = matcher.group(2).trim();

            return new Expense(amount,merchant,0L,bank,1);
        } else {
            Log.e("IDFC UPI Parser",msg);
            return null;
        }
    }

}
