# Architecture decisions

- Android native, Kotlin + Jetpack Compose. Four destinations: Today, Workout, Meals, Profile.
- Single activity, state hoisted to a ViewModel, unidirectional updates. UI observes Room through Flow.
- Room stores a versioned JSON aggregate in a singleton row. All updates use a database transaction so concurrent completion and plan generation cannot silently overwrite each other. This MVP favors atomic plan writes over SQL analytics; normalized tables can be introduced with a migration later.
- Dates are local ISO dates; times are local HH:mm. Seven-day rolling plans retain past progress. A day counts toward the streak when at least one activity is completed. Skips never count as completion.
- Local catalog personalization is available immediately and is labelled as local. Firebase AI Logic is opt-in after developer configuration. No Gemini API key is bundled. Generated content is parsed and validated before persistence; failures leave the existing plan intact.
- WorkManager schedules approximate notifications for enabled tasks. It is not an exact alarm. Completed/skipped tasks are checked again before notification. Reopening the app reconciles schedules with current timezone.
- No login, analytics, calorie tracking, advertising, or cloud sync in this iteration. App data is local; Android backup is disabled. Cloud AI receives only planning preferences after explicit in-app opt-in.
- The imported concept ends at section 28. Profile fields follow its onboarding and food-preference requirements. Beginner/intermediate are implemented as allowed by the MVP scope.

## Incremental history

Each implementation stage is committed separately. There is no squash, amend, or force-push workflow. Build outputs, device paths, Firebase configuration, and signing material are excluded from version control.
