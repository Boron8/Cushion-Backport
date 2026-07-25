package me.creeper.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class CushionModel extends EntityModel<CushionRenderState> {
    public CushionModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        root.addOrReplaceChild(
                "cushion",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -4.0f, -8.0f, 16.0f, 4.0f, 16.0f, new CubeDeformation(-0.005f)),
                PartPose.offset(0.0f, 4.0f, 0.0f)
        );
        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
