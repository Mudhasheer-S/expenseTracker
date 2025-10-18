# 📲 Android SMS Expense Tracker

An intelligent offline Android app that automatically reads SMS transaction messages and tracks your expenses. It extracts amounts, merchants, and categorizes transactions — learning over time for recurring merchants. Built with Room, MVVM, and Java/Kotlin (customizable).

---

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

---

## 📦 Tech Stack

- **Language**: Java (or Kotlin)
- **Architecture**: MVVM
- **Database**: Room
- **Permissions**: `READ_SMS`, `RECEIVE_SMS`

---

## 🗃️ Database Schema

### `Transaction`
| Field        | Type    | Description                   |
|--------------|---------|-------------------------------|
| id           | Int     | Primary key                   |
| date         | String  | ISO date format               |
| amount       | Double  | Transaction amount            |
| merchant     | String  | Merchant name extracted       |
| category_id  | Int     | Foreign key to `Category`     |
| sms_content  | String  | Original SMS message          |

### `Category`
| Field | Type   | Description        |
|-------|--------|--------------------|
| id    | Int    | Primary key        |
| name  | String | Category name      |

### `Merchant`
| Field       | Type   | Description            |
|-------------|--------|------------------------|
| id          | Int    | Primary key            |
| name        | String | Merchant name          |
| category_id | Int    | Category linked to it  |

### `SMS Hash`
| Field | Type   | Description |
|-------|--------|-------------|
| hash  | String | Primary Key |
---

## 🚀 Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/your-username/sms-expense-tracker.git
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

### 5. Generate APK
Go to Build > Build APK(s) or generate signed APK for release.

🧩 Ideas for Future Enhancements
Budget limits with alerts

Export to CSV or PDF

Recurring expense detection

Pie chart dashboards

ML-based merchant recognition

Firebase / Drive backup (optional)

## 📌 Default Category
Default category has ID = 1

All unknown merchants are tagged to it

Users can later assign categories, which are remembered

## App version and DB version(For migration)
App version  = 2.0 --> DB version = 13

## 👤 Author
Made with ❤️ by Krishna

## 📄 License
MIT License – use freely, but credit appreciated.


---
