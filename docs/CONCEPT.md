# AI Wellness Planner

## 1. Product Overview

**Working name:** Wellio  
**Category:** Health & Wellness / Lifestyle  
**Platform awal:** Android  
**Core idea:** Personal wellness assistant yang membantu user menjalani gaya hidup lebih sehat melalui rekomendasi workout, meal ideas, daily routine, reminder, dan progress tracking sederhana.

Aplikasi bukan aplikasi medis dan tidak ditujukan untuk diagnosis, pengobatan, atau penggantian konsultasi tenaga kesehatan.

### Tagline

**Your daily wellness plan, powered by AI.**

### Core Promise

User tidak perlu menyusun rutinitas sendiri.

Cukup tentukan:
- tujuan;
- kondisi aktivitas sehari-hari;
- preferensi workout;
- preferensi makanan;
- waktu yang tersedia.

Aplikasi akan membentuk daily wellness plan yang sederhana dan realistis.

---

# 2. Problem yang Diselesaikan

Banyak orang ingin:
- mulai olahraga;
- makan lebih baik;
- memiliki rutinitas teratur;

tetapi tidak tahu harus mulai dari mana.

Aplikasi fitness tradisional sering:
- terlalu kompleks;
- terlalu fokus pada kalori;
- membutuhkan banyak input;
- menyediakan program yang terasa generik;
- mengharuskan user mengatur semuanya sendiri.

Wellio mengambil pendekatan:

> “Tell us about yourself. We'll prepare your day.”

---

# 3. Target User

### Primary User

Pengguna umum yang:
- ingin lebih aktif;
- ingin mulai olahraga;
- ingin makan lebih teratur;
- tidak membutuhkan program atlet profesional;
- tidak ingin melakukan tracking kompleks;
- menginginkan rekomendasi sederhana.

### Contoh Persona

**Beginner**

> “Saya ingin mulai olahraga, tetapi tidak tahu harus melakukan latihan apa.”

**Busy Worker**

> “Saya hanya punya waktu sekitar 20 menit setiap hari.”

**Healthy Lifestyle User**

> “Saya ingin makan lebih baik tetapi bingung menentukan menu.”

---

# 4. Product Principles

Aplikasi mengikuti beberapa prinsip utama.

### Simple

User tidak harus mengisi puluhan parameter.

### Personalized

Plan menyesuaikan:
- goal;
- waktu;
- tingkat aktivitas;
- equipment;
- makanan yang disukai;
- makanan yang dihindari.

### Actionable

AI tidak memberikan artikel panjang.

AI memberikan:

> Do this today.

### Flexible

User bisa:
- mengganti meal;
- mengganti workout;
- skip;
- reschedule.

### Low Friction

Untuk MVP:
- tidak wajib login;
- tidak wajib cloud sync;
- tidak wajib input kalori;
- tidak wajib wearable.

---

# 5. Main Features

Aplikasi memiliki empat area utama.

## Today

Dashboard utama user.

Menampilkan:
- progress hari ini;
- workout berikutnya;
- meals;
- routine;
- reminder;
- completion status.

## Workout

Menampilkan workout berdasarkan profil user.

## Meals

Memberikan meal ideas berdasarkan preferensi.

## Profile

Mengatur goal dan personal preferences.

Bottom navigation:

`Today | Workout | Meals | Profile`

---

# 6. Onboarding

Onboarding bertujuan mendapatkan informasi yang benar-benar dibutuhkan untuk personalisasi.

Tidak perlu terlalu panjang.

## Screen 1 — Welcome

Headline:

**Build a healthier routine that fits your life.**

CTA:

`Get Started`

---

## Screen 2 — Wellness Goal

Pertanyaan:

**What do you want to improve?**

Pilihan:

- Stay Active
- Get Stronger
- Eat Better
- Weight Management
- Build Better Habits
- Improve Overall Wellness

Untuk MVP user memilih maksimal 2 goal.

---

## Screen 3 — Activity Level

**How active are you currently?**

Pilihan:

- Mostly Sitting
- Lightly Active
- Active
- Very Active

---

## Screen 4 — Workout Level

**What's your workout experience?**

Pilihan:

- Beginner
- Intermediate
- Advanced

Untuk MVP sebenarnya Beginner + Intermediate sudah cukup.

---

## Screen 5 — Available Time

**How much time can you spend on a workout?**

