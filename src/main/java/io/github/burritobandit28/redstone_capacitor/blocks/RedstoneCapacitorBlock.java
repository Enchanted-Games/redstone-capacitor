package io.github.burritobandit28.redstone_capacitor.blocks;


import com.mojang.serialization.MapCodec;
import io.github.burritobandit28.redstone_capacitor.RedstoneCapacitor;
import net.minecraft.block.*;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

import java.util.Set;

import static net.minecraft.state.property.Properties.POWER;

public class RedstoneCapacitorBlock extends AbstractRedstoneGateBlock {


    public static final BooleanProperty INSTANT_CHARGE = BooleanProperty.of("instant_charge");

    public static final MapCodec<RedstoneCapacitorBlock> CODEC = createCodec(RedstoneCapacitorBlock::new);


    public RedstoneCapacitorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(INSTANT_CHARGE, false).with(POWER, 0).with(FACING, Direction.NORTH).with(POWERED, false));
    }

    public MapCodec<RedstoneCapacitorBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INSTANT_CHARGE);
        builder.add(POWER);
        builder.add(FACING);
        builder.add(POWERED);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 3;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWER);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.update(world, pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        float f = state.get(INSTANT_CHARGE) ? 0.5F : 0.55F;
        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);

        if (state.get(INSTANT_CHARGE)) {
            world.setBlockState(pos, state.with(INSTANT_CHARGE, false));
        }
        else {
            world.setBlockState(pos, state.with(INSTANT_CHARGE, true));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction.equals(state.get(FACING))) {
            return state.get(POWER);
        }
        else {
            return 0;
        }
    }


    public void update(ServerWorld world, BlockPos pos, BlockState state) {

        Direction facing = state.get(FACING);
        boolean powered = world.isEmittingRedstonePower(pos.offset(facing), facing);
        int power_before = state.get(POWER);
        boolean did_something = false;

        if (powered) {
            if (state.get(INSTANT_CHARGE)) {
                world.setBlockState(pos, state.with(POWER, 15).with(POWERED, true));
            } else {
                if (power_before !=15) {
                    world.setBlockState(pos, state.with(POWER, power_before+1).with(POWERED, true));
                }
            }
            did_something=true;
        }
        else {
            if (power_before != 0) {
                world.setBlockState(pos, state.with(POWER, power_before - 1).with(POWERED, (power_before-1)>0));
                did_something = true;
            }
        }

        if (did_something) world.scheduleBlockTick(pos, this, 3, TickPriority.EXTREMELY_HIGH);
        world.updateNeighbors(pos, this);
    }




}
