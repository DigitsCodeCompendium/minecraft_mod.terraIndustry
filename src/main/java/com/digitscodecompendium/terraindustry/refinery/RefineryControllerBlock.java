package com.digitscodecompendium.terraindustry.refinery;

import com.digitscodecompendium.terraindustry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RefineryControllerBlock extends BaseEntityBlock {
    private final @Nullable ResourceLocation definitionId;
    public RefineryControllerBlock(Properties properties) {
        this(properties, null);
    }
    public RefineryControllerBlock(Properties properties, @Nullable ResourceLocation definitionId) {
        super(properties.strength(-1.0F, 3_600_000.0F));
        this.definitionId = definitionId;
    }

    public @Nullable ResourceLocation definitionId() {
        return definitionId;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(RefineryControllerBlock::new);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return -1.0F;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RefineryControllerBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof RefineryControllerBlockEntity controller) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inventory, ignored) -> new RefineryControllerMenu(id, inventory, controller),
                    getName()),
                    buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.REFINERY_CONTROLLER.get(),
                (tickLevel, pos, blockState, entity) -> RefineryControllerBlockEntity.serverTick(
                        (ServerLevel) tickLevel, pos, blockState, entity));
    }
}
