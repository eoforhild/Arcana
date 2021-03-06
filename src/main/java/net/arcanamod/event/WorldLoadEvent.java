package net.arcanamod.event;

import net.arcanamod.Arcana;
import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.ItemAspectRegistry;
import net.arcanamod.commands.FillAspectCommand;
import net.arcanamod.commands.NodeCommand;
import net.arcanamod.commands.ResearchCommand;
import net.arcanamod.commands.TaintCommand;
import net.arcanamod.items.ArcanaItems;
import net.arcanamod.network.Connection;
import net.arcanamod.network.PkSyncResearch;
import net.arcanamod.systems.research.ResearchBooks;
import net.arcanamod.systems.research.ResearchLoader;
import net.arcanamod.capabilities.Researcher;
import net.arcanamod.world.WorldInteractions;
import net.arcanamod.world.WorldInteractionsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import static net.arcanamod.ArcanaVariables.arcLoc;

/**
 * Class for handling any events that occur upon world load
 *
 * @author Atlas
 */
@EventBusSubscriber
public class WorldLoadEvent{
	
	@SubscribeEvent
	public static void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event){
		// It's definitely an ServerPlayerEntity.
		Connection.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()), new PkSyncResearch(ResearchBooks.books, ResearchBooks.puzzles));
		Researcher researcher = Researcher.getFrom(event.getPlayer());
		Connection.sendSyncPlayerResearch(researcher, (ServerPlayerEntity)event.getPlayer());
		
		// If the player should get a one-time scribbled notes,
		if(ArcanaConfig.SPAWN_WITH_NOTES.get()){
			ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
			MinecraftServer server = player.world.getServer();
			if(server != null){
				Advancement hasNote = server.getAdvancementManager().getAdvancement(arcLoc("obtained_note"));
				// and they haven't already got them this way,
				if(hasNote != null)
					if(!player.getAdvancements().getProgress(hasNote).isDone()){
						// give them the notes,
						player.addItemStackToInventory(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()));
						// and grant the advancement, so they never get it again.
						player.getAdvancements().getProgress(hasNote).grantCriterion("impossible");
					}
			}
		}
	}
	
	@SubscribeEvent
	public static void serverAboutToStart(FMLServerAboutToStartEvent event){
		IReloadableResourceManager manager = event.getServer().getResourceManager();
		manager.addReloadListener(Arcana.researchManager = new ResearchLoader());
		manager.addReloadListener(Arcana.itemAspectRegistry = new ItemAspectRegistry(event.getServer()));
		manager.addReloadListener(Arcana.worldInteractionsRegistry = new WorldInteractionsRegistry(event.getServer()));
	}
	
	@SubscribeEvent
	public static void serverStarting(FMLServerStartingEvent event){
		ResearchCommand.register(event.getCommandDispatcher());
		FillAspectCommand.register(event.getCommandDispatcher());
		NodeCommand.register(event.getCommandDispatcher());
		TaintCommand.register(event.getCommandDispatcher());
	}
}