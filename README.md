# WakadP1 - Officer Activity Tracker

## 📱 Project Purpose
WakadP1 is an Android application designed for police officers to efficiently track and manage their daily activities, duties, and reports. It serves as a digital logbook, allowing officers to record specific tasks (e.g., patrolling, investigation, paperwork), track time spent, and maintain a history of their service duties. The app aims to replace manual entries with a streamlined digital solution that synchronizes with a central database while offering offline capabilities.

## 🌟 Key Features
*   **Officer Profiling:** Stores detailed officer information including Rank, Buckle Number, and Police Station.
*   **Daily Activity Logging:** Officers can log activities with start/end times, notes, and attachment support.
*   **Dashboard & Analytics:** Real-time dashboard showing daily total tasks and pending items.
*   **Weekly Summaries:** View performance and activity logs over a weekly period.
*   **Offline First:** Uses **Room Database** to store data locally, ensuring functionality even without internet.
*   **Cloud Sync:** Integrates with **Firebase** for authentication and data backup.
*   **Biometric Security:** Supports fingerprint/face unlock for secure access.
*   **Push Notifications:** Firebase Cloud Messaging (FCM) integration for important alerts.

## 🛠 Tech Stack
*   **Language:** Kotlin
*   **Minimum SDK:** API 24 (Android 7.0)
*   **Target SDK:** API 34 (Android 14)
*   **Architecture:** MVVM (Model-View-ViewModel) / Repository Pattern
*   **Local Database:** Room
*   **Backend/Cloud:** Firebase (Auth, Realtime Database, Cloud Messaging)
*   **Concurrency:** Kotlin Coroutines & Flow
*   **UI:** XML Layouts with Material Design components
*   **Other Libraries:**
    *   AndroidX Biometric (Auth)
    *   WorkManager (Background tasks)
    *   Splash Screen API

## 🔄 Application Workflow

### 1. User Authentication
*   **Sign Up/Login:** Officers log in using their credentials (email/password).
*   **Profile Setup:** On first launch, officers set up their profile with details like **Buckle Number**, **Rank**, and **Station Name** (e.g., "Wakad Branch").

### 2. Dashboard (Home)
*   Upon login, the officer sees the **Dashboard**.
*   **Daily Stats:** Displays the count of "Total Activities" and "Pending" tasks for the current day.
*   **Quick Actions:** Access to "Add Activity" or "Weekly Summary".

### 3. Logging an Activity
*   Navigate to **Add Entry**.
*   **Select Type:** Choose the type of duty/activity from a predefined list.
*   **Time Tracking:** Input Start Time and End Time.
*   **Details:** Add optional notes and attachments (images/documents).
*   **Status:** Mark as "Pending" if the task is not yet complete.
*   **Save:** The entry is saved to the local database and synced when possible.

### 4. Review & Reports
*   **Weekly Summary:** Officers can view a breakdown of their duties over the past week.
*   **Profile:** Manage personal details and view service history.

## 🚀 Setup & Installation

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/your-username/WakadP1.git
    ```
2.  **Open in Android Studio:**
    *   Select "Open an existing Android Studio project" and point to the `WakadP1` folder.
3.  **Sync Gradle:**
    *   Allow Android Studio to download dependencies.
4.  **Firebase Configuration:**
    *   Place your `google-services.json` file in the `app/` directory.
5.  **Build & Run:**
    *   Connect an Android device (USB Debugging enabled) or use an Emulator.
    *   Click **Run**.

## 📂 Project Structure
*   `data/`: Contains Room Entities (`User`, `ActivityEntry`), DAOs, and Database configuration.
*   `services/`: Background services like `FCMService`.
*   `ui/` (implied): Activities like `DashboardActivity`, `AddEntryActivity`, `LoginActivity`.

---
*Internally developed for Wakad Police Station operational efficiency.*
