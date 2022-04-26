package com.github.mcanatalay.hooks;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Effect {
    private static final Set<UseEffect> effects = new LinkedHashSet<>();

    public static void useEffect(Runnable effect, Object... deps) {
        effects.add(new UseEffect(effect, deps));
    }

    public static void useEffect(Supplier<Runnable> effect, Object... deps) {
        effects.add(new UseEffect(effect, deps));
    }

    public static void renderEffects() {
        Iterator<UseEffect> iterator = effects.iterator();
        while(iterator.hasNext()) {
            UseEffect useEffect = iterator.next();
            useEffect.cleanup();
        }
        
        iterator = effects.iterator();
        while(iterator.hasNext()) {
            UseEffect useEffect = iterator.next();
            useEffect.render();
        }
    }

    private static class UseEffect {
        private final Supplier effect;
        private final Object[] deps;

        private Runnable clean;
        private List<Integer> hashCodes;
        private List<Integer> updatedHashCodes;

        private UseEffect(Supplier<Runnable> effect, Object... deps){
            this.effect = effect;
            this.deps = deps;
            this.hashCodes = null;
            this.updatedHashCodes = null;
        }

        private UseEffect(Runnable effect, Object... deps){
            this(() -> {
                effect.run();
                return () -> {};
            }, deps);
        }

        private void cleanup() {
            this.updatedHashCodes = hash(deps);
            if (hashCodes != null && IntStream.range(0, hashCodes.size()).anyMatch(i -> hashCodes.get(i) != updatedHashCodes.get(i))) {
                clean.run();
            }
        }

        private void render() {
            if (hashCodes == null || IntStream.range(0, hashCodes.size()).anyMatch(i -> hashCodes.get(i) != updatedHashCodes.get(i))) {
                this.clean = (Runnable) effect.get();
                hashCodes = updatedHashCodes;
            }
        }
    }

    private static List<Integer> hash(Object[] deps) {
        return Stream.of(deps)
            .map((dep) -> {
                if (dep instanceof Supplier) {
                    return ((Supplier) dep).get();
                } else {
                    return dep;
                }
            })
            .map((dep) -> Objects.hash(dep))
            .collect(Collectors.toList());
    }
}
