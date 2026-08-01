package gay.`object`.ioticblocks.casting.actions

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
//import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import gay.`object`.ioticblocks.api.IoticBlocksAPI
import gay.`object`.ioticblocks.utils.getEntityOrBlockPos
import gay.`object`.ioticblocks.utils.mishapBadEntityOrBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.phys.Vec3

object OpWriteIndex : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        // first, read the list
        val target = args.getEntityOrBlockPos(env.world, 0, argc)

        target.map(env::assertEntityInRange, env::assertPosInRangeForEditing)

        val datumHolder = target.map(
            IXplatAbstractions.INSTANCE::findDataHolder,
            { IoticBlocksAPI.INSTANCE.findIotaHolder(env.world, it) },
        ) ?: throw mishapBadEntityOrBlock(target, "iota.read")

        val list = (datumHolder.readIota() as? ListIota)?.list
            ?: throw mishapBadEntityOrBlock(target, "iota.read.list")

        // then, insert the iota at the specified index like surgeon's exaltation
        val index = args.getPositiveIntUnder(1, list.size, argc)
        val iota = args[2]

        val newListIota = ListIota(list.updated(index, iota))

        // then, make sure the new list is safe to write
        if (!datumHolder.writeIota(newListIota, true)) {
            throw mishapBadEntityOrBlock(target, "iota.write")
        }

        val trueNameMishap = MishapOthersName.getTrueNameMishapFromDatum(env.world,newListIota,null)
        if (null != trueNameMishap) {
            throw trueNameMishap
        }

        // finally, write the new list back to the target
        val burstPos = target.map(
            {
                when (it) {
                    // Special case these because the render is way above the entity
                    is ItemEntity -> it.position().add(0.0, 3.0 / 8.0, 0.0)
                    else -> it.position()
                }
            },
            BlockPos::getCenter,
        )

        return SpellAction.Result(
            Spell(newListIota, datumHolder),
            0,
            listOf(ParticleSpray(burstPos, Vec3(1.0, 0.0, 0.0), 0.25, 3.14, 40)),
        )
    }

    private data class Spell(val datum: Iota, val datumHolder: ADIotaHolder) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            datumHolder.writeIota(datum, false)
        }
    }
}