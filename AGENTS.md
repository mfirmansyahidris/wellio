# Working on Wellio

- This is a native Android application. Preserve Kotlin, Compose, Room and WorkManager architecture unless the user requests a change.
- The user explicitly requires one separate Git commit per logical stage of implementation. Use clear commit messages. Do not squash or amend prior stages. Do not create a remote or push unless requested.
- Keep local mode functional without Firebase configuration. Never commit google-services.json, keys, signing material, device paths, or build outputs.
- Preserve completed activities, historical progress and schedule edits when changing plans.
- Validate generated AI data before persisting it; keep existing plans on failures. Do not introduce medical claims or calorie targets.
- Run relevant tests for behavioral changes. The local validation baseline is documented in docs/VALIDATION.md.
