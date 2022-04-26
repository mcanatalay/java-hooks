package com.github.mcanatalay.hooks;

import java.util.*;
import java.util.function.*;

public class State {
    private static final Set<UseState> updatedStates = new LinkedHashSet<>();

    public static <T> UseState<T> useState(T initialState) {
        return new UseState(
            initialState,
            updatedStates
        );
    }

    public static <T> UseState<T> useState(UnaryOperator<T> setInitialState) {
        return new UseState(
            setInitialState,
            updatedStates
        );
    }

    public static void renderStates() {
        Iterator<UseState> iterator = updatedStates.iterator();
        while(iterator.hasNext()) {
            UseState updatedState = iterator.next();
            updatedState.render();
            iterator.remove();
        }
    }

    public static class UseState<T> {
        public final Supplier<T> state;
        public final Consumer<UnaryOperator<T>> setState;

        private T _state;
        private UnaryOperator<T> _setState;
        
        private UseState(T initialState, final Set<UseState> updatedStates) {
            this._state = initialState;
            this.state = () -> _state;
            this._setState = null;
            this.setState = (UnaryOperator<T> setterFunction) -> {
                _setState = setterFunction;
                updatedStates.add(UseState.this);
            };
        }

        private UseState(UnaryOperator<T> setInitialState, final Set<UseState> updatedStates) {
            this((T) null, updatedStates);
            setState.accept(setInitialState);
        }

        private void render() {
            if (_setState != null) {
                _state = _setState.apply(_state);
                _setState = null;
            }
        }
    }
}
