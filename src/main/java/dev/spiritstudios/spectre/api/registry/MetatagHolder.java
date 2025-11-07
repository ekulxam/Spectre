package dev.spiritstudios.spectre.api.registry;

import java.util.Optional;

/**
 * Represents an object that holds Metatag data.
 *
 * @apiNote Objects which hold a reference to their own registry entry should implement this interface.
 * @see net.minecraft.core.Holder
 * @see net.minecraft.world.level.block.Block
 * @see net.minecraft.world.item.Item
 */
public interface MetatagHolder<T> {
	/**
	 * Gets the data associated with a Metatag.
	 *
	 * @apiNote Will always be empty for direct registry entries.
	 */
	default <V> Optional<V> getData(MetatagKey<T, V> metatagKey) {
		return Optional.empty();
	}

	default <V> V getDataOrThrow(MetatagKey<T, V> key) {
		return getData(key).orElseThrow();
	}

	default <V> boolean hasData(MetatagKey<T, V> key) {
		return false;
	}
}
