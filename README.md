<p align="center">
  <img src="branding/logo-candidates/candidate-d.png" width="160" alt="Legado Vox 图标">
</p>

<h1 align="center">Legado Vox</h1>

<p align="center">
  基于 Legado 与 legado-with-MD3 二次开发的 Android 阅读、听书与 AI 辅助阅读应用。
</p>

<p align="center">
  <a href="https://github.com/Autsunset/legado-vox/releases"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/Autsunset/legado-vox?include_prereleases&style=flat-square"></a>
  <a href="https://github.com/Autsunset/legado-vox/actions/workflows/verify.yml"><img alt="Verify" src="https://img.shields.io/github/actions/workflow/status/Autsunset/legado-vox/verify.yml?branch=main&style=flat-square&label=verify"></a>
  <img alt="Android 8.0+" src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white">
  <a href="LICENSE"><img alt="GPL-3.0" src="https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square"></a>
</p>

Legado Vox 保留 Legado 成熟的书源规则、书架、阅读器、订阅和 Web 服务能力，并重点完善 Material Design 3 界面、应用内 AI TTS、多角色朗读、主题与书架管理体验。它拥有独立的应用 ID、版本线、品牌图标、发布签名和发行渠道，可与上游应用分别安装。

中文界面中的应用名称为“阅读 Vox”，项目英文名与 GitHub 仓库名为 “Legado Vox”。

> [!IMPORTANT]
> Legado Vox 不提供、制作或维护小说内容和第三方书源。应用首次安装时可以没有书源；请只导入你信任且有权使用的书源，并自行判断第三方规则、脚本和服务的合法性与安全性。

## 功能概览

### 阅读与书架

- 支持 TXT、EPUB、MOBI 等本地书籍，以及兼容 Legado 规则的网络书源。
- 支持目录、书签、搜索、阅读进度、替换规则、内容缓存和 WebDAV 备份恢复。
- 阅读页保留章节梗概、AI 改写等 AI 辅助功能；使用前需要配置可用的 AI 模型。
- 默认阅读背景为“羊皮纸1”，内置羊皮纸、护眼、纸张等背景可直接从背景预设中选择。
- 书籍详情页右上角菜单可以“移出书架”；书架主页右上角菜单可以进入多选模式批量移除。
- 移除本地书籍时可选择是否同时删除源文件；移除网络书籍时可选择是否同时删除离线缓存。两个选项默认均不勾选，避免误删数据。

### 听书与 TTS

- 支持 Android 系统 TTS、Legado HTTP TTS 和应用内云端 AI TTS。
- 可以为单角色朗读选择默认引擎与默认音色；未匹配人物时也会使用该默认配置。
- 支持人物与角色配音，将旁白、人物和未知对白绑定到不同音色。
- 支持听书预加载、并行预合成、段落间隔、音频缓存保留时间、流式播放和缓存清理。
- 默认短段落间隔为 `100 ms`。
- MiMo AI TTS 支持节点、API Key、音色、温度、重试次数、请求间隔和 User-Agent 配置。
- 朗读播放器支持章节切换、定时、语速调节、后台播放和通知栏媒体控制。

云端 AI TTS 在 Legado Vox 内部合成和播放音频，不会安装 Android `TextToSpeechService`，也不会修改系统默认 TTS 引擎。朗读文本会发送到你选择的服务节点，请阅读对应服务的条款和隐私政策。

### 界面、主题与图标

- Material 3 Expressive 与 Miuix 两套 Compose 主题引擎。
- 支持动态取色、浅色/深色模式、内置主题、自定义种子色和透明主题。
- 引导页切换主题时保留当前浏览位置，方便连续比较远端主题卡片。
- 提供 5 个可切换的桌面图标，默认使用 candidate-d 白发少女头像。
- 新品牌图标同时用于桌面、引导页和 About 页面。

## 下载与安装

