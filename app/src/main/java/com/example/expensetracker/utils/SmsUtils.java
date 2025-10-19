package com.example.expensetracker.utils;

import android.util.Log;

import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.utils.parsers.SmsParser;
import com.example.expensetracker.utils.parsers.SmsParserFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SmsUtils {
    public static String generateSmsHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
    public static String getBank(String sender) {
        if (sender == null) return null;
        String suffix = sender.toUpperCase();
        Log.d("GET BANK","Get bank"+suffix);
        if (suffix.contains("SBI")) return "SBI";
        else if (sender.contains("INDBNK") || sender.contains("INDIAN") || sender.contains("IND")) {
            return "INDIAN_BANK";
        }
        else if (sender.contains("KVBUPI") || sender.contains("KVBANK") || sender.contains("KVB")) return "KVB_BANK";
        else if (sender.contains("BOBSMS") || sender.contains("BOB")) return "BOB_BANK";
        else if (suffix.contains("HDFC")) return "HDFC";
        else if (suffix.contains("ICICI")) return "ICICI";
        else if (suffix.contains("IDFC")) return "IDFC";
        else if (suffix.contains("YES")) return "YES";
        else return null;



    }
    public static Expense parseExpense(String body, String bank) {
//        double amount = extractAmount(body);
        SmsParser smsParser = SmsParserFactory.getParser(bank);
        if (smsParser != null) {
            return smsParser.parse(body,bank);
//            return new Expense(amount, desc, System.currentTimeMillis(), bank);
        } else {
            Log.d("SmsReceiver", "No parser found for sender: " + bank);
            return null;
//            return new Expense(amount, "ERR", System.currentTimeMillis(), bank);

        }

    }}
