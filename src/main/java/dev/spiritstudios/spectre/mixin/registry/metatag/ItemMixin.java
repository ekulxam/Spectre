package dev.spiritstudios.spectre.mixin.registry.metatag;

import dev.spiritstudios.spectre.api.registry.MetatagHolder;
import dev.spiritstudios.spectre.api.registry.MetatagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

// Mostly for convenience, Item.builtInRegistryHolder() is deprecated so you would get a warning if this extension didn't exist.
@Mixin(Item.class)
public abstract class ItemMixin implements MetatagHolder<Item> {
	@Shadow
	@Final
	private Holder.Reference<Item> builtInRegistryHolder;

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> Optional<V> getData(MetatagKey<Item, V> metatagKey) {
		return builtInRegistryHolder.getData(metatagKey);
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> boolean hasData(MetatagKey<Item, V> key) {
		return builtInRegistryHolder.hasData(key);
	}
}
