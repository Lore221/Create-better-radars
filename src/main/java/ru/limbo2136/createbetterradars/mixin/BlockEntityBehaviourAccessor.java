package ru.limbo2136.createbetterradars.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockEntityBehaviour.class, remap = false)
public interface BlockEntityBehaviourAccessor {
    // Достаём protected/private поле blockEntity из родительского класса BlockEntityBehaviour.
    // Оно нужно, чтобы понять: это обычный радар или наш ПВО-радар.
    @Accessor("blockEntity")
    SmartBlockEntity create_better_radars$getBlockEntity();
}