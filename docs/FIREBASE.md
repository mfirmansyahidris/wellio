# Enable Firebase AI Logic

The Firebase integration is implemented and compiled into the application. The local planner remains available when Firebase is not configured.

1. Create or select a Firebase project and register an Android application with package name `com.fi.wellio`.
2. Enable Firebase AI Logic with the Gemini Developer API in Firebase Console.
3. Download `google-services.json` to `app/google-services.json`. This file is ignored by Git. Never place a Gemini API key directly in source code or application assets.
4. Register App Check. Debug builds use `DebugAppCheckProviderFactory`; register the debug token shown by the test device and never share it. Release builds use Play Integrity. Enable Firebase AI Logic enforcement after App Check has been verified.
5. Choose a model available to the Firebase project. The default is `gemini-3.8-flash`. Override it with the `wellioAiModel` Gradle property when necessary, for example: `./gradlew -PwellioAiModel=YOUR_ENABLED_MODEL assembleDebug`.
6. Restart the application. Open **Profile → Use cloud AI**, review the data notice, enable AI, then select **Generate 7 days with AI**.
7. Test plan generation, meal and workout replacement, output rejection, offline behavior, App Check, quota, and cost before production distribution.

## Integration behavior

- `FirebasePlanner` sends one request for a seven-day plan or one replacement item.
- `Schemas` defines the JSON response schema. `PlanValidation` verifies day count and dates, duration, equipment, exercise structure, avoided ingredients, and dietary tags.
- The user's name and completion history are not sent. Preferences are encoded as JSON, and free-text values are treated as data.
- Requests have a 90-second timeout. There is no automatic retry that could duplicate usage or cost.
- A failed response never replaces the saved plan. Task status changes remain local operations.
- The user can disable AI and continue using the saved plan.

## Official references

- [Firebase AI Logic overview](https://firebase.google.com/docs/ai-logic)
- [Structured output for Android](https://firebase.google.com/docs/ai-logic/generate-structured-output?platform=android)
- [App Check debug provider](https://firebase.google.com/docs/app-check/android/debug-provider)
- [App Check for AI Logic](https://firebase.google.com/docs/ai-logic/app-check)
- [Supported models](https://firebase.google.com/docs/ai-logic/models)

The current `google-services.json` is registered for `com.fi.wellio`. A successful build confirms client configuration parsing but does not prove that Gemini requests, quotas, billing, or App Check enforcement work in the Firebase project. Those items require a live cloud test.
