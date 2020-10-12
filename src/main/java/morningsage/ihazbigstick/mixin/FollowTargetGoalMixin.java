package morningsage.ihazbigstick.mixin;

import morningsage.ihazbigstick.events.GoalCheckerCallback;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowTargetGoal.class)
public abstract class FollowTargetGoalMixin extends TrackTargetGoal {
    /*
     * Don't follow anything while fleeing
     */

    public FollowTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Inject(
        at = @At("RETURN"),
        method = "canStart",
        cancellable = true
    )
    public void canStart(CallbackInfoReturnable<Boolean> callbackInfo) {
        boolean iHazBigStick = GoalCheckerCallback.EVENT.invoker().onGoalChecking(this.mob);
        callbackInfo.setReturnValue(callbackInfo.getReturnValue() && !iHazBigStick);
    }
}
