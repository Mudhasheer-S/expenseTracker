package com.example.expensetracker.utils.parsers;

import com.example.expensetracker.data.model.Expense;

public interface SmsParser {
    Expense parse(String messageBody,String bank);
}
