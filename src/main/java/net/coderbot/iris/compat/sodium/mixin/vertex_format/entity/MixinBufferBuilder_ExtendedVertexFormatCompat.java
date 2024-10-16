package net.coderbot.iris.compat.sodium.mixin.vertex_format.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.jellysquid.mods.sodium.client.model.vertex.VanillaVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.type.VertexType;
import net.coderbot.iris.compat.sodium.impl.vertex_format.entity_xhfp.ExtendedGlyphVertexType;
import net.coderbot.iris.compat.sodium.impl.vertex_format.entity_xhfp.ExtendedQuadVertexType;
import net.coderbot.iris.vertices.IrisVertexFormats;

/**
 * Apply after Sodium's mixins so that we can mix in to the added method. We do this so that we have the option to
 * use the non-extended vertex format in some cases even if shaders are enabled, without assumptions in the sodium
 * compatibility code getting in the way.
 */
@Mixin(value = BufferBuilder.class, priority = 1010)
public class MixinBufferBuilder_ExtendedVertexFormatCompat {
	@Shadow
	private VertexFormat vertexFormat;

	// todo
//	@SuppressWarnings("target")
//	@ModifyVariable(method = "createSink(Lme/jellysquid/mods/sodium/client/model/vertex/type/VertexType;)Lme/jellysquid/mods/sodium/client/model/vertex/VertexSink;",
//		at = @At("HEAD"), remap = false)
//	private VertexType<?> iris$createSink(VertexType<?> type) {
//		if (vertexFormat == IrisVertexFormats.ENTITY) {
//			if (type == VanillaVertexTypes.QUADS) {
//				return ExtendedQuadVertexType.INSTANCE;
//			}
//		} else if (vertexFormat == IrisVertexFormats.TERRAIN) {
//			if (type == VanillaVertexTypes.GLYPHS) {
//				return ExtendedGlyphVertexType.INSTANCE;
//			}
//		}
//
//		return type;
//	}
}
