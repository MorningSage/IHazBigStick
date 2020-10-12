package morningsage.ihazbigstick.mixin;

import morningsage.ihazbigstick.events.GoalCheckerCallback;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SlimeEntity.class, MagmaCubeEntity.class})
public abstract class SlimeEntityMixin {
    /*
     * Slimes deal damage when they simple touch players.
     *
     * Don't do that because you wouldn't anger something that could kill you.
     */

    @Inject(
        at = @At("RETURN"),
        method = "canAttack",
        cancellable = true
    )
    protected void canAttack(CallbackInfoReturnable<Boolean> callbackInfo) {
        boolean iHazBigStick = GoalCheckerCallback.EVENT.invoker().onGoalChecking((SlimeEntity) (Object) this);
        callbackInfo.setReturnValue(callbackInfo.getReturnValue() && !iHazBigStick);
    }
}
