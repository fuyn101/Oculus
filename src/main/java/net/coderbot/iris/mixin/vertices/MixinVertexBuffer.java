package net.coderbot.iris.mixin.vertices;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.coderbot.iris.vertices.IrisVertexFormats;

@Mixin(VertexBuffer.class)
public class MixinVertexBuffer {
	@Shadow
	@Final
	@Mutable
	private VertexFormat vertexFormat;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void iris$onInit(VertexFormat format, CallbackInfo ci) {
		if (BlockRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
			// We have to fix the vertex format here, or else the vertex count will be calculated wrongly and too many
			// vertices will be drawn.
			//
			// Needless to say, that is not good if you don't like access violation crashes!
			if (format == DefaultVertexFormats.BLOCK) {
				this.vertexFormat = IrisVertexFormats.TERRAIN;
//			} else if (format == DefaultVertexFormats.NEW_ENTITY || format == DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP) {
			} else if (format == DefaultVertexFormats.POSITION_TEX_LMAP_COLOR) {
				this.vertexFormat = IrisVertexFormats.ENTITY;
			}
		}
	}
}
