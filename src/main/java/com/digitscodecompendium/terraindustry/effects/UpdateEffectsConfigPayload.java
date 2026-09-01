package com.digitscodecompendium.terraindustry.effects;

import com.digitscodecompendium.terraindustry.TerraIndustry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server-validated settings submitted by an effects-block configuration screen. */
public record UpdateEffectsConfigPayload(
        BlockPos pos,
        int finalRadiusTenths,
        int fadeExponentHundredths,
        int pulseDurationTicks,
        int pulseColor,
        int segmentSizeTenths,
        int segmentSpacingTenths
) implements CustomPacketPayload {
    public static final Type<UpdateEffectsConfigPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraIndustry.MODID, "update_effects_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateEffectsConfigPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, UpdateEffectsConfigPayload::pos,
                    ByteBufCodecs.VAR_INT, UpdateEffectsConfigPayload::finalRadiusTenths,
                    ByteBufCodecs.VAR_INT, UpdateEffectsConfigPayload::fadeExponentHundredths,
                    ByteBufCodecs.VAR_INT, UpdateEffectsConfigPayload::pulseDurationTicks,
                    ByteBufCodecs.INT, UpdateEffectsConfigPayload::pulseColor,
                    SegmentSettings.STREAM_CODEC, UpdateEffectsConfigPayload::segments,
                    (pos, radius, fade, duration, color, segments) -> new UpdateEffectsConfigPayload(
                            pos, radius, fade, duration, color, segments.sizeTenths(), segments.spacingTenths()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private SegmentSettings segments() {
        return new SegmentSettings(segmentSizeTenths, segmentSpacingTenths);
    }

    public static void handle(UpdateEffectsConfigPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !player.hasPermissions(2)) {
            return;
        }
        if (player.level().getBlockEntity(payload.pos) instanceof EffectsBlockEntity effects
                && effects.canConfigure(player)) {
            effects.setConfiguration(payload.finalRadiusTenths, payload.fadeExponentHundredths,
                    payload.pulseDurationTicks, payload.pulseColor, payload.segmentSizeTenths,
                    payload.segmentSpacingTenths);
        }
    }

    private record SegmentSettings(int sizeTenths, int spacingTenths) {
        private static final StreamCodec<RegistryFriendlyByteBuf, SegmentSettings> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, SegmentSettings::sizeTenths,
                        ByteBufCodecs.VAR_INT, SegmentSettings::spacingTenths,
                        SegmentSettings::new);
    }
}
