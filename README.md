# 耳机电量（Wear OS）

面向 Samsung Galaxy Watch5 Pro / Wear OS 的最小原生应用，提供黑底、高对比度电量页，并注册一个表盘 complication 数据源。

## 重要限制
@@@尚不支持最新系统，无法使用
- Android 的公开蓝牙 API 并不保证所有耳机都会向手表暴露电量；某些耳机只通过厂商私有协议或手机端应用提供电量。
- 当前实现遍历已配对设备中第一个能返回电量的设备。部分 One UI Watch 版本/耳机组合可能需要进一步适配，且应用需要在手表上授予“附近设备/蓝牙”权限。
- 表盘 complication 的刷新频率受 Wear OS/表盘策略限制；不是实时秒级更新。
- 本工作环境没有 Android SDK、Gradle 或联网能力，因此无法在这里编译并验证 APK。请用 Android Studio 打开本项目，安装 SDK 35 后构建：Build > Build APK(s)。这是可构建的项目源码，不是已编译 APK。

## 构建
Android Studio 2025.x 或兼容版本，JDK 17，Android SDK Platform 35。
打开项目并等待 Gradle 同步，然后执行 Build > Build APK(s)。
