package com.github.oldnpluslusteam.old39_game.components;

import com.badlogic.gdx.math.MathUtils;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.BodyPhysicsComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.timing.TimingSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;

public class SparkController implements Component {
    private final String enablePropertyName;
    private final String enableEventName;
    private final float[] upRange;
    private final float[] downRange;

    public SparkController(String enablePropertyName, String enableEventName, float[] upRange, float[] downRange) {
        this.enablePropertyName = enablePropertyName;
        this.enableEventName = enableEventName;
        this.upRange = upRange;
        this.downRange = downRange;
    }

    private final Subscription<Event> tickSub
            = new Subscription<Event>() {
        @Override
        public boolean onTriggered(Event event) {
            boolean enable = !enableProp.get();
            enableProp.set(enable);
            if (enable) enableEvent.trigger();
            scheduleNext(enable ? upRange : downRange);
            return false;
        }
    };

    private Event enableEvent;
    private BooleanProperty enableProp;
    private TimingSystem timingSystem;
    private FloatProperty timeProp;

    @Override
    public void onConnect(Entity entity) {
        enableProp = entity.events().event(enablePropertyName, BooleanProperty.make());
        enableEvent = entity.events().event(enableEventName, Event.makeEvent());
        timingSystem = entity.game().systems().get("timing");
        timeProp = timingSystem.events().event("time");
        tickSub.set(Event.makeEvent().create());
        scheduleNext(downRange);

        BodyPhysicsComponent bodyPhysicsComponent = entity.components().get("body");
        bodyPhysicsComponent.body().applyForceToCenter(-100000, 0, true);
    }

    @Override
    public void onDisconnect(Entity entity) {
        tickSub.clear();
    }

    private void scheduleNext(float[] range) {
        float delay = MathUtils.random(range[0], range[1]);
        timingSystem.scheduleAt(timeProp.get() + delay, tickSub.event());
    }

    public static class Decl implements ComponentDeclaration {
        public String enableProperty = "enableSpark";
        public String enableEvent = "onSparkEnabled";

        public float[] upRange = new float[] {1,1};
        public float[] downRange = new float[] {2, 3};

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new SparkController(enableProperty, enableEvent, upRange, downRange);
        }
    }
}
