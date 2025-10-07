package com.example.expensetracker.utils.parsers;


public class SmsParserFactory {
    public static SmsParser getParser(String sender) {
        if (sender == null) return null;
        else if (sender.contains("HDFC")) return new HDFCSmsParser();
        else if (sender.contains("ICICI")) return new ICICISmsParser();
        else if(sender.contains("IDFC")) return  new IDFCSmsParser();
        else if(sender.contains("SBI")) return  new SBISmsParser();
        else if(sender.contains("INDIAN") || sender.contains("INDBNK")) return new IndianBankSmsParser();
        // Add more banks here
        return null;
    }
}
