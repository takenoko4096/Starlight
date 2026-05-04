package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.text.RgbColor

object Starlight : StarlightModInitializer("starlight") {
    override fun onInitialize() {
        commandRegistry.register("starlight") {
            "logger" {
                "info" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"<String>()
                            logger.info(message)
                            context.successful {
                                text("ログに書き込みました: ")
                                textColor(RgbColor.AQUA)
                                text(message)
                            }
                        }
                    }
                }
            }

            "version" {
                executes {
                    context.successful {
                        gradient(RgbColor.YELLOW, RgbColor.GOLD, RgbColor.YELLOW) {
                            text("✨--------------------------------------------✨")
                            linebreak()
                        }

                        gradient(RgbColor.BLUE, RgbColor.LIGHT_PURPLE) {
                            bold()
                            text("A versatile library for fabric mod - Starlight")
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("starlight version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.STARLIGHT_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("minecraft version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.MINECRAFT_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("java version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.JAVA_VERSION.toString())
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOADER_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric api version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_API_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("fabric loom version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOOM_VERSION)
                        }

                        linebreak()
                        section(textColor = RgbColor.DARK_GRAY) { text('-'); space() }
                        text("kotlin loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.KOTLIN_LOADER_VERSION)
                        }

                        gradient(RgbColor.YELLOW, RgbColor.GOLD, RgbColor.YELLOW) {
                            linebreak()
                            text("✨--------------------------------------------✨")
                        }
                    }
                }
            }
        }
    }
}
