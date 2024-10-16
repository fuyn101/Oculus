package net.coderbot.iris.mixin.sky;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables void plane rendering when submerged in a fluid, to avoid breaking the fog illusion in oceans
 * and lava.
 *
 * Inspired by https://github.com/CaffeineMC/sodium-fabric/pull/710, but this implementation
 * takes a far more conservative approach and only disables specific parts of sky rendering in high-fog
 * situations.
 */
@Mixin(WorldType.class)
public class MixinClientLevelData_DisableVoidPlane {
	// todo: unable to locate obfuscation mapping
//	@Inject(method = "getHorizon", at = @At("HEAD"), cancellable = true)
//	private void iris$getHorizonHeight(World world, CallbackInfoReturnable<Double> cir) {
//		Minecraft mc = Minecraft.getMinecraft();
//		Entity cameraEntity = mc.getRenderViewEntity();
//		// TODO getBlockStateAtEntityViewpoint is not adapted to Forge's IFluidBlock, maybe it's better to use ForgeHooks.isInsideOfMaterial?
//		IBlockState submergedFluid = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, cameraEntity, mc.timer.renderPartialTicks);
//		Block block = submergedFluid.getBlock();
//
//		if (block instanceof BlockLiquid || block instanceof IFluidBlock) {
//			cir.setReturnValue(Double.NEGATIVE_INFINITY);
//		}
//	}
}
