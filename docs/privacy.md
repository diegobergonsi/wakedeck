# WakeDeck privacy policy

Last updated: 11 October 2026

WakeDeck is a Wake-on-LAN app for Android made by Diego Bergonsi. This policy explains what the app does with your data. In short: WakeDeck does not collect, share or sell any personal data, and it has no ads, analytics or tracking.

## What stays on your phone

Everything you enter in WakeDeck is stored only on your phone:

- The devices you add: name, MAC address, IP address or host name, broadcast address, ports, operating system, group, and the optional SSH user name and port.
- Your widget, shortcut and Quick Settings tile settings.
- The last known online or offline status of your devices.
- For the optional remote shutdown feature, an SSH key pair. The private key is created inside the Android Keystore and cannot be read or copied out of it. The app never asks for or stores a password. The app also stores the fingerprint of each PC's SSH host key that you chose to trust.

None of this is sent to the developer or to any server run by the developer. WakeDeck has no account, no server and no backend. App data is excluded from Android cloud backup.

## Network traffic

WakeDeck only connects to the addresses you configure:

- A Wake-on-LAN magic packet (UDP) to the broadcast address you set for a device.
- Online or offline checks: a TCP connection attempt or a ping to the device's host.
- Remote shutdown, only when you tap it and confirm: an SSH connection to the PC you set up.

This traffic goes directly from your phone to your own devices, over your Wi-Fi or over a VPN that you run. It does not pass through the developer.

## Files you choose to create

The export function writes your device list to a file you pick. The file does not contain your SSH key or trusted host keys. WakeDeck does not upload it anywhere. What you do with the file is up to you. The import function reads a file you pick.

## Permissions

- Internet and network state: to send the packet and check device status.
- Wake lock: to finish a wake or a status check while the screen is off.
- Notifications: only so the system lets the app show its short confirmation message after a wake from a launcher shortcut. WakeDeck does not send notifications, reminders or promotions.

## Third parties

WakeDeck contains no advertising, analytics, crash reporting or other third-party services that collect data. It uses open-source libraries that run on your phone.

## Children

WakeDeck is not directed at children and collects no data from anyone.

## Changes

If this policy changes, the new version will be published at this address with a new date.

## Contact

Diego Bergonsi, diego.bergonsi@outlook.com
