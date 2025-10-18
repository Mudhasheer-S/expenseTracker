package com.example.expensetracker.utils.parsers;

import android.util.Log;
import com.example.expensetracker.data.model.Expense;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BobBankSmsParser implements SmsParser {

    // Example SMS:
    // Rs.1.00 Dr. from A/C XXXXXX4426 and Cr. to mudhasheer0786@oksbi. Ref:565749134643. AvIBal:Rs167.18(2025:10:18 02:58:22). Not you? Call 18005700/5000-BOB
    private static final Pattern BOB_PATTERN = Pattern.compile(
            "Rs\\.?\\s*(\\d+(?:\\.\\d{1,2})?)\\s*(Dr|Cr)\\.?" +        // Amount and Dr/Cr with optional dot
                    "\\s*from\\s*A/C\\s*([Xx0-9]+)\\s*and\\s*Cr\\.\\s*to\\s*([A-Za-z0-9@._-]+)\\." + // Account & payee
                    "\\s*Ref:([0-9]+)\\.\\s*AvIBal:Rs\\s*(\\d+(?:\\.\\d{1,2})?)" +  // Transaction ID & Balance
                    "\\((\\d{4}[:-]\\d{2}[:-]\\d{2})\\s*(\\d{2}:\\d{2}:\\d{2})\\).*", // Date & Time with optional extra text
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public Expense parse(String msg, String bank) {
        msg = msg.trim();
        Matcher matcher = BOB_PATTERN.matcher(msg);

        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(1));
            String type = matcher.group(2).equalsIgnoreCase("Dr") ? "debited" : "credited";
            String account = matcher.group(3);
            String payee = matcher.group(4);
            String transactionId = matcher.group(5);
            double balance = Double.parseDouble(matcher.group(6));
            String dateStr = matcher.group(7).replace("-", ":"); // normalize date to match formatter
            String timeStr = matcher.group(8);

            String fullDateTime = dateStr + " " + timeStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            long timestamp = LocalDateTime.parse(fullDateTime, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            Expense expense = new Expense(amount, payee, timestamp, "BOB_BANK", 1);
            expense.setType(type);
            expense.setAccount(account);
            expense.setTransactionId(transactionId);

            Log.d("BOB_BANK_PARSER", "Parsed successfully: " + payee + " | " + amount);
            return expense;
        } else {
            Log.e("BOB_BANK_PARSER", "No match for SMS: " + msg);
            return null;
        }
    }
}
