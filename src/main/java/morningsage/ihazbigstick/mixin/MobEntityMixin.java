package morningsage.ihazbigstick.mixin;

import morningsage.ihazbigstick.BigStickGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
	/*
	 * Add the flee logic to all mobs.
	 *
	 * ToDo: Add config file for users/pack devs
	 */

	@Shadow @Final protected GoalSelector goalSelector;

	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
	private void addBigStickGoal(EntityType<? extends MobEntity> entityType, World world, CallbackInfo callbackInfo) {
		goalSelector.add(0, new BigStickGoal((MobEntity) (Object) this));
	}
}
