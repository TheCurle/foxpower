package uk.gemwire.foxpower.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import uk.gemwire.foxpower.helper.Assorted;
import uk.gemwire.foxpower.spin.SpinType;

import javax.annotation.Nullable;

/**
 * A Block with two Properties - Facing and Type.
 *
 * A Tumble-type SpinBlock is two tall, two across.
 * A Rotate-type SpinBlock is two across, two back.
 * A Drill-type SpinBlock is two across, one back.
 * A Flail-type SpinBlock is two across, two back and two tall.
 *
 * @author Curle
 */
public class SpinBlock extends Block implements EntityBlock {

    public static final EnumProperty<SpinType> SPINTYPE = EnumProperty.create("type", SpinType.class);

    public SpinBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL));
        this.stateDefinition.any().setValue(Assorted.FACING_ALL, Direction.NORTH).setValue(SPINTYPE, SpinType.Rotate);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(Assorted.FACING_ALL).add(SPINTYPE);
    }

    /**
     * Place extra blocks where they are needed, for this type of SpinBlock.
     * This only runs after the {@link #getStateForPlacement} verification step, so we can be sure that everything we do here
     *  won't delete any blocks.
     */
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction[] directions = Direction.orderedByNearest(placer);
        switch(state.getValue(SPINTYPE)) {
            case Rotate:
                // One tall, two deep, two across.
                world.setBlock(pos.relative(directions[0]), state,  3);
                world.setBlock(pos.relative(directions[1]), state, 3);
                world.setBlock(pos.relative(directions[0]).relative(directions[1]), state,  3);
                break;
            case Drill:
                // One tall, two deep, one across.
                world.setBlock(pos.relative(directions[0]), state, 3);
                break;
            case Flail:
                // Two tall, two deep, two across.
                world.setBlock(pos.relative(directions[0]), state,  3);
                world.setBlock(pos.relative(directions[0]).above(), state, 3);
                world.setBlock(pos.relative(directions[1]), state, 3);
                world.setBlock(pos.relative(directions[1]).above(), state, 3);
                world.setBlock(pos.relative(directions[0]).relative(directions[1]), state,  3);
                world.setBlock(pos.relative(directions[0]).relative(directions[1]).above(), state, 3);
                break;
            case Tumble:
                // Two tall, two deep, one across.
                world.setBlock(pos.above(), state, 3);
                world.setBlock(pos.relative(directions[0]), state,  3);;
                world.setBlock(pos.relative(directions[0]).above(), state, 3);
                break;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
        BlockPos blockpos = p_52739_.getClickedPos();
        Level level = p_52739_.getLevel();
        SpinType spin = SpinType.valueOf(p_52739_.getItemInHand().getShareTag().get("type").getAsString());
        BlockState state = this.defaultBlockState().setValue(SPINTYPE, spin);

        // If any of these checks fail, the state becomes null (no block is placed).
        switch(state.getValue(SPINTYPE)) {
            case Rotate:
                // Rotate spinners take 4 blocks but they are flat.
                // Seek outward from the two nearest looking directions
                Direction[] sides = p_52739_.getNearestLookingDirections();
                boolean side = level.getBlockState(blockpos.relative(sides[0])).canBeReplaced(p_52739_);
                boolean back = level.getBlockState(blockpos.relative(sides[1])).canBeReplaced(p_52739_);
                boolean sideBack = level.getBlockState(blockpos.relative(sides[0]).relative(sides[1])).canBeReplaced(p_52739_);
                if (side && back && sideBack)
                    state.setValue(Assorted.FACING_ALL, p_52739_.getHorizontalDirection());
                else
                    state = null;
                break;
            case Drill:
                // Drill spinners take two blocks.
                back = level.getBlockState(blockpos.relative(p_52739_.getNearestLookingDirection())).canBeReplaced(p_52739_);

                if(back)
                    state.setValue(Assorted.FACING_ALL, p_52739_.getHorizontalDirection());
                else
                    state = null;
                break;
            case Tumble:
                // Tumble spinners take 4 blocks. The first has already been checked.
                boolean up = blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52739_);
                boolean forward = level.getBlockState(blockpos.relative(p_52739_.getNearestLookingDirection())).canBeReplaced(p_52739_);
                boolean upForward = level.getBlockState(blockpos.relative(p_52739_.getNearestLookingDirection()).above()).canBeReplaced(p_52739_);
                if (up && forward && upForward)
                    state.setValue(Assorted.FACING_ALL, p_52739_.getHorizontalDirection());
                else
                    state = null;
                break;

            case Flail:
                // Flail spinners take 8 blocks.
                // Two directions, plus up for each.
                sides = p_52739_.getNearestLookingDirections();
                boolean valid = true;
                // iterate two directions. We want to know if any validation point fails, so explicitly set false.
                for(int i = 0; i < 3; i++) {
                    boolean block = level.getBlockState(blockpos.relative(sides[i])).canBeReplaced(p_52739_);
                    boolean blockUp = level.getBlockState(blockpos.relative(sides[i]).above()).canBeReplaced(p_52739_);
                    if(!(block && blockUp))
                        valid = false;
                }

                if(valid)
                    state.setValue(Assorted.FACING_ALL, p_52739_.getHorizontalDirection());
                else
                    state = null;
                break;

            default:
                state = null;
        }

        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpinGenerator(pos, state);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        ((SpinGenerator) level.getBlockEntity(pos)).newFox();
        entity.remove(Entity.RemovalReason.DISCARDED);
    }
}
