package morningsage.ihazbigstick.mixin;

import morningsage.ihazbigstick.events.GoalCheckerCallback;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackTargetGoal.class)
public abstract class TrackTargetGoalMixin extends Goal {
    /*
     * Don't follow anything while fleeing
     */

    @Shadow @Final protected MobEntity mob;

    @Inject(
        at = @At("RETURN"),
        method = "shouldContinue",
        cancellable = true
    )
    public void shouldContinue(CallbackInfoReturnable<Boolean> callbackInfo) {
        boolean iHazBigStick = GoalCheckerCallback.EVENT.invoker().onGoalChecking(this.mob);
        callbackInfo.setReturnValue(callbackInfo.getReturnValue() && !iHazBigStick);
    }
}
