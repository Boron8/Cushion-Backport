package me.creeper.client;

import me.creeper.CushionBackport;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class CushionRenderState extends EntityRenderState {
    public Direction direction = Direction.NORTH;
    public Identifier texture = Identifier.fromNamespaceAndPath(CushionBackport.MOD_ID, "textures/entity/cushion/white_cushion.png");
}
