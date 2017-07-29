package com.github.oldnpluslusteam.old39_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.application.Layer;
import com.github.alexeybond.partly_solid_bicycle.application.Screen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.DefaultScreen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayerWith2DPhysicalGame;
import com.github.alexeybond.partly_solid_bicycle.application.util.ScreenUtils;
import com.github.alexeybond.partly_solid_bicycle.drawing.Technique;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.EDSLTechnique;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.visitor.impl.ApplyGameDeclarationVisitor;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.interfaces.APhysicsSystem;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.EventListener;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.parts.AParts;

public class StartupScreen extends DefaultScreen {
    @Override
    protected Technique createTechnique() {
        return new GameTechnique();
    }

    @Override
    protected void createLayers(AParts<Screen, Layer> layers) {
        super.createLayers(layers);

        ScreenUtils.enableToggleDebug(this, true);

        Game game = layers.add("game", new GameLayerWith2DPhysicalGame()).game();
        GameDeclaration gameDeclaration = IoC.resolve(
                "load game declaration",
                Gdx.files.internal("scenes/scene1.json"));
        new ApplyGameDeclarationVisitor().doVisit(gameDeclaration, game);

        game.systems().<APhysicsSystem>get("physics").world().setGravity(new Vector2(0, -100));

        input().keyEvent("R").subscribe(new EventListener<BooleanProperty>() {
            private BooleanProperty debugEnabled = input().events()
                    .event("debugEnabled", BooleanProperty.make(false));

            @Override
            public boolean onTriggered(BooleanProperty event) {
                if (event.get()) return false;
                if (!debugEnabled.get()) return false;
                next(new StartupScreen());
                return true;
            }
        });
    }
}
