package net.arcanamod.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.arcanamod.blocks.ArcanaBlocks;
import net.arcanamod.util.annotations.GenLootTable;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.arcanamod.blocks.ArcanaBlocks.*;

public class LootTables extends LootTableProvider{
	
	private DataGenerator generator;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
	
	public LootTables(DataGenerator dataGenerator){
		super(dataGenerator);
		generator = dataGenerator;
	}

	// TODO: remove all addStandardTable() from act() and use addStandardTableFromDataGenerables()
	
	public void act(@Nonnull DirectoryCache cache){
		try {
			addStandardTableFromDataGenerables();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		addStandardTable(TAINTED_GRAVEL);
		addStandardTable(TAINTED_PUMPKIN);
		addStandardTable(TAINTED_CARVED_PUMPKIN);
		addStandardTable(TAINTED_JACK_OLANTERN);
		addStandardTable(TAINTED_SAND);
		addStandardTable(TAINTED_SOIL);
		addStandardTable(TAINTED_ROCK);
		addStandardTable(TAINTED_ROCK_SLAB);
		addStandardTable(TAINTED_CRUST);
		addStandardTable(TAINTED_CRUST_SLAB);

		addStandardTable(TAINTED_FLOWER);

		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for(Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet())
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
		writeTables(cache, tables);
	}
	
	protected void addStandardTable(@Nonnull Supplier<? extends Block> block){
		addStandardTable(block.get());
	}
	
	protected void addStandardTable(Block block){
		lootTables.put(block, createStandardTable(block));
	}
	
	protected LootTable.Builder createStandardTable(Block block){
		LootPool.Builder builder = LootPool.builder()
				.rolls(ConstantRange.of(1))
				.addEntry(ItemLootEntry.builder(block))
				.acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(builder);
	}
	
	private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables){
		Path outputFolder = generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try{
				IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
			}catch(IOException e){
				LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}
	
	@Nonnull
	public String getName(){
		return "Arcana Loot Tables";
	}

	@SuppressWarnings("unchecked")
	protected void addStandardTableFromDataGenerables() throws IllegalAccessException {
		Class<ArcanaBlocks> clazz = ArcanaBlocks.class;
		Field[] fields = clazz.getFields();
		for (Field field : fields){
			// if field has DataGenerating annotation and l "@LootTable" value is true
			if (field.isAnnotationPresent(GenLootTable.class)){
					LOGGER.debug("Found field in ArcanaBlocks.class: name:" + field.getName() + " type:" + field.getType());
					if (field.get(field.getType()) instanceof RegistryObject) {
						// get RegistryObject from field and add standard table
						RegistryObject<Block> reg = (RegistryObject<Block>) field.get(field.getType());
						LOGGER.debug("RegistryObject: " + reg.get().toString());
						addStandardTable(reg);
					}
				}
		}
	}
}