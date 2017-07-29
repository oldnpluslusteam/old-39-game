package com.github.oldnpluslusteam.old39_game.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.CollisionData;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.components.BaseBodyComponent;
import com.github.alexeybond.partly_solid_bicycle.game.systems.timing.TimingSystem;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.ObjectProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;

public class PlayerController implements Component {
    private final String STATE_IDLE = "idle";
    private final String STATE_WALK_R = "walk-r";
    private final String STATE_WALK_L = "walk-l";
    private final String STATE_JUMP_R = "jump-r";
    private final String STATE_JUMP_L = "jump-l";
    private final String STATE_FALL_R = "fall-r";
    private final String STATE_FALL_L = "fall-l";

    private float jumpVelocity = 50;
    private float walkVelocity = 50;
    private float airWalkVelocity = 20;

    private Entity entity;
    private BaseBodyComponent bodyComponent;

    private final Subscription<FloatProperty> deltaTimeSub
            = new Subscription<FloatProperty>() {
        @Override
        public boolean onTriggered(FloatProperty event) {
            update(event.get());
            return true;
        }
    };

    private final Subscription<ObjectProperty<CollisionData>> groundCollisionBeginSub
            = new Subscription<ObjectProperty<CollisionData>>() {
        @Override
        public boolean onTriggered(ObjectProperty<CollisionData> event) {
            if (checkIgnoreGroundCollision(event.get().that.entity())) return false;
            ++sensorCollisionCount;
            return true;
        }
    };

    private final Subscription<ObjectProperty<CollisionData>> groundCollisionEndSub
            = new Subscription<ObjectProperty<CollisionData>>() {
        @Override
        public boolean onTriggered(ObjectProperty<CollisionData> event) {
            if (checkIgnoreGroundCollision(event.get().that.entity())) return false;
            --sensorCollisionCount;
            return true;
        }
    };

    private BooleanProperty goRightControl, goLeftControl, jumpControl;
    private Vec2Property positionProp;

    private int sensorCollisionCount = 0;
    private boolean jumping, jumpFinished;

    private ObjectProperty<String> stateProp;
    private float lastKnownXVelocity;

    @Override
    public void onConnect(Entity entity) {
        this.entity = entity;
        bodyComponent = entity.components().get("body");

        deltaTimeSub.set(entity.game().systems().<TimingSystem>get("timing").events()
                .<FloatProperty>event("deltaTime"));

        goRightControl = entity.events().event("goRightControl", BooleanProperty.make());
        goLeftControl = entity.events().event("goLeftControl", BooleanProperty.make());
        jumpControl = entity.events().event("jumpControl", BooleanProperty.make());
        positionProp = entity.events().event("position", Vec2Property.make());

        groundCollisionBeginSub.set(entity.events()
                .event("groundCollisionBegin", ObjectProperty.<CollisionData>make()));
        groundCollisionEndSub.set(entity.events()
                .event("groundCollisionEnd", ObjectProperty.<CollisionData>make()));

        stateProp = entity.events()
                .event("state", ObjectProperty.make("idle")).use(ObjectProperty.STRING_LOADER);

        sensorCollisionCount = 0;
        lastKnownXVelocity = 1;
        jumpFinished = true;
    }

    @Override
    public void onDisconnect(Entity entity) {
        deltaTimeSub.clear();
        groundCollisionBeginSub.clear();
        groundCollisionEndSub.clear();
    }

    private void update(float dt) {
        Vector2 velocity = bodyComponent.body().getLinearVelocity();

        final boolean grounded = isGrounded();

        if (grounded && jumpControl.get()) {
            if (!jumping && velocity.y > -jumpVelocity) {
                velocity.y = Math.max(jumpVelocity, velocity.y + jumpVelocity);
                jumping = true;
                jumpFinished = false;
            }
        } else {
            jumping = false;
            jumpFinished = grounded;
        }

        int vxi = 0;

        if (goRightControl.get()) ++vxi;
        if (goLeftControl.get()) --vxi;

        if (vxi != 0) {
            float targetAbsVelocity = grounded ? walkVelocity : airWalkVelocity;
            float targetVelocity = targetAbsVelocity * (float) vxi;

            float targetVelocityDelta = targetVelocity - velocity.x;

            if (Math.signum(targetVelocityDelta) == Math.signum(targetVelocity)) {
                velocity.x = targetVelocity;
            }
        } else {
            if (grounded && Math.abs(velocity.x) < walkVelocity) {
                velocity.x = 0;
            }
        }

        bodyComponent.body().setLinearVelocity(velocity);

        stateProp.set(getTargetState(velocity));
    }

    private boolean checkIgnoreGroundCollision(Entity entity) {
        if (this.entity == entity) return true;

        return false;
    }

    private boolean isGrounded() {
        return sensorCollisionCount > 0;
    }

    private String getTargetState(Vector2 velocity) {
        float xv = MathUtils.isZero(velocity.x) ? lastKnownXVelocity : velocity.x;
        lastKnownXVelocity = xv;

        if (!jumpFinished) {
            return (xv < 0) ? STATE_JUMP_L : STATE_JUMP_R;
        }

        if (isGrounded()) {
            if (MathUtils.isZero(velocity.x)) {
                return (xv < 0) ? STATE_IDLE : STATE_IDLE;
            } else {
                return (xv < 0) ? STATE_WALK_L : STATE_WALK_R;
            }
        }

        return (xv < 0) ? STATE_FALL_L : STATE_FALL_R;
    }

    public static class Decl implements ComponentDeclaration {

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new PlayerController();
        }
    }
}
