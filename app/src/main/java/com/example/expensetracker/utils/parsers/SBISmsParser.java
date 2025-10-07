package com.example.expensetracker.utils.parsers;

import android.util.Log;

import com.example.expensetracker.data.model.Expense;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBISmsParser implements SmsParser {
//    private static final Pattern SBI_UPI_PATTERN = Pattern.compile(
//            "debited by\\s*([\\d,.]+)\\s+.*?trf to\\s+([A-Za-z0-9\\s*&'\\-/\\.]+?)\\s+Refno",
//            Pattern.CASE_INSENSITIVE
//    );
//    private static final Pattern SBI_DEBIT_PATTERN = Pattern.compile(
//            "for\\s+Rs\\.?(\\d+(?:\\.\\d{1,2})?)\\s+by SBI Debit Card.*?done at\\s+([A-Za-z0-9\\s*&'\\-/\\.]+?)\\s+on",
//            Pattern.CASE_INSENSITIVE
//    );
private static final Pattern SBI_CARD_PATTERN = Pattern.compile(
        "Rs\\.?([\\d,]+(?:\\.?\\d{1,2})?).*?at\\s*([A-Za-z0-9*&\\s\\.\\-\\*\\_']+?)\\s+(?:on|Txn)",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
);
//    private static final Pattern SBI_CARD_PATTERN = Pattern.compile(
//        "Rs([\\d,]+(?:\\.?\\d{1,2})?).*?at\\s*([A-Za-z0-9*&\\s\\.\\-\\*\\_']+?)\\s+(?:on|Txn)",
//        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//);
    private static final Pattern SBI_UPI_PATTERN = Pattern.compile(
            "by\\s*(?:Rs)?([\\d,]+(?:\\.?\\d{1,2})?).*?to\\s+([A-Za-z0-9*&\\s\\.\\*']+?)\\s+(?:Ref|Refno)",
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
            Matcher matcher = SBI_UPI_PATTERN.matcher(sms);
            if (matcher.find()) {
                double amount = Double.parseDouble(matcher.group(1).replace(",", "").trim());
                String payee = matcher.group(2).trim();
                return new Expense(amount,payee,0L,bank,1);

            } else {
                Log.e("SBI UPI Parser",sms);
                return null;
            }
    }
    private Expense parseCard(String msg, String bank)
    {
        Matcher matcher = SBI_CARD_PATTERN.matcher(msg);
        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1).trim());
            String merchant = matcher.group(2).trim();
return  new Expense(amount,merchant,0L, bank,1);
//            System.out.println("Amount: " + amount);
//            System.out.println("Merchant/Location: " + merchant);
        } else {
            Log.e("SBI card Parser",msg);
            return  null;
        }
    }
}
