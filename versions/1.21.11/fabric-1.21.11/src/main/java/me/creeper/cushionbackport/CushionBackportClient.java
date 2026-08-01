package me.creeper.cushionbackport;

import me.creeper.cushionbackport.client.CushionModel;
import me.creeper.cushionbackport.client.CushionModelLayers;
import me.creeper.cushionbackport.client.CushionRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CushionBackportClient implements ClientModInitializer {
	public static final String MOD_ID = "cushion-backport";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Cushion-Backport! (client)");
		EntityModelLayerRegistry.registerModelLayer(CushionModelLayers.CUSHION, CushionModel::createBodyLayer);
		EntityRenderers.register(CBRegistry.CUSHION.get(), CushionRenderer::new);
	}
}
