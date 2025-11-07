package dev.spiritstudios.spectre.impl;

import dev.spiritstudios.spectre.api.network.ScreenshakeS2CPayload;
import dev.spiritstudios.spectre.api.registry.SpectreMetatags;
import dev.spiritstudios.spectre.api.registry.SpectreRegistries;
import dev.spiritstudios.spectre.impl.command.ComponentsCommand;
import dev.spiritstudios.spectre.impl.command.MetatagCommand;
import dev.spiritstudios.spectre.impl.command.ScreenshakeCommand;
import dev.spiritstudios.spectre.impl.world.item.CreativeModeTabReloader;
import dev.spiritstudios.spectre.impl.world.item.CreativeModeTabsS2CPayload;
import dev.spiritstudios.spectre.impl.registry.MetatagReloader;
import dev.spiritstudios.spectre.impl.registry.MetatagSyncS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Spectre implements ModInitializer {
	public static final String MODID = "spectre";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	private static final int MAX_METATAG_PACKET_SIZE = Integer.getInteger("spectre.metatag.max_packet_size", 32 * 1024 * 1024);

	@Override
	public void onInitialize() {
		// While it might look useless, this forces the JVM to load the class we reference
		// This lets us ensure that everything registered with a static field is always registered at game startup
		Object forceLoad0 = SpectreRegistries.METATAG;
		Object forceLoad1 = SpectreMetatags.COMPOSTING_CHANCE;

		MetatagReloader.register();
		CreativeModeTabReloader.register();

		PayloadTypeRegistry.playS2C().register(
			ScreenshakeS2CPayload.TYPE,
			ScreenshakeS2CPayload.CODEC
		);

		PayloadTypeRegistry.playS2C().registerLarge(
			MetatagSyncS2CPayload.TYPE,
			MetatagSyncS2CPayload.CODEC,
			MAX_METATAG_PACKET_SIZE
		);

		PayloadTypeRegistry.playS2C().register(
			CreativeModeTabsS2CPayload.TYPE,
			CreativeModeTabsS2CPayload.CODEC
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, context, registrationEnvironment) -> {
			ScreenshakeCommand.register(dispatcher);
			MetatagCommand.register(dispatcher, context);
			ComponentsCommand.register(dispatcher);
		});
	}
}
