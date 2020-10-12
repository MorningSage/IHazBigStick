package morningsage.ihazbigstick.mixin.accessors;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Invoker("applyArmorToDamage")
    float applyArmor(DamageSource source, float amount);

    @Invoker("applyEnchantmentsToDamage")
    float applyEnchantments(DamageSource source, float amount);
}
