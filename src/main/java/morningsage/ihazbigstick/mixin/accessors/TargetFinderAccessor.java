package morningsage.ihazbigstick.mixin.accessors;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(TargetFinder.class)
public interface TargetFinderAccessor {
    @Invoker("getRandomOffset")
    @Nullable static BlockPos getRandomOffset(Random random, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference) {
        throw new AssertionError("Untransformed Accessor!");
    }
}
