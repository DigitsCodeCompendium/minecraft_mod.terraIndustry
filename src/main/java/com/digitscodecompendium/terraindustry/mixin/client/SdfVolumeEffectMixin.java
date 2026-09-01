package com.digitscodecompendium.terraindustry.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.chocoboy.cascade.client.SdfVolumeEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Removes Minecraft's walking head-bob from Cascade's world-space SDF volumes. */
@Mixin(SdfVolumeEffect.class)
public class SdfVolumeEffectMixin {
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewMatrix()Lorg/joml/Matrix4f;")
    )
    private Matrix4f terraIndustry$worldAnchoredModelView() {
        Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !minecraft.options.bobView().get()) {
            return modelView;
        }

        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        float walk = -(player.walkDist + (player.walkDist - player.walkDistO) * partialTick);
        float bob = Mth.lerp(partialTick, player.oBob, player.bob);
        float translationX = Mth.sin(walk * (float) Math.PI) * bob * 0.5F;
        float translationY = -Math.abs(Mth.cos(walk * (float) Math.PI) * bob);
        float rotationZ = Mth.sin(walk * (float) Math.PI) * bob * 3.0F;
        float rotationX = Math.abs(Mth.cos(walk * (float) Math.PI - 0.2F) * bob) * 5.0F;

        // GameRenderer applies translation, then Z and X rotations. Remove those in reverse order.
        return modelView
                .rotate(Axis.XP.rotationDegrees(-rotationX))
                .rotate(Axis.ZP.rotationDegrees(-rotationZ))
                .translate(-translationX, -translationY, 0.0F);
    }
}
