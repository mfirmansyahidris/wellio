# Implementation validation

Validation was run on October 1, 2026, in the local development environment.

## Results

| Check | Result |
| --- | --- |
| `assembleDebug` | Passed; debug APK generated |
| `compileReleaseKotlin` | Passed; includes the release App Check provider |
| `testDebugUnitTest` | 14 tests passed, 0 failures |
| `lintDebug` | Passed with no errors; dependency update notices remain |
| Instrumentation on Android 15 / API 35 ARM64 emulator | 4 tests passed, 0 failures |

The application APK and AndroidTest APK were installed on a dedicated development emulator. No commands were run against the connected physical device.

## Covered behavior

- Seven unique dates, four meals per day, and stable task identifiers.
- Vegan preferences, avoided foods, item replacement, and rejection when the local catalog has no valid candidate.
- Progress, streak, skip behavior, storage serialization, and preservation of completed activities during plan refresh.
- AI output validation for duplicate dates, unavailable equipment, excessive sets, avoided ingredients, and dietary labels that conflict with ingredients.
- Concurrent Room transactions preserve both changes.
- Full UI flow from onboarding through Today, completion, activity recreation, meal replacement, workout sets, and Profile.
- Invalid time input, rescheduling, skipping, and resetting a task to pending.
- WorkManager retains identical pending work, replaces it when a schedule changes, and cancels it when a task is completed.
- The earlier package migration to `com.fi.wellio`, including manifest components, Firebase resources, unit tests, and device tests.

## Package migration to `fi.dev.wellio`

- Updated the application ID, namespace, Kotlin source and test packages, source directory layout, and Room schema location.
- `testDebugUnitTest` and `assembleDebug` passed with the old Firebase configuration temporarily removed. The pre-existing local edit in `PlanValidation.kt` was excluded from this test run and is not included in the migration commit.
- `bundleRelease` and `compileDebugAndroidTestKotlin` passed in local mode. The debug APK package, release manifest, and manifest embedded in the unsigned AAB use `fi.dev.wellio`. The local Firebase configuration was restored after validation.
- The earlier emulator and Firebase-resource checks above do not validate the new package. Firebase configuration for `fi.dev.wellio` must be downloaded before building a release with Firebase enabled.

## Validation limits

- A direct Gemini request has not been verified against the configured Firebase project.
- Notification delivery on a physical device, vendor battery restrictions, reboot behavior, and timezone changes have not been tested end to end.
- There is no signed release build, Play Store publication, comprehensive accessibility audit, or production-readiness claim.
- Cloud connectivity, App Check enforcement, quotas, and billing still require testing in Firebase Console.

## Release DEX optimization (October 2, 2026)

- Enabled R8 code shrinking, optimization and obfuscation, plus resource shrinking for the release build. Increased the app version to `0.1.1` / version code `2` for the replacement bundle.
- `:app:bundleRelease` passed in local mode. The local `google-services.json` still targets the old package, so it was temporarily moved aside for this check and restored afterward; it remains ignored by Git.
- The generated AAB contains R8 metadata and a `mapping.txt`. R8 reports `noObfuscationPercentage` of 12.62% for this build. This is build metadata, not Play Console's final score; verify the reported category after uploading the new AAB.
- The generated local AAB is unsigned because this Gradle build does not configure the upload keystore. Sign the replacement with the same Play upload key before uploading, and retain the matching `mapping.txt` for crash analysis.
