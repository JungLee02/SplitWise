# SplitMate — Expense Sharing & Bill Splitting App

**SplitMate** is a modern Android expense-sharing application inspired by the core workflows of Splitwise, built with modern Android development standards including **Jetpack Compose**, **Clean Architecture + MVVM**, **Hilt**, **Room**, and **Firebase**.

> **"Add expenses once. Split them fairly. Know exactly who owes whom."**

---

## 📱 Features

- **Authentication & User Profiles**:
  - Secure Email/Password registration & login via Firebase Auth.
  - User profile management and customizable display names.

- **Friends Network**:
  - Search and add friends via email.
  - View individual friends and manage friend lists.

- **Groups Management**:
  - Create custom expense groups (e.g. Goa Trip, Flatmates, Office Lunch).
  - Add members, view group expense feeds, and track individual group member roles.

- **Flexible Expense Splitting Engine**:
  - **Equal Split**: Evenly divides expenses with exact integer rounding adjustments.
  - **Exact Split**: Specify custom split amounts per participant.
  - **Percentage Split**: Divide expenses by exact percentages with validation.
  - **Shares Split**: Split expenses proportionally based on shares (e.g., 1 share vs 2 shares).
  - **Currency Precision**: Monetary values are stored as integer minor units (paise) to eliminate floating-point calculation errors.

- **Smart Debt Simplification**:
  - Automatically calculates net balances for all group members.
  - Uses a greedy debt simplification algorithm to reduce the total number of transactions required to settle up.

- **Settlement Recording**:
  - Mark debts as settled with custom notes (e.g. Paid via UPI).
  - Recalculates net balances in real-time.

- **Offline-First Architecture**:
  - Reactive local database caching using **Room**.
  - Background synchronization with **Cloud Firestore**.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture (Presentation, Domain, Data) + MVVM
- **State Management**: StateFlow & Compose State
- **Dependency Injection**: Hilt
- **Local Persistence**: Room Database (Room 2.8.5 with KSP)
- **Backend & Auth**: Firebase Authentication & Cloud Firestore
- **Asynchronous**: Kotlin Coroutines & Flow
- **Build System**: Gradle Version Catalogs (Kotlin DSL)

```text
                  PRESENTATION
        ┌────────────────────────────┐
        │ Compose UI (Material 3)    │
        │ ViewModel & UI State       │
        └──────────────┬─────────────┘
                       ↓
                   DOMAIN
        ┌────────────────────────────┐
        │ Use Cases                  │
        │ Split Calculator           │
        │ Debt Simplification Engine │
        └──────────────┬─────────────┘
                       ↓
                    DATA
        ┌────────────────────────────┐
        │ Repository Implementations │
        │ Room DAOs & Local Database │
        │ Cloud Firestore            │
        └────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 35 / Android 14+ device or emulator

### Setup Instructions
1. **Clone the repository**:
   ```bash
   git clone https://github.com/<your-username>/SplitMate.git
   cd SplitMate
   ```
2. **Firebase Setup**:
   - Create a project on the [Firebase Console](https://console.firebase.google.com/).
   - Enable **Firebase Authentication** (Email/Password) and **Cloud Firestore**.
   - Download `google-services.json` and place it in the `app/` directory.
3. **Build & Run**:
   - Open the project in Android Studio.
   - Run Gradle sync and deploy to your connected device/emulator.

---

## 🧪 Testing

Run unit tests for split algorithms and debt simplification:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
