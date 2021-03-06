package net.arcanamod.systems.spell.casts.impl;

import net.arcanamod.ArcanaVariables;
import net.arcanamod.aspects.Aspect;
import net.arcanamod.aspects.Aspects;
import net.arcanamod.blocks.tiles.ResearchTableTileEntity;
import net.arcanamod.systems.spell.casts.Cast;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

public class FabricCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("fabric");
	}

	@Override
	public Aspect getSpellAspect() {
		return Aspects.FABRIC;
	}

	@Override
	public int getSpellDuration() {
		return 0;
	}

	@Override
	public ActionResultType useOnEntity(PlayerEntity caster, Entity target) {
		target.sendMessage(new StringTextComponent("MCP is broken everyone shold use Yarn"));
		target.sendMessage(new StringTextComponent(target.getName().getString()+" gets gold award on r/minecraft"));
		target.sendMessage(new StringTextComponent(target.getName().getString()+" gets gold award on r/minecraft"));
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResultType useOnBlock(PlayerEntity caster, World world, BlockPos blockTarget) {
		TileEntity tileentity = new ResearchTableTileEntity();
		Throwable throwable = new NullPointerException();
		CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking block entity");
		CrashReportCategory crashreportcategory = crashreport.makeCategory("Block entity being ticked");
		tileentity.addInfoToCrashReport(crashreportcategory);
		throw new ReportedException(crashreport);
	}

	@Override
	public ActionResultType useOnPlayer(PlayerEntity playerTarget) {
		playerTarget.sendMessage(new StringTextComponent("MCP is broken everyone shold use Yarn"));
		playerTarget.sendMessage(new StringTextComponent(playerTarget.getName().getString()+" gets gold award on r/minecraft"));
		playerTarget.sendMessage(new StringTextComponent(playerTarget.getName().getString()+" gets gold award on r/minecraft"));
		return ActionResultType.SUCCESS;
	}
}
