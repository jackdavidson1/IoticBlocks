@file:JvmName("IoticBlocksAbstractionsImpl")

package gay.`object`.ioticblocks.neoforge

import gay.`object`.ioticblocks.registry.IoticBlocksRegistrar
import net.neoforged.neoforge.registries.*
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

fun <T : Any> initRegistry(registrar: IoticBlocksRegistrar<T>) {
    MOD_BUS.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}
