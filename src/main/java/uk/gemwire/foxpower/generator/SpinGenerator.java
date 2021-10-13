package uk.gemwire.foxpower.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import uk.gemwire.foxpower.helper.Assorted;
import uk.gemwire.foxpower.reg.Foxregistry;
import uk.gemwire.foxpower.spin.SpinType;

public class SpinGenerator extends BlockEntity {

    private Direction face;
    private SpinType type;

    private int spin = 0;
    private int force = 0;
    private int powerPerTick = 0;

    public boolean containsFox = false;

    public SpinGenerator(BlockPos pos, BlockState state) {
        super(Foxregistry.SPIN_TE.get(), pos, state);
        face = state.getValue(Assorted.FACING_ALL);
        type = state.getValue(SpinBlock.SPINTYPE);
    }

    public void setContainsFox(boolean containsFox) {
        this.containsFox = containsFox;
    }

    // A fox has been introduced to the Block for this generator.
    // Inform our neighbors.
    public void newFox() {
        if(containsFox)
            return;

        setContainsFox(true);

        /* I have no idea if this is necessary. We only want one power generating part, right?
        for(Direction dir : Direction.values())
            ((SpinGenerator) this.level.getBlockEntity(getBlockPos().relative(dir))).newFox();
         */
    }
}
