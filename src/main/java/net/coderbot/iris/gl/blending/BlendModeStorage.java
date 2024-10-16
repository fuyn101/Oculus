package net.coderbot.iris.gl.blending;

//import com.mojang.blaze3d.platform.GlStateManager;

import net.coderbot.iris.gl.IrisRenderSystem;
import net.coderbot.iris.mixin.GlStateManagerAccessor;
import net.coderbot.iris.mixin.statelisteners.BooleanStateAccessor;
import net.minecraft.client.renderer.GlStateManager;

public class BlendModeStorage {
	private static boolean originalBlendEnable;
	private static BlendMode originalBlend;
	private static boolean blendLocked;

	public static boolean isBlendLocked() {
		return blendLocked;
	}

	public static void overrideBlend(BlendMode override) {
		if (!blendLocked) {
			// Only save the previous state if the blend mode wasn't already locked
			GlStateManager.BlendState blendState = GlStateManagerAccessor.getBLEND();

			// todo
//			originalBlendEnable = ((BooleanStateAccessor) blendState.mode).isEnabled();
			originalBlend = new BlendMode(blendState.srcFactor, blendState.dstFactor, blendState.srcFactorAlpha, blendState.dstFactorAlpha);
		}

		blendLocked = false;

		if (override == null) {
			GlStateManager.disableBlend();
		} else {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(override.getSrcRgb(), override.getDstRgb(), override.getSrcAlpha(), override.getDstAlpha());
		}

		blendLocked = true;
	}

	public static void overrideBufferBlend(int index, BlendMode override) {
		if (!blendLocked) {
			// Only save the previous state if the blend mode wasn't already locked
			GlStateManager.BlendState blendState = GlStateManagerAccessor.getBLEND();

			// todo
//			originalBlendEnable = ((BooleanStateAccessor) blendState.mode).isEnabled();
			originalBlend = new BlendMode(blendState.srcFactor, blendState.dstFactor, blendState.srcFactorAlpha, blendState.dstFactorAlpha);
		}

		if (override == null) {
			IrisRenderSystem.disableBufferBlend(index);
		} else {
			IrisRenderSystem.enableBufferBlend(index);
			IrisRenderSystem.blendFuncSeparatei(index, override.getSrcRgb(), override.getDstRgb(), override.getSrcAlpha(), override.getDstAlpha());
		}

		blendLocked = true;
	}

	public static void deferBlendModeToggle(boolean enabled) {
		originalBlendEnable = enabled;
	}

	public static void deferBlendFunc(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
		originalBlend = new BlendMode(srcRgb, dstRgb, srcAlpha, dstAlpha);
	}

	public static void restoreBlend() {
		if (!blendLocked) {
			return;
		}

		blendLocked = false;

		if (originalBlendEnable) {
			GlStateManager.enableBlend();
		} else {
			GlStateManager.disableBlend();
		}

		GlStateManager.tryBlendFuncSeparate(originalBlend.getSrcRgb(), originalBlend.getDstRgb(),
				originalBlend.getSrcAlpha(), originalBlend.getDstAlpha());
	}
}
