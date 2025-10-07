package com.example.expensetracker.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.SmsHashDao;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.SmsHash;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.utils.SmsUtils;

import java.util.concurrent.Executors;

public class SmsReceiver extends BroadcastReceiver {

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        Log.d("SMS RECEIVER","NEW MSG");
//        if (bundle != null) {
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            String format = bundle.getString("format"); // <-- New
//            if (pdus != null) {
//                for (Object pdu : pdus) {
//                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu,format);
//                    String sender = message.getOriginatingAddress();
//                    String body = message.getMessageBody();
//                    long timestamp = message.getTimestampMillis();
//                    long timestampRounded = (timestamp / (60 * 1000)) * (60 * 1000);
//                    String bank= SmsUtils.getBank(sender);
//                    if (bank != null) {
//                        String raw = sender + "|" + body + "|" + timestampRounded;
//                        String hash = SmsUtils.generateSmsHash(raw);
////                        Log.d("Worker",String.format("hash %s address %s body %s timestamp %s",hash,sender
////                                ,body,timestamp));
//
//                        Expense expense = SmsUtils.parseExpense(body, bank);
//                        if(expense!=null) {
//                            Executors.newSingleThreadExecutor().execute(()->{
//                                SmsHashDao hashDao = AppDatabase.getInstance(context).smsHashDao();
//                                hashDao.insert(new SmsHash(hash));
//
//                                ExpenseRepository expenseRepository = new ExpenseRepository((Application) context.getApplicationContext());
//                                expense.categoryId=expenseRepository.resolveCategoryIdForMerchant(expense.merchant);
//                                expense.date = timestamp;
//                                expenseRepository.insert(expense);
//                            });
//
//                        }
//                    }
//                    else
//                    {
//                        Log.e("SMS RECEIVER",body);
//                    }
//                }
//            }
//        }
//    }



//    }




    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("SMS RECEIVER","NEW MSG");
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format"); // <-- New
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu,format);
                    String sender = message.getOriginatingAddress();
                    String body = message.getMessageBody();
                    long timestamp = message.getTimestampMillis();
                    long timestampRounded = (timestamp / (60 * 1000)) * (60 * 1000);
                    String bank = SmsUtils.getBank(sender);
                    if (bank != null) {
                        Log.d("SMS_RECEIVER", "Bank detected: " + bank);  // <--
                        Expense expense = SmsUtils.parseExpense(body, bank);
                        if(expense != null) {
                            String raw = sender + "|" + body + "|" + timestampRounded;
                            String hash = SmsUtils.generateSmsHash(raw);
                            Log.d("SMS_RECEIVER", "Expense parsed: " + expense.amount + " from " + expense.merchant);  // <--
                            Executors.newSingleThreadExecutor().execute(()->{
                                SmsHashDao hashDao = AppDatabase.getInstance(context).smsHashDao();
                                hashDao.insert(new SmsHash(hash));

                                ExpenseRepository expenseRepository = new ExpenseRepository((Application) context.getApplicationContext());
                                expense.categoryId=expenseRepository.resolveCategoryIdForMerchant(expense.merchant);
                                expense.date = timestamp;
                                expenseRepository.insert(expense);
                            });
                        } else {
                            Log.e("SMS_RECEIVER", "Parsing failed for body: " + body);
                        }
                    } else {
                        Log.e("SMS_RECEIVER", "Bank not recognized for sender: " + sender);
                    }

                }
            }
        }
    }


}




