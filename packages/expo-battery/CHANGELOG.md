# Changelog

## Unpublished

### 🛠 Breaking changes

- Removed following types: `BatteryLevelUpdateListener `, `BatteryStateUpdateListener` and `PowerModeUpdateListener` as they were only wrapping one-argument events responses. Use event types explicitly instead: `BatteryLevelEvent `, `BatteryStateEvent` and `PowerModeEvent`. ([#12592](https://github.com/expo/expo/pull/12592) by [@Simek](https://github.com/simek))

### 🎉 New features

### 🐛 Bug fixes

- Enable kotlin in all modules. ([#12716](https://github.com/expo/expo/pull/12716) by [@wschurman](https://github.com/wschurman))

## 4.1.0 — 2021-03-10

### 🎉 New features

- Updated Android build configuration to target Android 11 (added support for Android SDK 30). ([#11647](https://github.com/expo/expo/pull/11647) by [@bbarthec](https://github.com/bbarthec))

### 🐛 Bug fixes

- Remove peerDependencies and unimodulePeerDependencies from Expo modules. ([#11980](https://github.com/expo/expo/pull/11980) by [@brentvatne](https://github.com/brentvatne))

## 4.0.0 — 2021-01-15

### 🛠 Breaking changes

- Dropped support for iOS 10.0 ([#11344](https://github.com/expo/expo/pull/11344) by [@tsapeta](https://github.com/tsapeta))

## 3.1.0 — 2020-11-17

_This version does not introduce any user-facing changes._

## 3.0.0 — 2020-08-18

### 🛠 Breaking changes

- Added support for FULL state on web. ([#8937](https://github.com/expo/expo/pull/8937) by [@EvanBacon](https://github.com/EvanBacon))

### 🎉 New features

- Remove `fbjs` dependency. ([#8822](https://github.com/expo/expo/pull/8822) by [@EvanBacon](https://github.com/EvanBacon))

## 2.2.1 — 2020-05-29

_This version does not introduce any user-facing changes._

## 2.2.0 — 2020-05-27

### 🛠 Breaking changes

- Removed deprecated `isSupported` method. ([#7206](https://github.com/expo/expo/pull/7206) by [@bbarthec](https://github.com/bbarthec))
