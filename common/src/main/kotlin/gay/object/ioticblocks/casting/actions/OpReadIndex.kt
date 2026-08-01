package gay.`object`.ioticblocks.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.api.casting.iota.ListIota
import gay.`object`.ioticblocks.IoticBlocks
import gay.`object`.ioticblocks.api.IoticBlocksAPI
import gay.`object`.ioticblocks.utils.getEntityOrBlockPos
import gay.`object`.ioticblocks.utils.mishapBadEntityOrBlock
import kotlin.math.roundToInt

object OpReadIndex : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntityOrBlockPos(env.world, 0, argc)
        val index = args.getDouble(1, argc).roundToInt()

        target.map(env::assertEntityInRange, env::assertPosInRange)

        val datumHolder = target.map(
            IXplatAbstractions.INSTANCE::findDataHolder,
            { IoticBlocksAPI.INSTANCE.findIotaHolder(env.world, it) },
        ) ?: throw mishapBadEntityOrBlock(target, "iota.read")

        // read/deserialize the list manually so we avoid deserializing data we don't need
        val datum = datumHolder.readIota()
            ?: throw mishapBadEntityOrBlock(target, "iota.read")

        if (datum !is ListIota) {
            throw mishapBadEntityOrBlock(target, "iota.read.list")
        }

        val value = try {
            datum.list.get(index)
        } catch (_: IndexOutOfBoundsException) {
            return listOf(NullIota())
        }

        return listOf(value)
    }
}