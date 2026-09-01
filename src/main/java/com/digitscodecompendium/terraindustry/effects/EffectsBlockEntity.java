package com.digitscodecompendium.terraindustry.effects;

import com.digitscodecompendium.terraindustry.ModBlockEntities;
import com.digitscodecompendium.terraindustry.ModSounds;
import dev.chocoboy.cascade.Vfx;
import dev.chocoboy.cascade.engine.effect.SdfSpec;
import dev.chocoboy.cascade.net.SdfPayload;
import dev.chocoboy.cascade.engine.tween.Easings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/** Stores the pulse settings and emits the expanding particle ring on the server. */
public class EffectsBlockEntity extends BlockEntity {
    private static final int MIN_RADIUS_TENTHS = 1;
    private static final int MAX_RADIUS_TENTHS = 640;
    private static final int MIN_FADE_EXPONENT_HUNDREDTHS = 1;
    private static final int MAX_FADE_EXPONENT_HUNDREDTHS = 800;
    private static final int MIN_PULSE_TICKS = 5;
    private static final int MAX_PULSE_TICKS = 1_200;
    private static final int MIN_SEGMENT_TENTHS = 1;
    private static final int MAX_SEGMENT_TENTHS = 100;
    private static final int MIN_SPACING_TENTHS = 1;
    private static final int MAX_SPACING_TENTHS = 320;
    private static final int SHOCKWAVE_FRAME_TICKS = 4;
    private static final double CASCADE_RENDER_DISTANCE = 96.0D;

    private int finalRadiusTenths = 120;
    private int fadeExponentHundredths = 150;
    private int pulseDurationTicks = 40;
    private int pulseColor = 0x35D4FF;
    private int segmentSizeTenths = 5;
    private int segmentSpacingTenths = 2;
    private int pulseAge;

