/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.metrics.core.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * An abstraction for how time passes.
 */
public abstract class Clock {
    /**
     * Returns the current time tick.
     *
     * @return time tick in nanoseconds
     */
    public abstract long getTick();

    /**
     * Returns the current time in milliseconds.
     *
     * @return time in milliseconds
     */
    public long getTime() {
        return System.currentTimeMillis();
    }

    private static final Clock DEFAULT = new UserTimeClock();

    /**
     * The default clock to use.
     *
     * @return the default {@link Clock} instance
     * @see Clock.UserTimeClock
     */
    public static Clock defaultClock() {
        return DEFAULT;
    }

    /**
     * A clock implementation which returns the current time in epoch nanoseconds.
     */
    public static class UserTimeClock extends Clock {
        @Override
        public long getTick() {
            return System.nanoTime();
        }
    }

    /**
     * A clock implementation which returns the current thread's CPU time.
     */
    public static class CpuTimeClock extends Clock {
        private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

        @Override
        public long getTick() {
            return THREAD_MX_BEAN.getCurrentThreadCpuTime();
        }
    }
}
