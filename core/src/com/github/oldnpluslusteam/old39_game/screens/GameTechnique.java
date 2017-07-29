package com.github.oldnpluslusteam.old39_game.screens;

import com.github.alexeybond.partly_solid_bicycle.drawing.Pass;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;

public class GameTechnique extends PlainTechnique {
    private Pass gameBackPass;
    private Pass gameForePass;
    private Pass gameDebugPass;
    private Pass mainCameraPass;

    @Override
    protected void setup() {
        gameBackPass = newPass("game-background");
        gameForePass = newPass("game-objects");
        gameDebugPass = newPass("game-debug");
        mainCameraPass = newPass("setup-main-camera");
    }

    @Override
    protected void draw() {
        clear();
        doPass(mainCameraPass);
        doPass(gameBackPass);
        doPass(gameForePass);
        doPass(gameDebugPass);
    }
}
