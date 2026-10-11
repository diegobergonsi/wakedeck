# WakeDeck

Website: [wakedeck.app](https://wakedeck.app)

WakeDeck is an Android app that wakes your computers with Wake-on-LAN. Its main feature is a set of home-screen widgets that you can customize.

## Features

- Wake a computer with one tap, from the app, a widget, a launcher shortcut or a Quick Settings tile.
- Three home-screen widgets: a single device, a device list and a device grid. Each widget has its own colors, transparency, corner radius, icon, label, text size, density and layout.
- Online and offline status for every device. It refreshes every few seconds in the app, on demand with the refresh button or by pulling the list down, and in the background for widgets.
- Groups and "Wake all" for a whole group.
- Optional remote shutdown over SSH for Linux and Windows machines. It signs in with a key that stays in the Android Keystore. There are no passwords, and the host key is pinned after you confirm its fingerprint.
- Import and export of your devices as a JSON file. An import shows a preview first and flags devices that point outside your home network.
- A setup guide inside the app for the PC side: BIOS and network card settings, SSH, and waking from outside your home network over a VPN.
- Light, dark or system theme in the WakeDeck navy color scheme, with an optional switch for Material You wallpaper colors. The layout adapts to foldable screens.
- Languages: English, Portuguese (Brazil and Portugal), French, German, Spanish, Chinese and Japanese. On Android 13 and newer you can pick the app language inside the app.

The translations have not been reviewed by native speakers yet.

## Privacy

WakeDeck has no ads, no analytics and no telemetry. Device data stays on your phone. The app only talks to the devices you add, on your local network or through your own VPN. The full policy is at [wakedeck.app/privacy](https://wakedeck.app/privacy) (source: [docs/privacy.html](docs/privacy.html)).

Permissions: `INTERNET`, `ACCESS_NETWORK_STATE` and `POST_NOTIFICATIONS`. The last one is declared only because MagicOS hides toasts from apps that cannot post notifications. WakeDeck never posts a notification.

## Requirements

- Android 8.0 (API 26) or newer. The target is Android 16 (API 36).
- A computer with Wake-on-LAN turned on in its BIOS or UEFI and in its network card settings.
- A phone on the same network as the computer. To wake it from outside, use a VPN and set the device's broadcast address to your subnet broadcast, for example `192.168.1.255`.

## Build

You need JDK 17 and the Android SDK (platform 37).

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

A signed release bundle (`./gradlew bundleRelease`) reads its upload key from `~/keys/signing.properties`, which is not part of this repository.

## Tech

Kotlin, Jetpack Compose with Material 3, Jetpack Glance for the widgets, Hilt, Room, DataStore, WorkManager and sshj. The app is a single module with a Model-View-ViewModel structure.

## Status

The app is not on Google Play yet. The listing texts and the privacy policy are ready.

## License

All rights reserved. The code is public so that you can see how the app works, but it is not open source. See [LICENSE](LICENSE). Third-party libraries keep their own licenses, listed in the app under "Open source licenses".