    public EffectsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFECTS_BLOCK.get(), pos, state);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, EffectsBlockEntity effects) {
        effects.tickPulse(level);
    }

    private void tickPulse(ServerLevel level) {
        emitCascadePulse(level, pulseAge);
        pulseAge = (pulseAge + 1) % pulseDurationTicks;
    }

    private void emitCascadePulse(ServerLevel level, int age) {
        float startRadius = 0.15F;
        float travelSpeed = (finalRadius() - startRadius) / Math.max(1, pulseDurationTicks - 1);
        float radius = startRadius + travelSpeed * age;
        float previousRadius = age == 0 ? startRadius : radius - travelSpeed;
        int segmentCount = Math.max(1, (int) Math.ceil((radius - previousRadius) / segmentSpacing()));

        for (int segment = 1; segment <= segmentCount; segment++) {
            float segmentRadius = age == 0
                    ? startRadius
                    : previousRadius + (radius - previousRadius) * segment / segmentCount;
            emitShockwaveSegment(level, startRadius, segmentRadius);
        }

        if (age == 0) {
            level.playSound(null, worldPosition, ModSounds.EFFECTS_BLOCK_PULSE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            Vfx.light(level, worldPosition.getCenter(), pulseColor, Math.min(finalRadius(), 5.0F), 6);
        }
    }

    private void emitShockwaveSegment(ServerLevel level, float startRadius, float radius) {
        float progress = (radius - startRadius) / Math.max(0.001F, finalRadius() - startRadius);
        float alpha = Math.max(0.04F, (float) Math.pow(1.0F - progress, fadeExponent()));
        int brightColor = brighten(pulseColor, 0.65F);
        int darkColor = darken(pulseColor, 0.22F);

        // A succession of short-lived SDF torus volumes creates one smooth, horizontal pressure wall.
        // Unlike billboard particles, this is genuine world geometry: it is circular from above and has
        // visible thickness when viewed from the side.
        SdfSpec shockwave = Vfx.sdf()
                .torus(0.0F, 0.0F, 0.0F, radius, segmentSize() / 2.0F)
                .smoothness(0.0F)
                .gradient(Easings.LINEAR, 0xFFFFFF, brightColor, pulseColor, darkColor)
                .alpha(alpha)
                .duration(SHOCKWAVE_FRAME_TICKS)
                .spec();
        var effectOrigin = worldPosition.getCenter().add(0.0D, 0.5D, 0.0D);

        // Cascade's public builders send only 64 blocks away, while its client renderer supports 96.
        // Send the library's own payload over that full supported range so the effect does not disappear
        // immediately beyond the normal 64-block packet radius.
        PacketDistributor.sendToPlayersNear(level, null, effectOrigin.x, effectOrigin.y, effectOrigin.z,
                CASCADE_RENDER_DISTANCE, new SdfPayload(effectOrigin, shockwave, level.getGameTime()));
    }

    public boolean canConfigure(ServerPlayer player) {
        return player.hasPermissions(2) && player.distanceToSqr(worldPosition.getCenter()) <= 64.0D;
    }

    public void setConfiguration(int radiusTenths, int fadeExponentHundredths, int pulseDurationTicks, int pulseColor,
            int segmentSizeTenths, int segmentSpacingTenths) {
        this.finalRadiusTenths = Math.clamp(radiusTenths, MIN_RADIUS_TENTHS, MAX_RADIUS_TENTHS);
        this.fadeExponentHundredths = Math.clamp(
                fadeExponentHundredths, MIN_FADE_EXPONENT_HUNDREDTHS, MAX_FADE_EXPONENT_HUNDREDTHS);
        this.pulseDurationTicks = Math.clamp(pulseDurationTicks, MIN_PULSE_TICKS, MAX_PULSE_TICKS);
        this.pulseColor = pulseColor & 0xFFFFFF;
        this.segmentSizeTenths = Math.clamp(segmentSizeTenths, MIN_SEGMENT_TENTHS, MAX_SEGMENT_TENTHS);
        this.segmentSpacingTenths = Math.clamp(segmentSpacingTenths, MIN_SPACING_TENTHS, MAX_SPACING_TENTHS);
        this.pulseAge = 0;
        setChanged();
    }

    public int finalRadiusTenths() {
        return finalRadiusTenths;
    }

    public int fadeExponentHundredths() {
        return fadeExponentHundredths;
    }

    public int pulseDurationTicks() {
        return pulseDurationTicks;
    }

    public int pulseColor() {
        return pulseColor;
    }

    public int segmentSizeTenths() {
        return segmentSizeTenths;
    }

    public int segmentSpacingTenths() {
        return segmentSpacingTenths;
    }

    private float finalRadius() {
        return finalRadiusTenths / 10.0F;
    }

    private float fadeExponent() {
        return fadeExponentHundredths / 100.0F;
    }

    private float segmentSize() {
        return segmentSizeTenths / 10.0F;
    }

    private float segmentSpacing() {
        return segmentSpacingTenths / 10.0F;
    }

    private static int brighten(int color, float amount) {
        return blend(color, 0xFFFFFF, amount);
    }

    private static int darken(int color, float amount) {
        return blend(color, 0x000000, 1.0F - amount);
    }

    private static int blend(int from, int to, float amount) {
        int red = Math.round(((from >> 16) & 0xFF) * (1.0F - amount) + ((to >> 16) & 0xFF) * amount);
        int green = Math.round(((from >> 8) & 0xFF) * (1.0F - amount) + ((to >> 8) & 0xFF) * amount);
        int blue = Math.round((from & 0xFF) * (1.0F - amount) + (to & 0xFF) * amount);
        return red << 16 | green << 8 | blue;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("FinalRadiusTenths", finalRadiusTenths);
        tag.putInt("FadeExponentHundredths", fadeExponentHundredths);
        tag.putInt("PulseDurationTicks", pulseDurationTicks);
        tag.putInt("PulseColor", pulseColor);
        tag.putInt("SegmentSizeTenths", segmentSizeTenths);
        tag.putInt("SegmentSpacingTenths", segmentSpacingTenths);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        setConfiguration(
                tag.contains("FinalRadiusTenths") ? tag.getInt("FinalRadiusTenths") : finalRadiusTenths,
                tag.contains("FadeExponentHundredths") ? tag.getInt("FadeExponentHundredths") : fadeExponentHundredths,
                tag.contains("PulseDurationTicks") ? tag.getInt("PulseDurationTicks") : pulseDurationTicks,
                tag.contains("PulseColor") ? tag.getInt("PulseColor") : pulseColor,
                tag.contains("SegmentSizeTenths") ? tag.getInt("SegmentSizeTenths") : segmentSizeTenths,
                tag.contains("SegmentSpacingTenths") ? tag.getInt("SegmentSpacingTenths") : segmentSpacingTenths);
    }
}
