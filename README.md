# Noctiluca

コンテンツ追加系はクライアント用コードもアセット生成コードも全部一箇所にまとめて書きたい！！！！！！

<br>っていう人(我)のためのFabric Mod 用の API

## Usage

### Features

- 新規アイテムのDSLによる完全 `main` ソースセット内定義
- 新規ブロックのDSLによる完全 `main` ソースセット内定義
- 新規コマンドのDSLによる定義
- 主にコンテンツ追加のケースにおけるクライアントコードの省略
- アイテムスタックビルダー
- テキストコンポーネントビルダー
- 新規クリエイティブタブのDSLによる定義
- NBT編集／コンポーネントへのシリアライザー
- 汎用イベントディスパッチャー
- `runDatagen` 実行によるアイテムモデル／ブロックモデル／翻訳ファイル／タグの自動定義
- その他ユーティリティ等

### Setup

> [!WARNING]
> - `fabric.mod.json` に `TestMod`, `TestClientMod`, `TestModDataGenerator` の3つすべてについて記述すること
> - Noctiluca は MOD であり、 `mods` フォルダに配置する必要があります

```kotlin
object TestMod : NoctilucaModInitializer("testmod") {
    override fun onInitialize() {}
}

object TestClientMod : NoctilucaClientModInitializer(TestMod)

object TestModDataGenerator : NoctilucaDataGenerator(TestMod)
```

### Example

#### ブロック：プリズマリンランプを登録する例

> [!WARNING]
> - assets/MOD_ID/textures/block/の位置に対応する画像ファイルを配置すること
> (この場合, `block/prismarine_lamp.png`, `block/prismarine_lamp_on.png` が必要)

```kotlin
object TestMod : NoctilucaModInitializer("testmod") {
    override fun onInitialize() {
        val prismarineLamp = blockRegistry.register("prismarine_lamp") {
            val lit = "lit"

            val info = customBehaviour {
                val properties = blockStates {
                    booleanProperty(lit) {
                        defaultValue = false
                    }
                }

                val litProperty = properties.boolean(lit)

                events {
                    onInteract {
                        val value = blockState.getValue(litProperty)
                        level.setBlockAndUpdate(blockPos, blockState.setValue(litProperty, !value))
                    }
                }
            }

            val litProperty = info.properties.boolean(lit)

            blockProperties {
                destroyTime = 0.5f
                sound = SoundType.METAL
                requiresCorrectToolForDrops = true
                lightLevel {
                    if (it.getValue(litProperty)) 15 else 0
                }
            }

            withItem()

            translation {
                jaJp = "プリズマリンランプ"
                enUs = "Prismarine Lamp"
            }

            rendering {
                models {
                    val off = blockModels.cubeAll(blockDefaultTexturePath)
                    val on = blockModels.cubeAll(blockDefaultTexturePath underscore "on") {
                        suffix = "on"
                    }

                    block {
                        variants(litProperty) {
                            case(false, off.toVariant())
                            case(true, on.toVariant())
                        }
                    }

                    item {
                        handling {
                            use(off)
                        }
                    }
                }
            }
        }
    }
}
```

うまくいくと:
<img src="document_assets/output.png">

#### 実装予定

- `BlockEntity` 追加
- モデル生成API拡張
- エンティティ追加

など

### Example Mod

[**TestMod**](https://github.com/takenoko4096/TestMod)
