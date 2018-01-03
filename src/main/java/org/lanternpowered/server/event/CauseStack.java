/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * A {@link CauseStack} for a specific {@link Thread}.
 */
public interface CauseStack extends CauseStackManager {

    /**
     * Gets the {@link LanternCauseStack} for the current {@link Thread}.
     * {@code null} will be returned if the thread isn't supported.
     *
     * @return The cause stack
     */
    @Nullable
    static CauseStack currentOrNull() {
        return LanternCauseStack.causeStacks.get();
    }

    /**
     * Gets the {@link LanternCauseStack} for the current {@link Thread}. A
     * empty {@link CauseStack} will be returned if the thread isn't supported.
     *
     * @return The cause stack
     */
    static CauseStack currentOrEmpty() {
        final CauseStack causeStack = LanternCauseStack.causeStacks.get();
        return causeStack == null ? EmptyCauseStack.instance : causeStack;
    }

    /**
     * Gets the {@link LanternCauseStack} for the current {@link Thread}. A
     * {@link IllegalStateException} will be thrown if the current thread
     * isn't supported.
     *
     * @return The cause stack
     */
    static CauseStack current() {
        final CauseStack causeStack = LanternCauseStack.causeStacks.get();
        checkState(causeStack != null, "The current thread doesn't support a cause stack.");
        return causeStack;
    }

    /**
     * Sets the {@link LanternCauseStack} for the current {@link Thread}. This
     * method should be called by every thread that should support a
     * {@link LanternCauseStack}, attempting to push causes on a thread that
     * doesn't support it will result in an exception.
     *
     * @param causeStack The cause stack
     */
    static void set(LanternCauseStack causeStack) {
        LanternCauseStack.causeStacks.set(checkNotNull(causeStack, "causeStack"));
    }

    /**
     * Gets the first <code>T</code> object of this {@link Cause}, if available.
     *
     * @param target The class of the target type
     * @param <T> The type of object being queried for
     * @return The first element of the type, if available
     */
    <T> Optional<T> first(Class<T> target);

    /**
     * Gets the last object instance of the {@link Class} of type
     * <code>T</code>.
     *
     * @param target The class of the target type
     * @param <T> The type of object being queried for
     * @return The last element of the type, if available
     */
    <T> Optional<T> last(Class<T> target);

    /**
     * Returns whether the target class matches any object of this {@link Cause}.
     *
     * @param target The class of the target type
     * @return True if found, false otherwise
     */
    boolean containsType(Class<?> target);

    /**
     * Checks if this cause contains of any of the provided {@link Object}. This
     * is the equivalent to checking based on {@link #equals(Object)} for each
     * object in this cause.
     *
     * @param object The object to check if it is contained
     * @return True if the object is contained within this cause
     */
    boolean contains(Object object);

    @Override
    Frame pushCauseFrame();

    @Override
    CauseStack pushCause(Object obj);

    @Override
    <T> CauseStack addContext(EventContextKey<T> key, T value);

    interface Frame extends CauseStackManager.StackFrame {

        @Override
        Frame pushCause(Object obj);

        @Override
        <T> Frame addContext(EventContextKey<T> key, T value);
    }
}
