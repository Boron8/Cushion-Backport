package me.creeper;

import me.creeper.client.CushionModel;
import me.creeper.client.CushionModelLayers;
import me.creeper.client.CushionRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CushionBackportClient implements ClientModInitializer {
	public static final String MOD_ID = "cushion-backport";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Cushion-Backport! (client)");
		ModelLayerRegistry.registerModelLayer(CushionModelLayers.CUSHION, CushionModel::createBodyLayer);
		EntityRenderers.register(CBRegistry.CUSHION.get(), CushionRenderer::new);
	}
}
