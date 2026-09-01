package com.digitscodecompendium.terraindustry.effects;

import com.digitscodecompendium.terraindustry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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

/** Emits a configurable, purely visual sonic pulse. */
public class EffectsBlock extends BaseEntityBlock {
    public EffectsBlock(Properties properties) {
        super(properties.strength(-1.0F, 3_600_000.0F));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(EffectsBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return -1.0F;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EffectsBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        if (!serverPlayer.hasPermissions(2)) {
            serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "Only administrators can configure effects blocks."), true);
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof EffectsBlockEntity effects) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inventory, ignored) -> new EffectsBlockMenu(id, inventory, effects), getName()),
                    buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                              BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.EFFECTS_BLOCK.get(),
                (tickLevel, pos, blockState, entity) -> EffectsBlockEntity.serverTick(
                        (ServerLevel) tickLevel, pos, blockState, entity));
    }
}
