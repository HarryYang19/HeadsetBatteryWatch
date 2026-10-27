# 在 GitHub 上自动编译 APK

1. 在 GitHub 新建一个仓库（例如 `HeadsetBatteryWatch`）。
2. 将本 ZIP 解压，把里面所有文件和 `.github` 文件夹上传到仓库根目录并提交到 `main` 分支。
3. 打开仓库的 **Actions** 标签页。如果提示启用 Actions，选择启用。
4. 选择左侧 **Build Wear OS APK**，点击 **Run workflow**（或提交到 `main` 后等待自动运行）。
5. 等待工作流完成，进入该次运行页面底部的 **Artifacts**，下载 `HeadsetBatteryWatch-debug-apk` 并解压，得到 `app-debug.apk`。
6. 将 APK 传到手机/手表后安装。它是 debug 签名包，适合个人测试；后续正式发布应使用自己的签名密钥。

注意：这是自动化构建配置，不代表 APK 已经在本地编译或经过手表实机测试。若构建失败，请打开 Actions 日志，查看失败步骤和错误信息。
