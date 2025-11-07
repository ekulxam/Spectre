package dev.spiritstudios.spectre.mixin.registry.metatag;

import dev.spiritstudios.spectre.api.registry.MetatagKey;
import dev.spiritstudios.spectre.api.registry.MetatagHolder;
import dev.spiritstudios.spectre.impl.registry.MutableMetatagHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;

@Mixin(Holder.Reference.class)
public abstract class Holder$ReferenceMixin<T> implements MetatagHolder<T>, MutableMetatagHolder<T> {
	@Unique
	private @Nullable Map<MetatagKey<T, ?>, Object> metatags;

	@SuppressWarnings({"unchecked", "AddedMixinMembersNamePattern"})
	@Override
	public <V> Optional<V> getData(MetatagKey<T, V> metatagKey) {
		return metatags == null ?
			Optional.empty() :
			Optional.ofNullable((V) metatags.get(metatagKey));
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> boolean hasData(MetatagKey<T, V> key) {
		return metatags != null && metatags.containsKey(key);
	}

	@Override
	public void spectre$clearMetatags() {
		// Don't bother clearing out the previous map, it will get GCed at some point
		metatags = null;
	}

	@Override
	public <V> void spectre$putMetatag(MetatagKey<T, V> metatag, V value) {
		if (metatags == null) metatags = new Object2ObjectOpenHashMap<>();

		metatags.put(metatag, value);
	}
}