Pilihan:

- 10 minutes
- 20 minutes
- 30 minutes
- 45 minutes
- 60 minutes

---

## Screen 6 — Workout Location

**Where do you usually exercise?**

Pilihan:

- Home
- Gym
- Outdoor

---

## Screen 7 — Available Equipment

Pilihan contoh:

- No equipment
- Dumbbells
- Resistance bands
- Bench
- Barbell
- Treadmill
- Exercise bike

User bisa memilih beberapa.

---

# 7. Food Preferences

## Dietary Preference

Pilihan:

- Regular
- Vegetarian
- Vegan
- High Protein
- Low Carb

Jangan menggunakan kategori yang mengarah ke terapi kondisi medis pada MVP.

---

## Preferred Cuisine

Contoh:

- Indonesian
- Asian
- Western
- Japanese
- Korean
- Anything

---

## Food Priorities

User dapat memilih:

- Simple cooking
- Budget friendly
- High protein
- Quick preparation
- Balanced
- Lots of vegetables

---

## Foods to Avoid

Input berupa selectable chips atau free text.

Contoh:

`Seafood`

`Spicy Food`

`Milk`

`Peanuts`

Untuk MVP, ini diperlakukan sebagai **preference**, bukan diagnosis alergi medis.

---

# 8. Generate First Plan

Setelah onboarding:

**Creating your wellness plan...**

AI membuat plan selama beberapa hari sekaligus.

Rekomendasi awal:

**Generate 7-day plan.**

Alasan:
- lebih hemat request AI;
- user mendapatkan pengalaman konsisten;
- aplikasi tetap berfungsi tanpa memanggil AI setiap dibuka.

---

# 9. Today Screen

Today merupakan homepage utama.

Contoh struktur:

```text
Good Morning 👋

Today's Progress
████████████░░░  70%
5 of 7 activities completed

NEXT
┌─────────────────────────────┐
│ 🏋️ Full Body Workout        │
│ 20 min • Beginner           │
│                        →    │
└─────────────────────────────┘

MEALS

Breakfast
🥣 Banana Oatmeal
07:30

Lunch
🍛 Chicken Rice Bowl
12:30

Dinner
🥗 Tofu Vegetable Bowl
19:00

ROUTINE

✓ Morning Water
✓ Breakfast
○ Workout
○ Dinner
○ Wind Down
```

---

# 10. Daily Task Status

Setiap task mempunyai status:

- Pending
- Completed
- Skipped
- Rescheduled

User dapat melakukan:

`Done`

`Skip`

`Reschedule`

---

# 11. Workout Feature

Workout dibuat berdasarkan:

- user goal;
- experience;
- available equipment;
- duration;
- workout location.

---

# 12. Workout Card

Contoh:

```text
Full Body Beginner

20 MIN
6 Exercises

Squat
3 × 12

Push Up
3 × 8

Glute Bridge
3 × 12

Bird Dog
3 × 10

Plank
3 × 20 sec

Stretching
3 min
```

CTA:

`Start Workout`

---

# 13. Workout Session

Mode workout dapat dibuat sederhana.

```text
Squat

Set 2 of 3

12 REPS

[ Previous ]

[ Complete Set ]

[ Next ]
```

Tidak perlu video pada MVP.

Bisa menggunakan:
- illustration;
- static image;
- text instruction.

---

# 14. Workout Completion

Setelah selesai:

**Workout Complete 🎉**

```text
20 min
6 exercises
18 total sets
```

CTA:

`Finish`

Kemudian progress Today otomatis diperbarui.

---

# 15. Regenerate Workout

Jika user tidak cocok dengan workout:

`Replace Workout`

AI diberi konteks:

- profil user;
- workout yang sedang diganti;
- alasan jika tersedia.

Pilihan reason:

- Too difficult
- Too easy
- Don't have the equipment
- Don't like these exercises
- Need something shorter

AI kemudian membuat workout alternatif.

---

# 16. Meals

Meals screen menampilkan plan.

```text
TODAY

Breakfast
Banana Oatmeal
~10 min preparation

Lunch
Chicken Rice Bowl
~20 min preparation

Snack
Greek Yogurt & Fruit

Dinner
Vegetable Tofu Stir Fry
~20 min preparation
```

Tidak perlu calorie counting pada MVP.

---

# 17. Meal Detail

Detail meal:

