package dev.spiritstudios.spectre.mixin.registry;

import dev.spiritstudios.spectre.impl.world.item.CreativeModeTabReloader;
import net.fabricmc.fabric.impl.resource.v1.SetupMarkerResourceReloader;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SetupMarkerResourceReloader.class)
public class SetupMarkerResourceReloaderMixin {

	@Shadow
	@Final
	private ReloadableServerResources dataPackContents;

	@Inject(method = "prepareSharedState", at = @At("RETURN"))
	private void addPendingTags(PreparableReloadListener.SharedState store, CallbackInfo ci) {
		store.set(CreativeModeTabReloader.PENDING_TAGS_KEY, ((ReloadableServerResourcesAccessor) this.dataPackContents).spectre$getPostponedTags());
	}
}
