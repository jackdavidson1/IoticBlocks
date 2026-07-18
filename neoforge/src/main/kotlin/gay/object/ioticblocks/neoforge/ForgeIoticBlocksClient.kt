package gay.`object`.ioticblocks.neoforge

import gay.`object`.ioticblocks.IoticBlocksClient
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

object ForgeIoticBlocksClient {
    fun init(event: FMLClientSetupEvent) {
        IoticBlocksClient.init()
    }
}
