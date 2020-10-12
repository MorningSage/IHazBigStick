package morningsage.ihazbigstick.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.mob.MobEntity;

@FunctionalInterface
public interface GoalCheckerCallback {
    Event<GoalCheckerCallback> EVENT = EventFactory.createArrayBacked(GoalCheckerCallback.class,
        (listeners) -> (entity) -> {
            boolean result = false;

            for (GoalCheckerCallback event : listeners) {
                if (result = event.onGoalChecking(entity)) break;
            }

            return result;
        }
    );

    boolean onGoalChecking(MobEntity entity);
}
