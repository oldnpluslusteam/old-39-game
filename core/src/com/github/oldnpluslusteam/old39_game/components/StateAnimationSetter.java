package com.github.oldnpluslusteam.old39_game.components;

import com.badlogic.gdx.Gdx;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;

import java.util.HashMap;
import java.util.Map;

public class StateAnimationSetter implements Component {
    private final Subscription<ObjectProperty<String>> stateSub
            = new Subscription<ObjectProperty<String>>() {
        @Override
        public boolean onTriggered(ObjectProperty<String> event) {
            String animationName = animationMap.get(event.get());
            if (null == animationName) {
                Gdx.app.log("GAME", "No animation for state '" + event.get() + "'");
                return false;
            }
            animationProp.set(animationName);
            return true;
        }
    };

    private final Map<String, String> animationMap;

    private ObjectProperty<String> animationProp;

    public StateAnimationSetter(Map<String, String> animationMap) {
        this.animationMap = animationMap;
    }

    @Override
    public void onConnect(Entity entity) {
        animationProp = entity.events().event("animation", ObjectProperty.<String>make());
        stateSub.set(entity.events().event("state", ObjectProperty.make("idle")));
    }

    @Override
    public void onDisconnect(Entity entity) {
        stateSub.clear();
    }

    private final static HashMap<String, String> DEFAULT_MAP = new HashMap<String, String>() {{}};

    public static class Decl implements ComponentDeclaration {
        public HashMap<String, String> map = DEFAULT_MAP;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new StateAnimationSetter(map);
        }
    }
}
