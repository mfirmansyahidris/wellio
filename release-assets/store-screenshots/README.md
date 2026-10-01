# Wellio Play Store screenshots

The `raw/` directory contains eight screenshots captured directly from the Wellio app running on an Android 15 emulator. The images in `presentation/` place those real app screens in a consistent Play Store presentation layout. Each presentation image is a 1080 × 1920 PNG (9:16).

The set covers the Today, Workout, Meals, and Profile tabs, a guided workout set, a meal detail, recipe steps, and the single-meal replacement flow. `../wellio-feature-graphic.png` is a 1024 × 500 PNG for the Play Store feature graphic. `../store-screenshots-preview.jpg` is a contact sheet for review.

To regenerate the presentation assets, run `create_assets.py` with Python and Pillow installed. The raw emulator captures remain unchanged.
