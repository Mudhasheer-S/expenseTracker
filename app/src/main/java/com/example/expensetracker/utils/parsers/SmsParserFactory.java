package com.example.expensetracker.utils.parsers;


public class SmsParserFactory {
    public static SmsParser getParser(String sender) {
        if (sender == null) return null;
        else if (sender.contains("HDFC")) return new HDFCSmsParser();
        else if (sender.contains("ICICI")) return new ICICISmsParser();
        else if(sender.contains("IDFC")) return  new IDFCSmsParser();
        else if(sender.contains("SBI")) return  new SBISmsParser();
        else if(sender.contains("INDIAN") || sender.contains("INDBNK") || sender.contains("IND")) return new IndianBankSmsParser();
        else if(sender.contains("KVBUPI") || sender.contains("KVB") || sender.contains("KVBANK")) return new KvbBankSmsParser();
        else if(sender.contains("BOBSMS") || sender.contains("BOB")) return new BobBankSmsParser();

        // Add more banks here
        return null;
    }
}
