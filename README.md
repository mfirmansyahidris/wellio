# Wellio

**Small steps. A healthier you.** Wellio is an Android wellness application based on the supplied product concept. It provides a daily plan, gentle movement, meal ideas, routines, reminders, and simple progress tracking.

Android application ID and namespace: `com.fi.wellio`.

## Run the project

1. Open the `wellio` directory in Android Studio.
2. Use JDK 17 or 21, Android SDK 36, and a device or emulator running Android 8.0 (API 26) or later.
3. Allow Android Studio to create `local.properties`, then press **Run**.

From a terminal with suitable `JAVA_HOME` and `ANDROID_HOME` values:

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest lintDebug
./gradlew connectedDebugAndroidTest
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The Gradle Wrapper is included. Dependency versions are pinned for reproducible builds; the project does not claim to use every latest release.

## Available features

- Short onboarding for up to two goals, activity, experience, available time, location, equipment, diet, cuisine, priorities, and foods to avoid.
- A seven-day local plan that works without an account. After installation, core planning remains available without an internet connection.
- Four primary destinations: **Today, Workout, Meals, and Profile**.
- Pending, completed, skipped, and rescheduled activity states. A task time can be changed within the selected day.
- Set-by-set workout sessions that can be paused and resumed after restarting the application. Duration is an estimate, not a stopwatch or required target.
- Targeted workout replacement by reason and single-meal replacement without changing the rest of the day.
- Simple ingredients and cooking instructions without calorie counting.
- Per-task reminders using Android notification permission and WorkManager. Delivery time is approximate. Past times do not create new reminders.
- Weekly progress, completion percentage, streak, and local history. Completing one activity keeps a daily streak; skipped activities do not add progress.
- Optional Firebase AI Logic support for seven-day plans, workout replacement, and meal replacement. Structured JSON responses are validated before storage.

## Local mode and AI mode

The application starts in **local mode**. It uses a catalog and deterministic planning rules, and the interface clearly identifies the plan source.

To connect cloud AI, follow the [Firebase setup guide](docs/FIREBASE.md). The user explicitly enables AI from Profile after reviewing what data is sent. The application never calls AI silently on launch. Errors, timeouts, and invalid output leave the saved plan unchanged.

## Data behavior

Room stores application data on the device. There is no login, cloud synchronization, advertising, or analytics. Cloud AI receives only planning preferences and the item being replaced; the user's name and completion history are excluded.

Updating the profile refreshes unfinished activities while preserving completed activities, edited schedules, and history. Missing future days are generated locally when the application opens. If the local catalog cannot satisfy a previously saved AI preference, the application repeats validated content from the latest plan and labels it **Saved plan repeat**. A new AI week is generated only after a user action.

## MVP limitations

- The local meal and workout catalogs are intentionally limited.
- Rescheduling changes the time on the selected date; it does not move a task to another date.
- Foods to avoid are preferences, not medical allergy screening. Recipes do not provide exact weights, portions, or nutritional calculations.
- Wellio is not a medical service and does not diagnose or treat health conditions.
- Wearable integration, workout video, cloud sync, and user accounts are outside this iteration.
- The generated APK is a debug build for testing and is not signed for production distribution.

## Development history

Each logical implementation stage has its own commit. Run `git log --oneline --reverse` to review the progression from foundation through models, onboarding, Today, workouts, meals, reminders, AI integration, testing, and package migration. No remote or push was created.

The local Git identity is `Wellio Project <wellio@localhost>` because no user Git identity was available when the repository was created. Change the local Git configuration before future commits if needed.

- [Product concept](docs/CONCEPT.md)
- [Architecture decisions](docs/ARCHITECTURE.md)
- [Firebase setup](docs/FIREBASE.md)
- [Validation record](docs/VALIDATION.md)

The supplied concept ended at the beginning of section 28 (`UserProfile`). Profile attributes were completed from the onboarding requirements in the preceding sections.
