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

package org.orecruncher.dsurround.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.SeasonInfo;
import org.orecruncher.lib.capability.CapabilityProviderSerializable;
import org.orecruncher.lib.capability.CapabilityUtils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CapabilitySeasonInfo {

	@CapabilityInject(ISeasonInfo.class)
	public static final Capability<ISeasonInfo> SEASON_INFO = null;
	public static final EnumFacing DEFAULT_FACING = null;
	public static final ResourceLocation CAPABILITY_ID = new ResourceLocation(ModBase.MOD_ID, "seasoninfo");

	@SideOnly(Side.CLIENT)
	public static void register() {
		CapabilityManager.INSTANCE.register(ISeasonInfo.class, new Capability.IStorage<ISeasonInfo>() {
			@Override
			public NBTBase writeNBT(@Nonnull final Capability<ISeasonInfo> capability,
					@Nonnull final ISeasonInfo instance, @Nullable final EnumFacing side) {
				return ((INBTSerializable<NBTTagCompound>) instance).serializeNBT();
			}

			@Override
			public void readNBT(@Nonnull final Capability<ISeasonInfo> capability, @Nonnull final ISeasonInfo instance,
					@Nullable final EnumFacing side, @Nonnull final NBTBase nbt) {
				((INBTSerializable<NBTTagCompound>) instance).deserializeNBT((NBTTagCompound) nbt);
			}
		}, SeasonInfo::new);
	}

	@SideOnly(Side.CLIENT)
	public static ISeasonInfo getCapability(@Nonnull final World world) {
		return CapabilityUtils.getCapability(world, SEASON_INFO, null);
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	public static ICapabilityProvider createProvider(final ISeasonInfo data) {
		return new CapabilityProviderSerializable<>(SEASON_INFO, DEFAULT_FACING, data);
	}

	@EventBusSubscriber(modid = ModBase.MOD_ID, value = Side.CLIENT)
	public static class EventHandler {
		@SubscribeEvent
		public static void attachCapabilities(@Nonnull final AttachCapabilitiesEvent<World> event) {
			final World world = event.getObject();
			if (world != null && world.isRemote) {
				final SeasonInfo info = SeasonInfo.factory(world);
				event.addCapability(CAPABILITY_ID, createProvider(info));
			}
		}
	}
}
