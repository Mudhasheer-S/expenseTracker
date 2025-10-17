package com.example.expensetracker.worker;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.SmsHashDao;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.SmsHash;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.utils.SmsUtils;

import java.util.HashSet;
import java.util.Set;

public class SmsScanWorker extends Worker {

    private final Context context;

    public SmsScanWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SMS_SCAN", "SmsScanWorker START");

        SmsHashDao hashDao = AppDatabase.getInstance(context).smsHashDao();
        ExpenseRepository expenseRepository =
                new ExpenseRepository((Application) context.getApplicationContext());

        Set<String> existingHashes = new HashSet<>(hashDao.getAllHashes());
        Log.d("SMS_SCAN", "existingHashes size=" + existingHashes.size());

        long ninetyDaysAgo = System.currentTimeMillis() - (4 * 90L * 24 * 60 * 60 * 1000);
        String selection = "date >= ?";
        String[] selectionArgs = {String.valueOf(ninetyDaysAgo)};

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver()
                .query(uriSms, null, selection, selectionArgs, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                long timestampRounded = (timestamp / (60 * 1000)) * (60 * 1000);

                String bank = SmsUtils.getBank(address);
                Log.d("GET BANK", "Get bank=" + bank);

                if (bank != null) {
                    String raw = address + "|" + body + "|" + timestampRounded;
                    String hash = SmsUtils.generateSmsHash(raw);

                    Expense parsedExpense = SmsUtils.parseExpense(body, bank);

                    if (parsedExpense == null) continue;

                    if (!existingHashes.contains(hash)) {
                        // ---- New expense ----
                        Log.d("SMS_SCAN", "NEW expense hash=" + hash);
                        hashDao.insert(new SmsHash(hash));
                        parsedExpense.date = timestamp;
                        parsedExpense.categoryId =
                                expenseRepository.resolveCategoryIdForMerchant(parsedExpense.merchant);
                        expenseRepository.insert(parsedExpense);

                    } else {
                        // ---- Already scanned ----
//                        Log.d("SMS_SCAN", "ALREADY SCANNED hash=" + hash);
//
//                        Expense existingExpense = expenseRepository.findByMerchantAmountAndDate(
//                                parsedExpense.merchant,
//                                parsedExpense.amount,
//                                timestamp
//                        );
//
//                        // If we find the expense, check if merchant has a saved category
//                        if (existingExpense != null) {
//                            int mappedCategoryId =
//                                    expenseRepository.resolveCategoryIdForMerchant(parsedExpense.merchant);
//
//                            Log.d("SMS_SCAN", "Mapped category for " + parsedExpense.merchant + " = " + mappedCategoryId);
//
//                            if (mappedCategoryId > 0 &&
//                                    existingExpense.categoryId != mappedCategoryId) {
//                                existingExpense.categoryId = mappedCategoryId;
//                                expenseRepository.update(existingExpense);
//                                Log.d("SMS_SCAN", "Updated category for existing expense " + existingExpense.id);
//                            }
//                        }
                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        return Result.success();
    }
}
