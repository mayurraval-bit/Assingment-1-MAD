# 📚 Student Study Planner

A simple and student-friendly **Android Study Planner** application built with **Kotlin** and **ConstraintLayout**.  
It helps students create study tasks, organize them by subject and priority, set reminders, track completion, and view overall study progress.

> **Project type:** B.Tech / MAD student project  
> **Platform:** Android  
> **Language:** Kotlin

## ✨ Features

- 🏠 **Home Dashboard**
  - Greeting based on time of day
  - Today's task summary
  - Completed and pending task counts
  - Study progress percentage
  - Today's study task list

- 📝 **Task Management**
  - Add new study tasks
  - Edit existing tasks
  - Delete tasks
  - Mark tasks as completed
  - Set subject/course
  - Add description or notes
  - Select date and start/end time
  - Set priority: Low, Medium, High

- 🔎 **Search & Filters**
  - Search tasks by title or subject
  - Filter by All, Pending, Completed, or High Priority

- 🔔 **Study Reminders**
  - Optional reminder at the task start time
  - Android AlarmManager based notifications

- 📊 **Progress Tracking**
  - Overall completion percentage
  - Total, completed, pending and high-priority task counts
  - Subject-wise task breakdown
  - Motivational progress messages

- 💾 **Local Storage**
  - Uses Room Database
  - Tasks remain available locally on the device

## 🛠️ Technologies Used

| Technology | Usage |
|---|---|
| Kotlin | Main programming language |
| Android Studio | Development environment |
| ConstraintLayout | UI layout |
| Material 3 | UI components and styling |
| RecyclerView | Task list |
| Room Database | Local task storage |
| ViewModel | UI/data state management |
| LiveData | Observing database changes |
| Kotlin Coroutines | Background database operations |
| AlarmManager | Study reminders |

## 🧩 Project Structure

```text
Stuudy_Planner/
├── app/
│   └── src/main/
│       ├── java/com/example/stuudy_planner/
│       │   ├── adapter/
│       │   ├── data/
│       │   ├── receiver/
│       │   ├── repository/
│       │   ├── ui/
│       │   ├── utils/
│       │   └── viewmodel/
│       ├── res/
│       │   ├── drawable/
│       │   ├── layout/
│       │   ├── menu/
│       │   ├── mipmap/
│       │   └── values/
│       └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 📱 Application Screens

### Home Dashboard
<img width="272" height="600" alt="WhatsApp Image 2026-09-21 at 8 04 15 PM" src="https://github.com/user-attachments/assets/eb6d56a6-574a-4c5d-8741-f22470a7319f" />


### Task Management
<img width="272" height="600" alt="WhatsApp Image 2026-09-21 at 8 04 15 PM1" src="https://github.com/user-attachments/assets/d03d8f92-f000-4a6f-93fc-be7dcf6c4a7a" />

### Progress Tracking
<img width="267" height="600" alt="WhatsApp Image 2026-09-21 at 8 04 15 PM2" src="https://github.com/user-attachments/assets/7462dc04-e87b-4c8f-8eb6-c46280dac6b8" />


## 🚀 How to Run

1. Download or clone this repository.
2. Open the `Stuudy_Planner` folder in Android Studio.
3. Allow Gradle to sync and download required dependencies.
4. Connect an Android device or start an emulator.
5. Click **Run ▶**.
6. The application will open with the splash screen and then show the study dashboard.

### Current project configuration

- **Minimum SDK:** 24
- **Target SDK:** 36
- **Compile SDK:** 36.1
- **Java compatibility:** Java 11
- **Gradle Wrapper:** 9.4.1

## 🎯 Main Application Flow

```text
Splash Screen
      ↓
Home Dashboard
      ↓
Add Study Task
      ↓
Save Task
      ↓
Task List
      ↓
Mark Complete / Edit / Delete
      ↓
Progress Tracking
