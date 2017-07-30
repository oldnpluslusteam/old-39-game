package com.github.oldnpluslusteam.old39_game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.alexeybond.partly_solid_bicycle.application.impl.DefaultScreen;
import com.github.alexeybond.partly_solid_bicycle.drawing.Technique;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;

public class FadeScreen extends DefaultScreen {
    private float time = 0;

    @Override
    protected Technique createTechnique() {
        return new PlainTechnique() {
            private TextureRegion texture;

            @Override
            protected void setup() {
                texture = IoC.resolve("get texture region", "sprites/black");
            }

            @Override
            protected void draw() {
                prev().scene().draw();

                Batch batch = state().beginBatch();
                batch.setColor(1f, 1f, 1f, Math.min(1f, time * 0.5f));
                screenQuad(texture, true);
                batch.setColor(Color.WHITE);
            }
        };
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        time += dt;

        if (time > 5f) {
            next(new StartupScreen());
        }
    }

    @Override
    protected boolean checkKeepPrevious() {
        return true;
    }
}
