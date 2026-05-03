package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.command.ModCommandRegistry
import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import io.github.takenoko4096.starlight.registry.creativetab.ModCreativeModeTabRegistry
import io.github.takenoko4096.starlight.registry.item.ModItemRegistry
import io.github.takenoko4096.starlight.registry.tag.ModTagRegistry
import io.github.takenoko4096.starlight.registry.translation.ModTranslationRegistry
import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.text.RgbColor
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class StarlightModInitializer(val identifier: String) : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger(identifier)

    val itemRegistry: ModItemRegistry = ModItemRegistry(this)

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: ModTranslationRegistry = ModTranslationRegistry(this)

    val commandRegistry: ModCommandRegistry = ModCommandRegistry(this)

    val tagRegistry: ModTagRegistry = ModTagRegistry(this)

    val creativeModeTabRegistry: ModCreativeModeTabRegistry = ModCreativeModeTabRegistry(this)

    init {
        logger.info("$identifier is powered by starlight v. ${BuildConfig.STARLIGHT_VERSION}")

        ServerLifecycleEvents.SERVER_STARTED.register {
            val data = DataDrivenStarlight(this, it)
            onServerStart(data)
        }

        commandRegistry.register("starlight") {
            "logger" {
                "info" {
                    "message"(greedyString()) {
                        executes {
                            val message = "message"[String::class]
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
                        gradient(RgbColor.BLUE, RgbColor.LIGHT_PURPLE) {
                            text("A versatile library for fabric mod - Starlight")
                        }

                        linebreak()
                        text("starlight version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.STARLIGHT_VERSION)
                        }

                        linebreak()
                        text("minecraft version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.MINECRAFT_VERSION)
                        }

                        linebreak()
                        text("java version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.JAVA_VERSION.toString())
                        }

                        linebreak()
                        text("fabric loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOADER_VERSION)
                        }

                        linebreak()
                        text("fabric api version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_API_VERSION)
                        }

                        linebreak()
                        text("fabric loom version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.FABRIC_LOOM_VERSION)
                        }

                        linebreak()
                        text("kotlin loader version:")
                        space()
                        section(textColor = RgbColor.GREEN) {
                            text(BuildConfig.KOTLIN_LOADER_VERSION)
                        }
                    }
                }
            }
        }
    }

    abstract override fun onInitialize()

    open fun onServerStart(data: DataDrivenStarlight) {}

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is StarlightModInitializer) identifier == other.identifier else false
    }

    fun identifierOf(value: String): Identifier {
        return Identifier.fromNamespaceAndPath(identifier, value)
    }

    fun texturePathOf(value: String): TexturePath {
        return TexturePath(identifierOf(value))
    }
}
