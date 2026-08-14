# Legado Vox

**Legado Vox** 是一个独立维护的 Android 阅读与应用内 AI 听书项目。

项目以 [Legado](https://github.com/gedoor/legado) 及其 Material Design 3 社区实现为基础进行二次开发，并集成了来自 [VoxEngine](https://github.com/Autsunset/VoxEngine) 的 MiMo AI TTS 调用能力。当前代码仓库、包名、版本线、发布签名和发行渠道均独立于上游项目。

## 项目标识

- 应用名称：**Legado Vox / 阅读 Vox**
- Release 包名：`io.github.autsunset.legadovox`
- Debug 包名：`io.github.autsunset.legadovox.debug`
- 源代码仓库：`https://github.com/Autsunset/legado-vox`
- 独立版本线：从 `1.0.0` 开始

> 源代码内部仍保留 `io.legado.app` namespace，以降低大规模重命名带来的兼容性和回归风险；Android 安装身份由新的 `applicationId` 决定。

## 应用内 AI TTS 听书

MiMo AI TTS 已接入 Legado 自己的朗读链路：

- 使用阅读进度、章节切换和 Legado MD3 听书播放器
- 支持音频缓存、后续章节预合成和多角色音色路由
- 支持 MiMo 节点、API Key、温度、重试、请求间隔和 User-Agent 设置
- 内置冰糖、茉莉、苏打、白桦、Mia、Chloe、Milo 和 Dean 音色
- 支持无变调语速调整

本功能仅在应用内部生成并播放语音，**不会注册 Android `TextToSpeechService`，不会安装或切换系统默认 TTS 引擎**。

配置入口：阅读页 → 朗读设置 → 音色 → 朗读引擎与音色。

## 构建

需要 JDK 21 和 Android SDK：

```bash
./gradlew verifyConfigArchitecture testAppDebugUnitTest assembleAppDebug
```

构建正式版本：

```bash
./gradlew assembleAppRelease
```

## 发布签名

正式签名密钥不进入 Git。项目从根目录的 `signing.properties`、Gradle 属性或环境变量读取：

```properties
RELEASE_STORE_FILE=keystore/legado-vox-release.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=legado-vox
RELEASE_KEY_PASSWORD=your_key_password
```

`signing.properties` 和 `keystore/` 均被 `.gitignore` 排除。发布密钥应另行加密备份；丢失密钥后无法为同一包名发布可覆盖安装的升级包。

## 隐私

本项目不绑定上游 Firebase 项目，也不内置 Firebase Analytics/Performance 配置。用户主动配置 AI TTS、在线朗读、同步或其他第三方网络服务时，完成请求所需的文本、凭据或数据会由设备发送至用户选择的服务提供方，具体处理规则以对应服务条款和隐私政策为准。

## 上游与许可证

Legado Vox 是新的独立二开仓库，但这不改变上游代码的著作权归属和许可证义务。感谢：

- [gedoor/legado](https://github.com/gedoor/legado)
- [HapeLee/legado-with-MD3](https://github.com/HapeLee/legado-with-MD3)
- [Autsunset/VoxEngine](https://github.com/Autsunset/VoxEngine)
- 以及项目依赖的其他开源库和贡献者

项目继续遵循仓库中的 [GPL-3.0 License](LICENSE)。

已提交的 `signing-certificate.pem` 和 `signing-certificate.sha256` 仅包含公钥证书，可用于核对官方 APK 的签名身份，不包含私钥。
