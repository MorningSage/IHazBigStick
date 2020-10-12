package morningsage.ihazbigstick;

import morningsage.ihazbigstick.mixin.accessors.TargetFinderAccessor;
import morningsage.ihazbigstick.mixin.accessors.LivingEntityAccessor;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class BigStickGoal extends Goal {
    private final MobEntity mob;
    protected final TargetPredicate targetPredicate;
    protected PlayerEntity target;
    protected Path fleePath;

    public BigStickGoal(MobEntity mob) {
        this.mob = mob;
        // ToDo: Add these to a config file?
        this.targetPredicate = new TargetPredicate()
            .setBaseMaxDistance(6.0F).includeTeammates()
            .includeInvulnerable().ignoreEntityTargetRules()
            .setPredicate((livingEntity) -> EntityPredicates.rides(mob).test(livingEntity));
    }

    @Override
    public boolean canStart() {
        // Get the players around
        this.target = this.mob.world.getClosestPlayer(
            this.targetPredicate, this.mob,
            this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()
        );

        // Can't start if there are no players
        if (this.target == null) return false;

        // Amount of damage the player can deal (does not include critical hits)
        float damage = (float) this.target.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // Damage lessened by our Armor and Enchants
        damage = ((LivingEntityAccessor) this.mob).applyArmor(DamageSource.player(this.target), damage);
        damage = ((LivingEntityAccessor) this.mob).applyEnchantments(DamageSource.player(this.target), damage);

        // Damage lessened by our Absorption
        damage = Math.max(damage - this.mob.getAbsorptionAmount(), 0.0F);

        // If we can take it, don't run like a coward
        if (this.mob.getHealth() > damage) return false;

        // I guess we can't... Find a location to flee to
        Vec3d vec3d = findTarget(this.mob, this.mob.getPos().subtract(this.target.getPos()));

        // If we can't find one, we can't flee at all
        if (vec3d == null) return false;

        // If the new location is even closer to the player, there's no point
        if (this.target.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.target.squaredDistanceTo(this.mob)) return false;

        // Calculate and save the path
        this.fleePath = this.mob.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);

        // Only flee if we have somewhere to go
        return this.fleePath != null;
    }

    @Override
    public void start() {
        // Start the mob moving
        // ToDo: Add to a config file?
        mob.getNavigation().startMovingAlong(this.fleePath, 2.0);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        // ToDo: Theoretically, we could run faster when the player is closer.
        //if (this.mob.squaredDistanceTo(this.target) < 49.0D) {
        //    this.mob.getNavigation().setSpeed(3.0);
        //} else {
        //    this.mob.getNavigation().setSpeed(0.5);
        //}
    }

    @Override
    public boolean canStop() {
        // Keep fleeing until we get to where we were going
        return this.mob.getNavigation().isIdle();
    }

    @Override
    public boolean shouldContinue() {
        return !canStop();
    }

    @Nullable
    private static Vec3d findTarget(MobEntity mob, @Nullable Vec3d preferredAngle) {
        boolean isWithinDistance = false;
        if (mob.hasPositionTarget()) {
            isWithinDistance = mob.getPositionTarget().isWithinDistance(
                mob.getPos(), ((double) mob.getPositionTargetRange()) + 17.0D
            );
        }

        boolean foundTarget = false;
        BlockPos targetPosition = mob.getBlockPos();

        // Try to find a location up to 10 times
        for (int i = 0; i < 10; i++) {
            // Get a random position around the mob
            BlockPos randomOffset = TargetFinderAccessor.getRandomOffset(
                mob.getRandom(), 16, 7,
                0, preferredAngle, Math.PI / 2
            );

            // Try again if that one failed
            if (randomOffset == null) continue;

            // Adjust the offset based on the mob's current target (if any)
            if (mob.hasPositionTarget()) {
                BlockPos currentTarget = mob.getPositionTarget();

                randomOffset = new BlockPos(
                    // Maximum of half the current max horizontal distance
                    randomOffset.getX() + mob.getRandom().nextInt(8) * (mob.getX() > (double) currentTarget.getX() ? -1 : 1),
                    // Don't adjust Y though, so that we stay on the same plane
                    randomOffset.getY(),
                    // Maximum of half the current max horizontal distance
                    randomOffset.getZ() + mob.getRandom().nextInt(8) * (mob.getZ() > (double) currentTarget.getZ() ? -1 : 1)
                );
            }

            // Calculate the actual position the mob would end up
            BlockPos proposedPosition = randomOffset.add(mob.getBlockPos());

            // Not valid if below the world
            if (proposedPosition.getY() < 0) continue;
            // Not valid if above the world
            if (proposedPosition.getY() > mob.world.getHeight()) continue;
            // Not valid if the entity cannot stand there
            if (!mob.getNavigation().isValidPosition(proposedPosition)) continue;

            if (!isWithinDistance || mob.isInWalkTargetRange(proposedPosition)) {
                PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(mob.world, proposedPosition.mutableCopy());
                if (mob.getPathfindingPenalty(pathNodeType) != 0.0F) continue;

                if (getPathfindingFavor(mob, proposedPosition) > Double.NEGATIVE_INFINITY) {
                    targetPosition = proposedPosition;
                    foundTarget = true;
                    break;
                }
            }
        }

        // If we found something, return the middle of the block
        if (foundTarget) return Vec3d.ofBottomCenter(targetPosition);

        // Boooooo
        return null;
    }

    public static float getPathfindingFavor(MobEntity mob, BlockPos pos) {
        if (mob instanceof PathAwareEntity) {
            return ((PathAwareEntity) mob).getPathfindingFavor(pos);
        }

        return 0.0F;
    }
}
