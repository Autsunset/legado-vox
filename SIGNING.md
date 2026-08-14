# Legado Vox 发布签名

Legado Vox 使用独立发布签名，不复用任何上游项目密钥。

## 当前签名身份

- Alias：`legado-vox`
- 算法：RSA 4096 / SHA256withRSA
- SHA-1：`DA:DF:4D:5F:2B:44:C2:DD:99:86:53:84:65:F9:F9:44:72:F4:C9:07`
- SHA-256：`EE:44:C4:F7:2E:EA:3E:74:81:85:AD:24:36:3C:71:D3:58:8B:07:1C:24:43:F2:E5:96:46:32:34:6B:EA:79:B1`

公钥证书保存在 `signing-certificate.pem`。私钥保存在本机被 Git 忽略的 `keystore/legado-vox-release.jks`，密码配置保存在被 Git 忽略的 `signing.properties`。

## 必须备份

请将以下两个文件加密备份到至少两个独立位置：

- `keystore/legado-vox-release.jks`
- `signing.properties`

丢失私钥或密码后，无法为 `io.github.autsunset.legadovox` 发布可以覆盖安装的升级版本。

## GitHub Actions

将 keystore 的 Base64 内容和密码保存为仓库 Secrets：

- `SIGNING_KEY`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

本地生成 Base64 时可使用：

```bash
base64 -w 0 keystore/legado-vox-release.jks
```

由于本地 keystore 使用 PKCS12，`KEY_PASSWORD` 应与 `KEYSTORE_PASSWORD` 相同。
