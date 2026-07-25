package me.creeper.cushionbackport;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CushionBackport implements ModInitializer {
	public static final String MOD_ID = "cushion-backport";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Cushion-Backport! (shared)");

		CBRegistry.init();
	}
}
