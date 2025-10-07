package com.example.expensetracker;

import org.junit.Test;

import static org.junit.Assert.*;
import java.util.regex.*;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    // Online Java Compiler
// Use this editor to write, compile and run your Java code online
// PS : The name of the public class has to be Main for the code to work

        private static final Pattern[] ICICI_PATTERNS = new Pattern[] {
                // Pattern 1: "debited for Rs 23.00 on 20-May-25; Krishna Kumar S credited"
                //  Pattern.compile(
                //         "debited\\s+for\\s+Rs\\s*([\\d,]+\\.\\d{2}).*?;\\s*([A-Za-z\\*_0-9\\s.'&\\-/(),]+?)\\s+credited",
                //         Pattern.CASE_INSENSITIVE
                // ),
                Pattern.compile(
                        "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:\\.|;)\\s*([A-Z_a-z\\*_0-9\\s.'&\\-/(),]+?)\\s+credited", Pattern.CASE_INSENSITIVE)

                // Pattern 2: "debited with INR 3800.00 on 23-Feb-24. Acct XXX999 credited"
        };

        private static final Pattern ICICI_UPI_PATTERN = Pattern.compile(
                "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:\\.|;)\\s*([A-Za-z0-9*_\\s.&'\\-/]+?)\\s+credited",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        private static final Pattern ICICI_CARD_PATTERN = Pattern.compile(
                "(?:INR|Rs)\\s*([\\d,]+\\.\\d{2}).*?(?:on|using).*?on\\s+.*?(?:on|at)\\s*([A-Za-z0-9*_\\s.&'\\-/]+?)\\.",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        private static final Pattern HDFC_UPI_PATTERN = Pattern.compile(
                "(?:INR|Rs\\.?)\\s*([\\d,]+\\.\\d{2}).*?(?:to|To)\\s*([A-Za-z0-9\\*_\\s.&'\\-/]+?)(?:\\son|:)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        private static final Pattern HDFC_PATTERN = Pattern.compile(
                "(?:INR|Rs\\.?)\\s*([\\d,]+(?:\\.?\\d{2})?).*?(?:At|at)\\s+([A-Za-z0-9*&\\s\\(\\)\\.\\-']+?)\\s+on",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        private static final Pattern SBI_UPI_PATTERN = Pattern.compile(
                "by\\s*(?:Rs)?([\\d,]+(?:\\.?\\d{1,2})?).*?to\\s+([A-Za-z0-9*&\\s\\(\\)\\.\\*']+?)\\s+(?:Ref|Refno)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        //  private static final Pattern IDFC_CARD_PATTERN = Pattern.compile(
        //         "INR\\s*([\\d,]+\\.\\d{2}).*?at\\s+([A-Za-z0-9*\\s.&'\\-/]+?)\\s+on",
        //         Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        // );
        // private static final Pattern SBI_CARD_PATTERN = Pattern.compile(
        //         "by\\s*(?:Rs)?([\\d,]+(?:\\.?\\d{1,2})?).*?to\\s+([A-Za-z0-9*&\\s\\.\\-']+?)\\s+(?:Ref|Refno)",
        //         Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        // );
        private static final Pattern SBI_CARD_PATTERN = Pattern.compile(
                "Rs\\.?([\\d,]+(?:\\.?\\d{1,2})?).*?at\\s*([A-Za-z0-9*&\\s\\.\\-\\*\\_']+?)\\s+(?:on|Txn)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        private static final Pattern IDFC_CARD_PATTERN = Pattern.compile(
                "INR\\s*([\\d,]+\\.\\d{2}).*?at\\s*([A-Za-z0-9*\\s.&'\\-]+?)\\s+on",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        public static void parseICICISms(String message) {
            // for (Pattern pattern : ICICI_PATTERNS) {
            Matcher matcher = HDFC_PATTERN.matcher(message);
            if (matcher.find()) {

                String amount = matcher.group(1).trim();
                String merchant = matcher.group(2).trim();
                System.out.println(amount+" "+merchant);
                // return txn;
                // }
            }}
        public static void GetAll() {
            String[] icici_card={
                    "INR 369.00 spent using ICICI Bank Card XX3006 on 08-Nov-24 on Cashfree*SWIGGY. Avl Limit: INR 6,96,591.00. If not you, call 1800 2662/SMS BLOCK 3006 to 9215676766.",
                    "INR 15,039.00 spent on ICICI Bank Card XX4007 on 02-Jul-23 at Paytm_FlipkartI. Avl Lmt: INR 4,70,027.00. To dispute,call 18002662/SMS BLOCK 4007 to 9215676766"
            };
            String[] icici_upi = {
                    "ICICI Bank Acct XXX321 debited with INR 3800.00 on 23-Feb-24. Acct XXX999 credited.UPI:405427082414.Call 18002662 for dispute or SMS BLOCK 321 to 9215676766.",
                    "ICICI Bank Acct XX321 debited for Rs 23.00 on 20-May-25; SWIGGY PPS credited. UPI:514062297549. Call 18002662 for dispute. SMS BLOCK 321 to 9215676766."
            };
            String[] hdfc_upi={
                    " Sent Rs.247.05  From HDFC Bank A/C x1999 To VJ82 MY HOME MANGALA On 20/05/25 Ref 514066806230 Not You? Call 18002586161/SMS BLOCK UPI to 7308080808",
                    "Sent Rs.71.00 From HDFC Bank A/C x1999 To ROPPEN TRANS*PORTATION SERVICES PRIVATE LIMITED On 19/05/25 Ref 513953486986 Not You? Call 18002586161/SMS BLOCK UPI to 7308080808",
                    "Money Transfer:Rs 5488.00 from HDFC Bank A/c **1999 on 31-01-24 to MYGATE UPI: 403148973685 Not you? Call 18002586161",
                    "Amt Sent Rs.37.00 From HDFC Bank A/C *1999 To SmartQ On 13-11 Ref 431846337027 Not You? Call 18002586161/SMS BLOCK UPI to 7308080808"
            };
            String[] hdfc_card={
                    "Rs.53 spent on HDFC Bank Card x8302 at MYNTRA DESIGNS PRIVATE on 2024-09-21:20:48:13.Not U? To Block & Reissue Call 18002586161/SMS BLOCK CC 8302 to 7308080808",
                    "Rs.453 spent on HDFC Bank Card x8302 at Amazon Pay (india) on 2024-10-03:01:21:17.Not U? To Block & Reissue Call 18002586161/SMS BLOCK CC 8302 to 7308080808",
                    "Rs.666 spent on HDFC Bank Card x8302 at Airtel on 2024-05-18:11:12:21.Not U? To Block & Reissue Call 18002586161/SMS BLOCK CC 8302 to 7308080808",
                    "You've spent Rs.1399 On HDFC Bank CREDIT Card xx8302 At AMAZON PAY INDIA Pvt Ltd On 2023-12-26:12:50:47 Avl bal: Rs.120626 Curr O/s: Rs.4374 Not you?Call 18002586161",
                    " Rs.1566.55 spent on HDFC Bank Card x4616 at AMAZON on 2024-07-25:07:19:27 Avl bal: 149781.88.Not You? Call 18002586161 / SMS BLOCK DC 4616 to 7308080808"
            };
            String[] sbi_upi={
                    "Dear SBI User, your A/c X1098-debited by Rs100.0 on 12Jul23 transfer to RAM REDDY Ref No 355950410084. If not done by u, fwd this SMS to 9223008333/Call 1800111109 or 09449112211 to block UPI -SBI 2025",
                    "Dear UPI user A/C X1098 debited by 334.84 on date 05Jan25 trf to bigbasket Refno 500581295498. If not u? call 1800111109. -SBI"
            };
            String [] sbi_card={
                    "Dear Customer, transaction number 513403622291 for Rs.224.00 by SBI Debit Card X6808 done at 70050000 on 14May25 at 08:45:48. Your updated available balance is Rs.16086.03. If not done by you, forward this SMS to 7400165218/ call 1800111109/9449112211 to block card. GOI helpline for cyber fraud 1930.",
                    "SBIDrCard X7096 used for Rs149.00 on10Dec20 atMygate *PPS Txn#034510244296 If not done fwd this SMS to 9223008333/call1800111109/9449112211 to block Card",
                    "Dear Customer, tx#119001786927 for Rs706.82 by SBIDrCARD X7096 at Amazon FLPK_*SG on 09Jul21 at 07:27:02. If not done forward this SMS to 9223008333/18001111109/9449112211 to block card."
            };
            String [] idfc={
                    "Happy Shopping! INR 294.25 spent on your IDFC FIRST Bank Credit Card ending XX5625 at ZOMATO on 19 MAY 2025 at 01:52 AM Avbl Limit: INR 375597.91 If not done by you, call 180010888 for dispute or to block your card SMS CCBLOCK 5625 to 5676732"
            };
            for (String msg : hdfc_card) {
                parseICICISms(msg);
            }
        }

}