请从 [GitHub Releases](https://github.com/Autsunset/legado-vox/releases) 下载 APK：

| 文件类型 | 适用设备 |
| --- | --- |
| `universal` | 不确定设备架构时选择，体积较大 |
| `arm64-v8a` | 绝大多数现代 64 位 Android 手机和平板 |
| `armeabi-v7a` | 较旧的 32 位 ARM 设备 |

系统要求：Android 8.0（API 26）或更高版本。

> [!NOTE]
> Release 包名为 `io.github.autsunset.legadovox`，Debug 包名为 `io.github.autsunset.legadovox.debug`。源码 namespace 仍为 `io.legado.app`，这是为了保留上游兼容性，不影响独立安装身份。

升级前建议先在“设置 → 备份与恢复”中创建备份。请只安装本仓库 Release 页面发布、且签名证书与仓库中 `signing-certificate.sha256` 一致的 APK。

## 快速开始

1. 安装并打开阅读 Vox，完成主题与隐私引导。
2. 导入自己的本地书籍，或从可信渠道导入兼容的 Legado 书源。
3. 搜索书籍并加入书架，点击书籍开始阅读。
4. 在阅读页打开朗读设置，进入“引擎与音色”。
5. 选择一个系统 TTS、HTTP TTS 或云 TTS 作为默认引擎；云 TTS 还需要保存并选择默认音色。
6. 如需多角色朗读，再进入“人物与角色配音”配置角色映射。

书源通常可以通过 URL、二维码、剪贴板或 JSON 文件导入。第三方书源可执行 JavaScript 规则，并可能访问对应站点；导入前请检查来源。

## 通知与后台播放

Android 13 及以上版本会在首次开始朗读或音频播放时请求通知权限，用于显示媒体控制和下载进度。拒绝通知权限不会被当作朗读失败，但系统可能不在通知栏展示完整控制器。

开始后台朗读时，应用还可能提示允许忽略电池优化。前台媒体服务会像音乐播放器一样保持播放；不同厂商系统仍可能需要在系统设置中额外允许后台运行、自启动或锁定最近任务。

相关入口：

- 通知权限：设置 → 其他设置 → 通知权限
- 后台权限：设置 → 其他设置 → 后台权限
- 朗读唤醒锁：阅读页 → 朗读设置 → 常规

## 数据与隐私

Legado Vox 的主要功能运行在本地设备，不设自有内容服务器，也不会主动上传阅读内容、书源列表或浏览记录。当前版本未内置 Firebase Analytics、Firebase Performance、Crashlytics 或其他第三方遥测项目配置。

以下功能会按用户操作连接外部服务：

- 网络书源、订阅与在线内容站点
- 用户配置的 AI 模型、云 TTS 或 HTTP TTS
- 用户配置的 WebDAV 服务
- GitHub 更新检查

凭据和请求数据由设备发送给用户选择的服务提供方，具体处理方式以服务方条款为准。完整说明可在应用的“关于 → 用户隐私与协议”和“免责声明”中查看。

## 从源码构建

### 环境要求

- JDK 21
- Android SDK 37
- Git
- 约 8 GB 可用内存和足够的 Gradle 缓存空间

快速编译检查：

```bash
./gradlew :app:compileAppDebugKotlin
```

构建并测试 Debug：

```bash
./gradlew verifyConfigArchitecture testAppDebugUnitTest assembleAppDebug
```

构建启用 R8 和资源压缩的正式版：

```bash
./gradlew assembleAppRelease
```

构建不启用 R8 的排错版本：

```bash
./gradlew assembleAppNoR8
```

APK 输出位于 `app/build/outputs/apk/app/<variant>/`。项目会分别生成 `arm64-v8a`、`armeabi-v7a` 和 `universal` APK。

### Release 签名

正式签名密钥不进入 Git。构建会从根目录 `signing.properties`、Gradle 属性或环境变量读取以下配置：

```properties
RELEASE_STORE_FILE=keystore/legado-vox-release.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=legado-vox
RELEASE_KEY_PASSWORD=your_key_password
```

`signing.properties` 与 `keystore/` 已被 `.gitignore` 排除。仓库中的 `signing-certificate.pem` 和 `signing-certificate.sha256` 只包含公钥身份信息，不包含私钥。

## 技术结构

- Kotlin、Coroutines、Flow
- Jetpack Compose、Material 3 Expressive、Miuix
- Navigation 3 类型安全路由
- Clean Architecture：`data`、`domain`、`ui`
- Koin 依赖注入
- Room 数据库
- OkHttp、Cronet、Rhino JavaScript
- 传统 View 阅读器与 Compose 新界面并存，持续向 Compose/MVI 迁移

主要模块：

| 模块 | 说明 |
| --- | --- |
| `:app` | Android 主应用、阅读器、书架、服务与 Compose 界面 |
| `:modules:book` | EPUB、TXT 等本地书籍解析 |
| `:modules:rhino` | 书源规则使用的 Rhino JavaScript 封装 |
| `modules/web` | Vue 3 远程书架与书源管理前端 |

## 项目来源与致谢

Legado Vox 是独立维护的二次开发项目，主要基于：

- [gedoor/legado](https://github.com/gedoor/legado)
- [HapeLee/legado-with-MD3](https://github.com/HapeLee/legado-with-MD3)
- [Autsunset/VoxEngine](https://github.com/Autsunset/VoxEngine)

感谢上述项目、依赖库和所有贡献者。上游代码的著作权归原作者所有；本仓库继续遵循 [GPL-3.0](LICENSE) 的要求。
