package com.example.expensetracker.utils.parsers;


import android.util.Log;

import com.example.expensetracker.data.model.Expense;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.*;

public class ICICISmsParser implements SmsParser {
//Pattern.compile(
//        "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:\\.|;)\\s*([A-Z_a-z\\*_0-9\\s.'&\\-/(),]+?)\\s+credited", Pattern.CASE_INSENSITIVE)
//    private static final Pattern ICICI_PATTERN_UPI = Pattern.compile(
//            "debited\\s+for\\s+Rs\\s*([\\d,]+(?:\\.\\d{1,2})?)\\s+on\\s+(\\d{2}\\-[A-Za-z]{3}\\-\\d{2});\\s*([A-Za-z0-9\\s.'&\\-/(),]+?)\\s+credited",
//            Pattern.CASE_INSENSITIVE
//    );
//    private static final Pattern ICICI_CARD_PATTERN = Pattern.compile(
//            "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2})\\s+spent.*?(?:on|at)\\s+(?:\\d{1,2}-[A-Za-z]{3}-\\d{2,4}\\s+)?(?:on|at)?\\s*([A-Za-z0-9*_\\s.&'\\-/]+?)\\.",
//            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//    );
    private static final Pattern ICICI_UPI_PATTERN = Pattern.compile(
            "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:\\.|;)\\s*([A-Za-z0-9*_\\s.&'\\-/]+?)\\s+credited",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ICICI_CARD_PATTERN = Pattern.compile(
            "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:on|using).*?on\\s+.*?(?:on|at)\\s*([A-Za-z0-9*_\\s.&'\\-/]+?)\\.",
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
        Matcher matcher = ICICI_UPI_PATTERN.matcher(sms);
        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1));
//            LocalDate date = LocalDate.parse(matcher.group(2), DateTimeFormatter.ofPattern("dd-MMM-yy"));
//            long dateLong= date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String payee = matcher.group(2);
//
//            System.out.println("Amount: " + amount);
//            System.out.println("Date: " + date);
//            System.out.println("Paid To: " + payee);
            return new Expense(amount,payee,0L,bank,1);
        } else {
            Log.e("ICICI UPI Parser",sms);
//            System.out.println("No match found.");
            return null;
        }
    }
    private Expense parseCard(String msg, String bank)
    {
        Matcher matcher = ICICI_CARD_PATTERN.matcher(msg);
        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1).replace(",", "").trim());
//            LocalDate date = LocalDate.parse(matcher.group(2), DateTimeFormatter.ofPattern("dd-MMM-yy"));
//            long dateLong= date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String merchant = matcher.group(2).trim();

//            System.out.println("Amount: " + amount);
//            System.out.println("Date: " + date);
//            System.out.println("Paid To: " + merchant);
            return new Expense(amount,merchant,0L,bank,1);
        } else {
            Log.e("ICICI card parser",msg);
//            System.out.println("No match found.");
            return null;
        }
    }


}

