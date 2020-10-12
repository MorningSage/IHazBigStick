package morningsage.ihazbigstick;

import net.fabricmc.api.ModInitializer;
import morningsage.ihazbigstick.events.GoalCheckerCallback;
import morningsage.ihazbigstick.mixin.accessors.MobEntityAccessor;
import net.minecraft.entity.ai.goal.GoalSelector;

import java.util.concurrent.atomic.AtomicBoolean;

public class IHazBigStick implements ModInitializer {
	@Override
	public void onInitialize() {
		/*
		 * This event is not all encompassing.  It's very specific to the targeted
		 * goals that may interfere with fleeing.  Also worth noting that not a lot
		 * of testing was done and there are likely others that should be added to
		 * this list to make the behavior more believable.
		 */
		GoalCheckerCallback.EVENT.register((entity) -> {
			// Get the current list of goals
			GoalSelector goalSelector = ((MobEntityAccessor) entity).getGoalSelector();

			// Thread safe variable since the "loop" below uses parallelism
			AtomicBoolean hazBigStickGoal = new AtomicBoolean(false);

			// Loop through all running goals at the same time
			goalSelector.getRunningGoals().forEach(prioritizedGoal -> {
				if (prioritizedGoal.getGoal() instanceof BigStickGoal) {
					hazBigStickGoal.set(true);
				}
			});

			// True if the mob is currently fleeing, otherwise False
			return hazBigStickGoal.get();
		});
	}
}
