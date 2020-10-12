package morningsage.ihazbigstick.mixin;

import morningsage.ihazbigstick.events.GoalCheckerCallback;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {
    /*
     * Don't attack anything while fleeing
     */

    @Shadow @Final protected PathAwareEntity mob;

    @Inject(
        at = @At("RETURN"),
        method = {"canStart", "shouldContinue"},
        cancellable = true
    )
    public void isValidGoal(CallbackInfoReturnable<Boolean> callbackInfo) {
        boolean iHazBigStick = GoalCheckerCallback.EVENT.invoker().onGoalChecking(this.mob);
        callbackInfo.setReturnValue(callbackInfo.getReturnValue() && !iHazBigStick);
    }
}
