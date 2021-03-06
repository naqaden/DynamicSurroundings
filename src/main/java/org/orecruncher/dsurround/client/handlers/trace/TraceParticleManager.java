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
package org.orecruncher.dsurround.client.handlers.trace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.lib.ThreadGuard;
import org.orecruncher.lib.ThreadGuard.Action;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TraceParticleManager extends ParticleManager {

	protected final ParticleManager manager;
	protected Object2IntOpenHashMap<Class<?>> counts;

	protected final ThreadGuard guard = new ThreadGuard(ModBase.log(), Side.CLIENT, "ParticleManager")
			.setAction(ModBase.isDeveloperMode() ? Action.EXCEPTION
					: ModOptions.logging.enableDebugLogging ? Action.LOG : Action.NONE);

	public TraceParticleManager(@Nonnull final ParticleManager manager) {
		super(null, null);

		this.manager = manager;
		this.counts = new Object2IntOpenHashMap<>();
		this.counts.defaultReturnValue(-1);
	}

	private void checkForClientThread(final String method) {
		this.guard.check(method);
	}

	public Object2IntOpenHashMap<Class<?>> getSnaptshot() {
		final Object2IntOpenHashMap<Class<?>> result = this.counts;
		this.counts = new Object2IntOpenHashMap<>();
		this.counts.defaultReturnValue(-1);
		return result;
	}

	@Override
	public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
		checkForClientThread("emitParticleAtEntity");
		this.manager.emitParticleAtEntity(entityIn, particleTypes);
	}

	@Override
	public void emitParticleAtEntity(Entity p_191271_1_, EnumParticleTypes p_191271_2_, int p_191271_3_) {
		checkForClientThread("emitParticleAtEntity");
		this.manager.emitParticleAtEntity(p_191271_1_, p_191271_2_, p_191271_3_);
	}

	@Override
	public Particle spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed,
			double ySpeed, double zSpeed, int... parameters) {
		checkForClientThread("spawnEffectParticle");
		return this.manager.spawnEffectParticle(particleId, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
	}

	@Override
	public void addEffect(Particle effect) {
		checkForClientThread("addEffect");
		if (effect != null) {
			final Class<?> clazz = effect.getClass();
			final int c = this.counts.getInt(clazz);
			if (c == -1)
				this.counts.put(clazz, 1);
			else
				this.counts.put(clazz, c + 1);
		}
		this.manager.addEffect(effect);
	}

	@Override
	public void updateEffects() {
		checkForClientThread("updateEffects");
		this.manager.updateEffects();
	}

	@Override
	public void renderParticles(Entity entityIn, float partialTicks) {
		checkForClientThread("renderParticles");
		this.manager.renderParticles(entityIn, partialTicks);
	}

	@Override
	public void renderLitParticles(Entity entityIn, float partialTick) {
		checkForClientThread("renderLitParticles");
		this.manager.renderLitParticles(entityIn, partialTick);
	}

	@Override
	public void clearEffects(@Nullable World worldIn) {
		checkForClientThread("clearEffects");
		this.manager.clearEffects(worldIn);
	}

	@Override
	public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
		checkForClientThread("addBlockDestroyEffects");
		this.manager.addBlockDestroyEffects(pos, state);
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
		checkForClientThread("addBlockHitEffects");
		this.manager.addBlockHitEffects(pos, side);
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, net.minecraft.util.math.RayTraceResult target) {
		checkForClientThread("addBlockHitEffects");
		this.manager.addBlockHitEffects(pos, target);
	}

	@Override
	public String getStatistics() {
		checkForClientThread("getStatistics");
		return this.manager.getStatistics();
	}
}
