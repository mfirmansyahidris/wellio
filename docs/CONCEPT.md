# Wellio Product Concept

## Product overview

Wellio is an Android health and lifestyle application that helps people build a healthier routine through personalized workouts, meal ideas, daily habits, reminders, and simple progress tracking.

Tagline: **Your daily wellness plan, powered by AI.**

The application is intended for everyday wellness. It is not a medical product and must not diagnose, treat, or replace professional healthcare advice.

## Product promise

The user should not need to design a routine from scratch. They provide:

- wellness goals;
- current activity level;
- workout experience;
- available workout time and location;
- available equipment;
- dietary and cuisine preferences;
- food priorities and foods to avoid.

Wellio turns those preferences into a simple, realistic daily plan.

## Target users

- Beginners who do not know where to start.
- Busy people with approximately 10–30 minutes available each day.
- People who want easier meal decisions.
- General wellness users who do not need advanced athletic programming or complex tracking.

## Product principles

- **Simple:** collect only information needed for useful recommendations.
- **Personalized:** account for goals, time, activity, experience, equipment, and food preferences.
- **Actionable:** provide clear activities for today instead of long articles.
- **Flexible:** allow replacement, skipping, rescheduling, and continuation.
- **Low friction:** no required account, cloud sync, calorie counting, or wearable for the MVP.

## Primary navigation

`Today | Workout | Meals | Profile`

### Today

The main dashboard shows:

- today's completion percentage;
- the next workout;
- breakfast, lunch, snack, and dinner;
- lightweight habits;
- reminder state;
- task completion status.

Each task supports `Pending`, `Completed`, `Skipped`, and `Rescheduled` states.

### Workout

Workouts use the user's goal, experience, equipment, duration, and location. A workout contains:

- title and description;
- difficulty and estimated duration;
- location and required equipment;
- exercises with sets, repetitions or duration, rest, and instructions.

The session view advances set by set and supports pausing and resuming. Completing all sets allows the user to finish the workout and updates Today automatically.

The user may replace a workout for one of these reasons:

- too difficult;
- too easy;
- required equipment is unavailable;
- disliked exercises;
- a shorter workout is needed.

### Meals

The daily meal plan contains breakfast, lunch, snack, and dinner. Each meal provides:

- preparation estimate;
- ingredients;
- short preparation steps;
- dietary and practical tags.

The user may replace only one meal while keeping the remainder of the plan. Replacement reasons include dislike, unavailable ingredients, lower cost, faster preparation, or a surprise alternative.

The MVP does not count calories. Foods to avoid are treated as preferences rather than medical allergy guarantees.

### Profile and progress

The profile stores onboarding preferences and displays:

- workouts completed this week;
- routine completion percentage;
- current streak;
- current wellness goals and planning preferences.

Progress exists to reinforce consistency rather than competition. A day contributes to a streak when at least one activity is completed.

## Onboarding

1. Welcome and product promise.
2. Select up to two wellness goals.
3. Select current activity level.
4. Select beginner or intermediate workout experience.
5. Select 10, 20, 30, 45, or 60 minutes.
6. Select home, gym, or outdoor workout location.
7. Select available equipment.
8. Select dietary preference, cuisine, food priorities, and foods to avoid.
9. Review preferences and create the first seven-day plan.

Supported wellness goals:

- Stay Active
- Get Stronger
- Eat Better
- Weight Management
- Build Better Habits
- Improve Overall Wellness

Supported dietary preferences:

- Regular
- Vegetarian
- Vegan
- High Protein
- Low Carb

The MVP avoids categories that imply treatment for a medical condition.

## Routines and reminders

Routine tasks may include:

- water after waking;
- breakfast;
- a short walk;
- a workout;
- dinner;
- winding down before sleep.

WorkManager schedules approximate background reminders. Exact alarms are not an MVP requirement. The application rechecks task state before showing a notification and does not notify for completed or skipped tasks.

## AI responsibilities

Cloud AI is reserved for areas that benefit from personalization:

1. Seven-day wellness plan generation.
2. Workout generation and replacement.
3. Meal recommendation and replacement.

Local logic handles completion state, streaks, reminders, notifications, schedules, timers, progress, and storage.

## AI architecture

```text
Android application
        |
Firebase AI Logic
        |
Gemini
        |
Structured JSON response
        |
Validation and domain models
        |
Room database
        |
Compose interface
```

Firebase AI Logic avoids embedding a direct Gemini API key in the application. App Check protects the client integration from unauthorized use.

AI output must use a response schema rather than free-form Markdown. Generated content is validated before storage. Invalid, empty, timed-out, or failed responses leave the existing plan unchanged.

## Core data models

### UserProfile

- name;
- up to two goals;
- activity level;
- workout experience;
- available minutes;
- location;
- equipment;
- diet and cuisine;
- food priorities;
- foods to avoid;
- explicit AI opt-in.

### Workout

- identifier or title;
- description;
- difficulty;
- duration;
- location;
- equipment;
- exercises.

### Exercise

- name;
- sets;
- repetitions or seconds;
- rest duration;
- instruction.

### Meal

- meal type;
- name;
- preparation duration;
- ingredients;
- instructions;
- tags;
- cuisine and budget indication.

### Daily task

- stable identifier;
- task type;
- title;
- local date and time;
- completion state;
- reminder preference.

## MVP exclusions

- mandatory login;
- cloud synchronization;
- wearable integration;
- workout video;
- exact alarms;
- advanced fitness analytics;
- medical diagnosis or treatment;
- calorie targets;
- automatic background AI generation.
