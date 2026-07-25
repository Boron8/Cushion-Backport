package me.creeper.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.creeper.CBRegistry;
import me.creeper.CushionBackport;
import me.creeper.entity.Cushion;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;

import javax.swing.plaf.nimbus.State;
import java.util.HashMap;
import java.util.Map;

public class CushionRenderer extends EntityRenderer<Cushion, CushionRenderState> {
    private static final Map<DyeColor, Identifier> TEXTURES_BY_COLOR = Util.make(new HashMap<>(), textures -> {
        for (DyeColor color : CBRegistry.ALL_COLORS) {
            textures.put(color, Identifier.fromNamespaceAndPath(CushionBackport.MOD_ID, "textures/entity/cushion/" + color.getName() + "_cushion.png"));
        }
    });
    private CushionModel model;

    public CushionRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CushionModel(context.bakeLayer(CushionModelLayers.CUSHION));
    }

    @Override
    public void extractRenderState(Cushion cushion, CushionRenderState state, float partialTicks) {
        super.extractRenderState(cushion, state, partialTicks);
        state.direction = Direction.fromYRot(cushion.getYRot());
        state.texture = texture(cushion.getColor());
    }

    @Override
    public void submit(CushionRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.direction.toYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        poseStack.translate(0.0, -0.25, 0.0);
        submitNodeCollector.submitModel(
                this.model, state, poseStack, this.model.renderType(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null
        );
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public CushionRenderState createRenderState() {
        return new CushionRenderState();
    }

    private static Identifier texture(DyeColor color) {
        return TEXTURES_BY_COLOR.getOrDefault(color, TEXTURES_BY_COLOR.get(DyeColor.WHITE));
    }
}
