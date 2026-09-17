# AndroidAutoVideo

AndroidAutoVideo is a small Android project with two parts:

- A phone/tablet YouTube launcher screen for parked/passenger-safe use.
- An Android Auto compatible media browser service so the app can appear in the Android Auto app launcher.

It intentionally does **not** bypass Android Auto driving restrictions. Use it only when the vehicle is parked or when a passenger display supports video.

## Features

- Simple YouTube URL launcher on the phone screen
- Android Auto media browser entry point
- Car-mode awareness message
- Automotive manifest metadata for media app discovery
- GitHub Actions CI that builds a debug APK artifact

## Build

GitHub Actions builds the debug APK on every push to `main`.

Artifact name:

`AndroidAutoVideo-debug-apk`

Local build, if Android SDK and Gradle are installed:

```bash
gradle assembleDebug
```

Output:

`app/build/outputs/apk/debug/app-debug.apk`

## Safety

Android Auto phone projection does not show arbitrary Activity/video apps on the car screen. This project uses the supported media app path for Android Auto visibility. It does not bypass Android Auto driving restrictions or play YouTube video on the car display while driving.