**Chicken Rice Bowl**

Estimated preparation:

`20 minutes`

Ingredients:

- rice;
- chicken;
- carrot;
- broccoli;
- garlic.

Simple instructions:

1. Cook the rice.
2. Season and cook chicken.
3. Stir-fry vegetables.
4. Combine and serve.

CTA:

`Replace Meal`

---

# 18. Replace Meal

Ini adalah salah satu AI interaction utama.

AI tidak regenerate seluruh plan.

Hanya satu meal.

Contoh:

```text
Replace Dinner

Why?

○ Don't like it
○ Ingredients unavailable
○ Something cheaper
○ Something quicker
○ Surprise me
```

Generate:

> Alternative meal.

Plan kemudian diperbarui.

---

# 19. Daily Routine

Routine bukan hanya workout dan meal.

User juga bisa mempunyai lightweight habit.

Contoh default:

- Drink water after waking up
- Breakfast
- Short walk
- Workout
- Dinner
- Wind down before sleep

Routine dibuat seminimal mungkin.

---

# 20. Reminder

User bisa mengaktifkan notification pada task.

Contoh:

**Workout Reminder**

`17:30`

**Breakfast**

`07:30`

**Wind Down**

`22:30`

Untuk reminder rutin yang toleran terhadap sedikit fleksibilitas, Android WorkManager cocok untuk pekerjaan background yang tidak membutuhkan alarm pada detik tertentu. Jika suatu hari aplikasi membutuhkan alarm yang benar-benar presisi, Android memiliki pembatasan tambahan untuk exact alarms sehingga sebaiknya tidak menjadi requirement MVP.

---

# 21. Progress

Tidak perlu fitness analytics kompleks.

MVP cukup menampilkan:

### This Week

```text
Workout

M ✓
T ✓
W -
T ✓
F ○
S ○
S ○

3 workouts completed
```

### Routine Completion

`82%`

### Current Streak

`🔥 4 days`

Tujuan progress bukan kompetisi.

Tujuannya:

> memberikan feedback bahwa user konsisten.

---

# 22. AI Responsibilities

AI hanya menangani area yang memang membutuhkan personalisasi.

### AI digunakan untuk:

1. Weekly wellness plan generation
2. Workout generation
3. Workout replacement
4. Meal recommendation
5. Meal replacement

---

# 23. Hal yang Tidak Menggunakan AI

Tidak semua fitur perlu Gemini.

Local logic menangani:

- task completion;
- streak;
- reminder;
- notification;
- timer;
- progress;
- schedule;
- local storage.

Dengan demikian request AI tetap sedikit.

---

# 24. AI Architecture

Recommended flow:

```text
Android App

     │

Firebase AI Logic

     │

Gemini

     │

Structured Response

     │

App Data Model

     │

Room Database

     │

UI
```

Firebase AI Logic memang dirancang agar mobile/web client dapat memanggil Gemini melalui Firebase proxy, sehingga API key Gemini tidak perlu ditanam langsung di aplikasi. Firebase juga merekomendasikan App Check untuk melindungi endpoint AI dari unauthorized clients.

---

# 25. Structured AI Output

AI sebaiknya tidak mengembalikan Markdown bebas.

Gunakan structured output.

Firebase AI Logic mendukung response schema sehingga hasil Gemini dapat dipaksa mengikuti struktur JSON yang sudah ditentukan.

Contoh:

```json
{
  "day": "Monday",
  "workout": {
    "name": "Beginner Full Body",
    "durationMinutes": 20,
    "exercises": []
  },
  "meals": {
    "breakfast": {},
    "lunch": {},
    "dinner": {},
    "snack": {}
  }
}
```

Keuntungan:

- parsing lebih mudah;
- UI konsisten;
- model lebih sulit menghasilkan struktur aneh;
- lebih mudah melakukan validation.

---

# 26. Workout Data Model

```text
Workout

id
title
description
difficulty
durationMinutes
location
equipment[]
exercises[]
```

Exercise:

```text
Exercise

id
name
sets
reps
durationSeconds
restSeconds
instruction
```

---

# 27. Meal Data Model

```text
Meal

id
type
name
description
preparationMinutes
ingredients[]
instructions[]
tags[]
```

Meal type:

- BREAKFAST
- LUNCH
- DINNER
- SNACK

---

# 28. User Profile Model

```text
UserProfile
