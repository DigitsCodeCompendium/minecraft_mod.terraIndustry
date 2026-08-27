package com.digitscodecompendium.terraindustry.refinery;

import com.digitscodecompendium.terraindustry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class RefineryPortBlock extends BaseEntityBlock {
    private final RefineryPortType portType;
    public RefineryPortBlock(Properties properties, RefineryPortType portType) {
        super(properties.strength(-1.0F, 3_600_000.0F));
        this.portType = portType;
    }
    public RefineryPortType portType() { return portType; }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(properties -> new RefineryPortBlock(properties, RefineryPortType.FUEL)); }
    @Override public float getDestroyProgress(BlockState state, net.minecraft.world.entity.player.Player player, BlockGetter level, BlockPos pos) { return -1.0F; }
    @Override public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new RefineryPortBlockEntity(pos, state); }
    @Override protected InteractionResult useWithoutItem(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof RefineryPortBlockEntity port) {
            serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, ignored) -> new RefineryPortMenu(id, inventory, port, portType), getName()),
                    buffer -> { buffer.writeBlockPos(pos); buffer.writeVarInt(portType.ordinal()); });
        }
        return InteractionResult.SUCCESS;
    }
}
