# AndroidAutoVideo

AndroidAutoVideo is a small Android project for parked/passenger-safe YouTube launching.

It intentionally does **not** bypass Android Auto driving restrictions. Use it only when the vehicle is parked or when a passenger display supports video.

## Features

- Simple YouTube URL launcher
- Car-mode awareness message
- Automotive manifest metadata for video-style parked app experiments
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

This project is designed for compliant parked/passenger-safe use. For driving scenarios, build an Android Auto media/audio app instead of video playback.
