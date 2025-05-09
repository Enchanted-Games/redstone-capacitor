package io.github.burritobandit28.redstone_capacitor;

import io.github.burritobandit28.redstone_capacitor.blocks.RedstoneCapacitorBlock;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneCapacitor implements ModInitializer {
	public static final String MOD_ID = "redstone-capacitor";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RedstoneCapacitorBlock CAPACITOR = new RedstoneCapacitorBlock(
			AbstractBlock.Settings.copy(Blocks.REPEATER)
	);

	public static final BlockItem CAPACITOR_BLOCK_ITEM = new BlockItem(CAPACITOR, new Item.Settings());

	@Override
	public void onInitialize() {


		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "capacitor"), CAPACITOR_BLOCK_ITEM);
		Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "capacitor"), CAPACITOR);


		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(itemGroup -> {
			itemGroup.add(CAPACITOR_BLOCK_ITEM);
		});

		LOGGER.info("Hello Fabric world!");
	}
}