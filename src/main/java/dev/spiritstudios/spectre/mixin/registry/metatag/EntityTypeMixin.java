package dev.spiritstudios.spectre.mixin.registry.metatag;

import dev.spiritstudios.spectre.api.registry.MetatagHolder;
import dev.spiritstudios.spectre.api.registry.MetatagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

// Mostly for convenience, EntityType.builtInRegistryHolder() is deprecated so you would get a warning if this extension didn't exist.
@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements MetatagHolder<EntityType<?>> {
	@Shadow
	@Final
	private Holder.Reference<EntityType<?>> builtInRegistryHolder;

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> Optional<V> getData(MetatagKey<EntityType<?>, V> metatagKey) {
		return builtInRegistryHolder.getData(metatagKey);
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> boolean hasData(MetatagKey<EntityType<?>, V> key) {
		return builtInRegistryHolder.hasData(key);
	}
}
