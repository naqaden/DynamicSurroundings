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

package org.orecruncher.dsurround.event;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReloadEvent extends Event {

	/**
	 * Event fired when resource packs have changed.
	 */
	@SideOnly(Side.CLIENT)
	public static class Resources extends ReloadEvent {

		public final IResourceManager manager;

		public Resources(final IResourceManager manager) {
			this.manager = manager;
		}
	}

	/**
	 * Event fired when the mod configuration file changed.
	 */
	public static class Configuration extends ReloadEvent {

		public final Side side;

		public Configuration() {
			this.side = null;
		}

		public Configuration(@Nonnull final Side side) {
			this.side = side;
		}
	}

	/**
	 * Event fired when the RegistryManager finished reloading registries and is
	 * notifying listeners of the fact.
	 */
	public static class Registry extends ReloadEvent {

		public final Side side;

		public Registry(@Nonnull final Side side) {
			this.side = side;
		}
	}

}
