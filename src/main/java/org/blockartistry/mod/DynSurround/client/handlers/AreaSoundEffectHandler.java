/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.DynSurround.client.handlers;

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.DynSurround.client.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.client.event.RegistryReloadEvent;
import org.blockartistry.mod.DynSurround.client.handlers.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect;
import org.blockartistry.mod.DynSurround.client.sound.SoundManager;
import org.blockartistry.mod.DynSurround.registry.BiomeInfo;
import org.blockartistry.mod.DynSurround.registry.BiomeRegistry;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class AreaSoundEffectHandler extends EffectHandlerBase {

	private static boolean doBiomeSounds() {
		return EnvironState.isPlayerUnderground() || !EnvironState.isPlayerInside();
	}

	private static List<SoundEffect> getBiomeSounds() {
		// Need to collect sounds from all the applicable biomes
		// along with their weights.
		final TObjectIntHashMap<SoundEffect> sounds = new TObjectIntHashMap<SoundEffect>();
		final TObjectIntHashMap<BiomeInfo> weights = AreaSurveyHandler.getBiomes();
		for (final BiomeInfo biome : weights.keySet()) {
			final List<SoundEffect> bs = biome.findSoundMatches();
			for (final SoundEffect sound : bs)
				sounds.put(sound, sounds.get(sound) + weights.get(biome));
		}

		// Scale the volumes in the resulting list based on the weights
		final List<SoundEffect> result = new ArrayList<SoundEffect>();
		final int area = AreaSurveyHandler.getBiomeArea();
		for (final SoundEffect sound : sounds.keySet()) {
			final float scale = 0.3F + 0.7F * ((float) sounds.get(sound) / (float) area);
			result.add(SoundEffect.scaleVolume(sound, scale));
		}

		return result;
	}

	private static void resetSounds() {
		SoundManager.clearSounds();
	}
	
	@Override
	public String getHandlerName() {
		return "AreaSoundEffectHandler";
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		// Dead players hear no sounds
		if (!player.isEntityAlive()) {
			resetSounds();
			return;
		}

		final BiomeInfo playerBiome = EnvironState.getPlayerBiome();
		final List<SoundEffect> sounds = new ArrayList<SoundEffect>();
		
		if (doBiomeSounds())
			sounds.addAll(getBiomeSounds());
		sounds.addAll(BiomeRegistry.PLAYER.findSoundMatches());

		SoundManager.queueAmbientSounds(sounds);

		if (doBiomeSounds()) {
			SoundEffect sound = playerBiome.getSpotSound(EnvironState.RANDOM);
			if (sound != null)
				SoundManager.playSoundAtPlayer(player, sound, SoundCategory.AMBIENT);
		}

		SoundEffect sound = BiomeRegistry.PLAYER.getSpotSound(EnvironState.RANDOM);
		if (sound != null)
			SoundManager.playSoundAtPlayer(player, sound, SoundCategory.AMBIENT);

		SoundManager.update();
	}

	@Override
	public void onConnect() {
		SoundManager.clearSounds();
	}
	
	@Override
	public void onDisconnect() {
		SoundManager.clearSounds();
	}
	
	/*
	 * Fired when the underlying biome config is reloaded.
	 */
	@SubscribeEvent
	public void registryReloadEvent(final RegistryReloadEvent.Biome event) {
		resetSounds();
	}

	/*
	 * Fired when the player joins a world, such as when the dimension changes.
	 */
	@SubscribeEvent
	public void playerJoinWorldEvent(final EntityJoinWorldEvent event) {
		if (event.getEntity().worldObj.isRemote && EnvironState.isPlayer(event.getEntity()))
			resetSounds();
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		final StringBuilder builder = new StringBuilder();
		builder.append("SoundSystem: ").append(SoundManager.currentSoundCount()).append('/')
				.append(SoundManager.maxSoundCount());
		event.output.add(builder.toString());
		for (final String sound : SoundManager.getSounds()) {
			event.output.add(sound);
		}
	}

}
