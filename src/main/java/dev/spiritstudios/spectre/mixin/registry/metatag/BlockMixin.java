package dev.spiritstudios.spectre.mixin.registry.metatag;

import dev.spiritstudios.spectre.api.registry.MetatagHolder;
import dev.spiritstudios.spectre.api.registry.MetatagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

// Mostly for convenience, Block.builtInRegistryHolder() is deprecated so you would get a warning if this extension didn't exist.
@Mixin(Block.class)
public abstract class BlockMixin implements MetatagHolder<Block> {

	@Shadow
	@Final
	private Holder.Reference<Block> builtInRegistryHolder;

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> Optional<V> getData(MetatagKey<Block, V> metatagKey) {
		return builtInRegistryHolder.getData(metatagKey);
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public <V> boolean hasData(MetatagKey<Block, V> key) {
		return builtInRegistryHolder.hasData(key);
	}
}
