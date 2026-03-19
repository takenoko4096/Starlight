package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.SoundType

abstract class StarlightModInitializer : ModInitializer {
    abstract val identifier: String

    abstract fun getBlockRegistry(): ModBlockRegistry

    override fun onInitialize() {
        ModBlockRegistry(this).run {
            val whiteLeaves = register("white_leaves") {
                blockProperties {
                    occlusion = true
                    sound = SoundType.STONE
                    strength = 4f
                }

                itemProperties {
                    translationKeyAuto()
                }

                rendering {
                    chunkSectionLayer {
                        cutout()
                    }

                    blockModel {
                        trivialCube()
                    }
                }

                customBehaviour {
                    blockStates {
                        booleanProperty("activated") {
                            defaultValue = false
                        }

                        integerProperty("level") {
                            defaultValue = 0
                            range = 0..9
                        }

                        enumerationProperty<Mode>("mode") {
                            defaultValue = Mode.DISABLED
                            clazz = Mode::class
                        }
                    }

                    events {
                        onFallOn {
                            causeDamage = false
                        }
                    }
                }
            }
        }
    }

    enum class Mode : StringRepresentable {
        ENABLED {
            override fun getSerializedName() =  "enabled"
        },
        DISABLED {
            override fun getSerializedName() = "disabled"
        }
    }
}
