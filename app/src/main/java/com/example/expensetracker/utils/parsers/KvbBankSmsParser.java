package com.example.expensetracker.utils.parsers;

import android.util.Log;
import com.example.expensetracker.data.model.Expense;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KvbBankSmsParser implements SmsParser {

    // Example: Your a/c XXXXXXXXXXXX8639 is debited Rs. 64.00 on 13-Sep-2025 to WESTERN GHARTS EXPORTS info :P2A/111192009468.
    private static final Pattern KVB_PATTERN = Pattern.compile(
            "Your\\s+a/c\\s+X*([0-9]{3,})\\s+is\\s+(debited|credited)\\s+Rs\\.?\\s*(\\d+(?:\\.\\d{1,2})?)\\s+on\\s+(\\d{2}-[A-Za-z]{3}-\\d{4})\\s+to\\s+([A-Za-z0-9@._&\\- ]+)\\s+info\\s*:?\\s*([A-Za-z0-9/]+)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public Expense parse(String msg, String bank) {
        msg = msg.trim();
        Matcher matcher = KVB_PATTERN.matcher(msg);

        if (matcher.find()) {
            String account = matcher.group(1);       // Last digits of account
            String type = matcher.group(2);          // debited or credited
            double amount = Double.parseDouble(matcher.group(3)); // Amount
            String dateStr = matcher.group(4);       // Date (e.g. 13-Sep-2025)
            String payee = matcher.group(5).trim();  // Merchant / Payee
            String transactionId = matcher.group(6); // info: P2A/111192009468

            // Parse date "dd-MMM-yyyy"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            long timestamp = LocalDate.parse(dateStr, formatter)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            // Create Expense model
            Expense expense = new Expense(amount, payee, timestamp, "KVB_BANK", 1);
            expense.setType(type);
            expense.setAccount(account);
            expense.setTransactionId(transactionId);

            Log.d("KVB_BANK_PARSER", "Parsed successfully: " + payee + " | " + amount);
            return expense;
        } else {
            Log.e("KVB_BANK_PARSER", "No match for SMS: " + msg);
            return null;
        }
    }
}
