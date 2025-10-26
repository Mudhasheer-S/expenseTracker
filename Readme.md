# 📲 Android SMS Expense Tracker

An intelligent offline Android app that automatically reads SMS transaction messages and tracks your expenses. It extracts amounts, merchants, and categorizes transactions — learning over time for recurring merchants. Track the group spend with friends. Built with Room, MVVM, and Java/Kotlin (customizable).

---

## 🎥 Demo Video
[▶️ Watch Demo (MP4)](https://github.com/Mudhasheer-S/expenseTracker/raw/main/app_demo.mp4)



## ✨ Features

- 📩 **Automatic SMS Reading**
    - Auto-detects new incoming transaction SMS
- 🔁 **Manual SMS Scan**
    - Allows scanning past/historical SMS for missed transactions
- 🧠 **Smart Categorization**
    - Assigns categories automatically based on merchant
    - Unknown merchants fall under "Default" (ID = 1)
- 📊 **Monthly Expense Summary**
    - Expenses are grouped and displayed by **month**
    - Totals by category and merchant available
- 🗂️ **Custom Categories**
    - Add your own categories and link merchants
- 📆 **Date-wise Transaction Log**
- 🔒 **Offline Only & Private**
    - Data is stored only on the device, with no internet needed
- 📊 **Category-wise Pie Chart**.
    - see pie chart on daily,monthly,yearly,custom date basics
    - see total amount on each categories
    - what transaction are on each categories
- 👥 **Add Friends Name**
    - to track friend share on group spend
- 👤 **Pay For You And Your Friends And Track It**
    - split your transaction with friends equally or by manual enter amount on each
    - track how much the friend need to pay back to you
    - you can give mark as paid on each split amount of each friend separately
    - your share is automatically update to main transaction to analysis your expense
    - you can manual enter credit amount on each friend
- 🔣 **Image Symbols**
    - on each transaction split symbol to know this transaction is splitted or not
    - on each credit in friend section click arrow symbol to know which transaction is splitted
    - bank logo are added to know the transaction is based on which bank, help to manage multiple bank

---

## For better UI
- Use dark mode
- work well for Indian Bank, KVB bank, BOB Bank. -->(add you bank, parser file for your bank and your bank logo)

## 📦 Tech Stack

- **Language**: Java (or Kotlin)
- **Architecture**: MVVM
- **Database**: Room
- **Permissions**: `READ_SMS`, `RECEIVE_SMS`

---

## 🚀 Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/Mudhasheer-S/expenseTracker.git
```
### 2. Open in Android Studio
### 3. Grant SMS Permissions
Ensure these are in your AndroidManifest.xml:

```xml

<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_SMS"/>

```
### 4. Build & Run
You can run it on your device/emulator.

### 5. APK
Download the .apk from release section of github
```bash
https://github.com/Mudhasheer-S/expenseTracker/releases/tag/v2.0.0
```

### 6. Install
- Turn on Developer Options from your settings.
- Allow installation from unknown sources (Files app).
- Disable Play Protect from Play Store if it blocks the install.

## 📌 Default Category
Default category has ID = 1

All unknown merchants are tagged to it

Users can later assign categories, which are remembered

## App version and DB version(For migration)
- App version  = 2.0 --> DB version = 13
- App version  = 2.0.1 --> DB version = 13

## 👤 Thanks
Thanks to imkks for base of code(scan message)

## 📄 License
use freely, but credit appreciated.


---
