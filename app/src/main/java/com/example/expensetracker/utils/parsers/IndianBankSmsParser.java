package com.example.expensetracker.utils.parsers;

import android.util.Log;
import com.example.expensetracker.data.model.Expense;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndianBankSmsParser implements SmsParser {

    // Updated pattern to also capture UPI Ref / transaction ID
    // Example: A/c *1151 debited Rs. 1.00 on 28-09-25 to NAGARJUN. UPI:563732863223.
    private static final Pattern INDIAN_BANK_PATTERN = Pattern.compile(
            "A/c \\*?(\\d+)\\s+(debited|credited)\\s+Rs\\.?\\s*(\\d+(?:\\.\\d{1,2})?)\\s+on\\s+(\\d{2}-\\d{2}-\\d{2})\\s+to\\s+([A-Za-z0-9@._ ]+?)\\.\\s*UPI:(\\d+)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public Expense parse(String msg, String bank) {
        msg = msg.trim();
        Matcher matcher = INDIAN_BANK_PATTERN.matcher(msg);
        if (matcher.find()) {
            String account = matcher.group(1);       // Account number
            String type = matcher.group(2);          // debited / credited
            double amount = Double.parseDouble(matcher.group(3)); // Amount
            String dateStr = matcher.group(4);       // Date
            String payee = matcher.group(5).trim();  // Payee name
            String transactionId = matcher.group(6); // UPI transaction ID

            // Parse date dd-MM-yy
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            long timestamp = LocalDate.parse(dateStr, formatter)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            Expense expense = new Expense(amount, payee, timestamp, "INDIAN_BANK", 1);
            expense.setType(type);
            expense.setAccount(account);
            expense.setTransactionId(transactionId); // ✅ store transaction ID

            return expense;
        } else {
            Log.e("INDIAN_BANK_PARSER_V2", "No match for SMS: " + msg);
            return null;
        }
    }
}
