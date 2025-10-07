package com.example.expensetracker.utils.parsers;
import android.util.Log;

import com.example.expensetracker.data.model.Expense;

import java.time.LocalDate;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class HDFCSmsParser implements SmsParser {
    private static final Pattern HDFC_UPI_PATTERN = Pattern.compile(
            "(?:INR|Rs\\.?)\\s*([\\d,]+\\.\\d{2}).*?(?:to|To)\\s*([A-Za-z0-9\\*_\\s.&'\\-/]+?)(?:\\son|:)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern HDFC_CARD_PATTERN = Pattern.compile(
            "(?:INR|Rs\\.?)\\s*([\\d,]+(?:\\.?\\d{2})?).*?(?:At|at)\\s+([A-Za-z0-9*&\\s\\(\\)\\.\\-']+?)\\s+on",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

//    private static final Pattern HDFC_PATTERN = Pattern.compile(
//            "(?:Spent\\s+Rs\\.?|Rs\\.?)([\\d,]+(?:\\.\\d{1,2})?)\\s+(?:spent\\s+on|On)?\\s*HDFC Bank Card.*?at\\s+([A-Za-z0-9*&\\s\\.\\-']+?)\\s+on",
//            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//    );
//    private static final Pattern HDFC_UPI_PATTERN =Pattern.compile(
//            "(?:Sent|Amt Sent)\\s+Rs\\.?(\\d+(?:\\.\\d{1,2})?)\\s+From.*?To\\s+([A-Za-z0-9\\s*&'\\-/\\.]+?)\\s+On",
//            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//    );
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
    private  Expense parseCard(String sms,String bank) {
        Matcher matcher = HDFC_CARD_PATTERN.matcher(sms);
        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1).replace(",", "").trim());
            String merchant = matcher.group(2).trim();
//            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(3), DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss"));
//            long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return new Expense(amount,merchant,0L,bank,1);
//            System.out.println("Amount: " + amount);
//            System.out.println("Merchant: " + merchant);
//            System.out.println("DateTime: " + timestamp);
        } else {
            Log.e("HDFC CARD Parser",sms);
            return null;
        }
    }
    private  Expense parseUPI(String sms,String bank) {
        Matcher matcher = HDFC_UPI_PATTERN.matcher(sms);
        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1).trim());
            String payee = matcher.group(2).trim();
            LocalDate date = LocalDate.parse(matcher.group(3), DateTimeFormatter.ofPattern("dd/MM/yy"));
            long timestamp =date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return new Expense(amount,payee,0L,bank,1);

        } else {
            Log.e("HDFC UPI Parser",sms);

            return  null;
        }
    }

}

