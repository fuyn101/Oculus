package net.coderbot.iris.texture.pbr;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.coderbot.iris.Iris;
import net.coderbot.iris.gl.state.StateUpdateNotifiers;
import net.coderbot.iris.mixin.GlStateManagerAccessor;
import net.coderbot.iris.rendertarget.BufferedImageBackedSingleColorTexture;
import net.coderbot.iris.texture.TextureTracker;
import net.coderbot.iris.texture.pbr.loader.PBRTextureLoader;
import net.coderbot.iris.texture.pbr.loader.PBRTextureLoader.PBRTextureConsumer;
import net.coderbot.iris.texture.pbr.loader.PBRTextureLoaderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;

import javax.annotation.Nonnull;

public class PBRTextureManager {
	public static final PBRTextureManager INSTANCE = new PBRTextureManager();

	public static final boolean DEBUG = System.getProperty("iris.pbr.debug") != null;

	// TODO: Figure out how to merge these two.
	private static Runnable normalTextureChangeListener;
	private static Runnable specularTextureChangeListener;

	static {
		StateUpdateNotifiers.normalTextureChangeNotifier = listener -> normalTextureChangeListener = listener;
		StateUpdateNotifiers.specularTextureChangeNotifier = listener -> specularTextureChangeListener = listener;
	}

	private final Int2ObjectMap<PBRTextureHolder> holders = new Int2ObjectOpenHashMap<>();
	private final PBRTextureConsumerImpl consumer = new PBRTextureConsumerImpl();

	private BufferedImageBackedSingleColorTexture defaultNormalTexture;
	private BufferedImageBackedSingleColorTexture defaultSpecularTexture;
	// Not PBRTextureHolderImpl to directly reference fields
	private final PBRTextureHolder defaultHolder = new PBRTextureHolder() {
		@Override
		public @Nonnull AbstractTexture getNormalTexture() {
			return defaultNormalTexture;
		}

		@Override
		public @Nonnull AbstractTexture getSpecularTexture() {
			return defaultSpecularTexture;
		}
	};

	private PBRTextureManager() {
	}

	public void init() {
		defaultNormalTexture = new BufferedImageBackedSingleColorTexture(PBRType.NORMAL.getDefaultValue());
		defaultSpecularTexture = new BufferedImageBackedSingleColorTexture(PBRType.SPECULAR.getDefaultValue());
	}

	public PBRTextureHolder getHolder(int id) {
		PBRTextureHolder holder = holders.get(id);
		if (holder == null) {
			return defaultHolder;
		}
		return holder;
	}

	public PBRTextureHolder getOrLoadHolder(int id) {
		PBRTextureHolder holder = holders.get(id);
		if (holder == null) {
			holder = loadHolder(id);
			holders.put(id, holder);
		}
		return holder;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private PBRTextureHolder loadHolder(int id) {
		AbstractTexture texture = TextureTracker.INSTANCE.getTexture(id);
		if (texture != null) {
			Class<? extends AbstractTexture> clazz = texture.getClass();
			PBRTextureLoader loader = PBRTextureLoaderRegistry.INSTANCE.getLoader(clazz);
			if (loader != null) {
				int previousTextureBinding = GlStateManagerAccessor.getTEXTURES()[GlStateManagerAccessor.getActiveTexture()].textureName;
				consumer.clear();
				try {
					loader.load(texture, Minecraft.getMinecraft().getResourceManager(), consumer);
					return consumer.toHolder();
				} catch (Exception e) {
					Iris.logger.debug("Failed to load PBR textures for texture " + id, e);
				} finally {
					GlStateManager.bindTexture(previousTextureBinding);
				}
			}
		}
		return defaultHolder;
	}

	public void onDeleteTexture(int id) {
		PBRTextureHolder holder = holders.remove(id);
		if (holder != null) {
			closeHolder(holder);
		}
	}

	public void clear() {
		for (PBRTextureHolder holder : holders.values()) {
			if (holder != defaultHolder) {
				closeHolder(holder);
			}
		}
		holders.clear();
	}

	public void close() {
		clear();
		defaultNormalTexture.deleteGlTexture();
		defaultSpecularTexture.deleteGlTexture();
	}

	private void closeHolder(PBRTextureHolder holder) {
		AbstractTexture normalTexture = holder.getNormalTexture();
		AbstractTexture specularTexture = holder.getSpecularTexture();
		if (normalTexture != defaultNormalTexture) {
			closeTexture(normalTexture);
		}
		if (specularTexture != defaultSpecularTexture) {
			closeTexture(specularTexture);
		}
	}

	private static void closeTexture(AbstractTexture texture) {
		try {
			texture.deleteGlTexture();
		} catch (Exception e) {
			//
		}
		texture.deleteGlTexture();
	}

	public static void notifyPBRTexturesChanged() {
		if (normalTextureChangeListener != null) {
			normalTextureChangeListener.run();
		}

		if (specularTextureChangeListener != null) {
			specularTextureChangeListener.run();
		}
	}

	private class PBRTextureConsumerImpl implements PBRTextureConsumer {
		private AbstractTexture normalTexture;
		private AbstractTexture specularTexture;
		private boolean changed;

		@Override
		public void acceptNormalTexture(@Nonnull AbstractTexture texture) {
			normalTexture = texture;
			changed = true;
		}

		@Override
		public void acceptSpecularTexture(@Nonnull AbstractTexture texture) {
			specularTexture = texture;
			changed = true;
		}

		public void clear() {
			normalTexture = defaultNormalTexture;
			specularTexture = defaultSpecularTexture;
			changed = false;
		}

		public PBRTextureHolder toHolder() {
			if (changed) {
				return new PBRTextureHolderImpl(normalTexture, specularTexture);
			} else {
				return defaultHolder;
			}
		}
	}

	private static class PBRTextureHolderImpl implements PBRTextureHolder {
		private final AbstractTexture normalTexture;
		private final AbstractTexture specularTexture;

		public PBRTextureHolderImpl(AbstractTexture normalTexture, AbstractTexture specularTexture) {
			this.normalTexture = normalTexture;
			this.specularTexture = specularTexture;
		}

		@Override
		public @Nonnull AbstractTexture getNormalTexture() {
			return normalTexture;
		}

		@Override
		public @Nonnull AbstractTexture getSpecularTexture() {
			return specularTexture;
		}
	}
}
