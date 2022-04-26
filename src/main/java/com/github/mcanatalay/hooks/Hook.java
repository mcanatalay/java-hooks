package com.github.mcanatalay.hooks;

import com.github.mcanatalay.hooks.Effect;
import com.github.mcanatalay.hooks.State;
import java.util.concurrent.*;
import java.util.*;
import java.util.function.*;

public class Hook {
    private static ScheduledExecutorService executor;
    
    private static void initialize() {
        if (executor != null) {
            return;
        }

        Hook.executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            State.renderStates();
            Effect.renderEffects();
        }, 500, TimeUnit.MILLISECONDS);
    }

    public static <T> UseState<T> useState(UnaryOperator<T> setInitialState) {
        initialize();
        return State.useState(setInitialState);
    }

    public static void useEffect(Runnable effect, Object... deps) {
        initialize();
        Effect.useEffect(effect, deps);
    }

    public static void useEffect(Supplier<Runnable> effect, Object... deps) {
        initialize();
        Effect.useEffect(effect, deps);
    }
}
