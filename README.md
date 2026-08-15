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

## 使用边界与法律说明

> [!WARNING]
> 下载、安装或使用阅读 Vox，即表示使用者应自行了解并遵守所在地法律法规、目标网站服务条款及知识产权规则。本项目明确反对利用软件实施侵权传播、未授权数据获取、绕过访问控制、破坏网络服务或其他违法违规行为。

- 阅读 Vox 是用户可配置的本地阅读与网页内容解析工具，默认不预置、不内置、不销售任何第三方网站内容、账号、数据资源或书源规则。
- 开发者不经营内容平台，不提供小说存储、发布、传播或聚合服务，也不参与第三方书源、规则社区、群组或网站的运营。
- 用户导入书源后，网络请求由用户设备直接发往目标网站。规则的来源、合法性、准确性、安全性和适用性应由用户自行核实。
- 用户应确保自己有权访问、缓存、转换、朗读或使用相关内容，并遵守目标网站的 robots、访问限制、服务协议和版权要求。
- 第三方书源可能包含 JavaScript、请求外部接口或发生变化；本项目不对第三方内容的可用性、真实性、安全性及由此产生的损失作保证。
- AI 模型、云 TTS、HTTP TTS、WebDAV 等服务由用户自行选择。发送给服务方的数据、生成内容及费用均受相应服务条款约束，使用者应自行确认授权与合规性。
- “阅读 Vox / Legado Vox”是独立维护的二次开发项目，不是 gedoor/legado 或 HapeLee/legado-with-MD3 的官方发行版；上游作者不对本项目新增代码、发布包或服务承担责任。
- GPL-3.0 授权适用于本仓库中受其约束的软件源码，不代表授予任何第三方内容、书源、商标、封面、字体或在线服务的权利。

如权利人认为本仓库直接托管的内容侵犯其合法权益，可通过 GitHub Issues 提交包含权属证明、具体位置和处理请求的通知；第三方网站或规则平台上的内容应优先联系其实际托管方。更完整的协议可在应用“关于 → 免责声明”中查看。本节参考了 [gedoor/legado 的法律公告](https://github.com/gedoor/legado) 与 [legado-with-MD3 的用户协议及免责声明](https://github.com/HapeLee/legado-with-MD3#%EF%B8%8F-%E7%94%A8%E6%88%B7%E5%8D%8F%E8%AE%AE%E4%B8%8E%E5%85%8D%E8%B4%A3%E5%A3%B0%E6%98%8E)，并结合阅读 Vox 的实际网络与 AI 功能重新表述。

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

- [gedoor/legado](https://github.com/gedoor/legado)：核心阅读能力、书源规则体系与长期社区积累。
- [HapeLee/legado-with-MD3](https://github.com/HapeLee/legado-with-MD3)：Material Design 3 界面、Compose 迁移和分支功能基础。
- [Autsunset/VoxEngine](https://github.com/Autsunset/VoxEngine)：MiMo AI TTS 接入思路与实现来源。

同时感谢上游 README 中列出的 [Luoyacheng/legado](https://github.com/Luoyacheng/legado)、[komikku-app/komikku](https://github.com/komikku-app/komikku)、[FoedusProgramme/Gramophone](https://github.com/FoedusProgramme/Gramophone)、[MaterialKolor](https://github.com/jordond/MaterialKolor)、[Reorderable](https://github.com/Calvin-LL/Reorderable)，以及本项目使用的其他开源库和贡献者。

上游代码、设计与资源的著作权归各自作者所有。本项目保留原有许可证和版权信息，并继续遵循 [GPL-3.0](LICENSE) 的源码开放与再分发要求。
