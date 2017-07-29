package com.github.oldnpluslusteam.old39_game.screens;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.github.alexeybond.partly_solid_bicycle.drawing.Pass;
import com.github.alexeybond.partly_solid_bicycle.drawing.TargetSlot;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;

public class GameTechnique extends PlainTechnique {

    private final static int NORMALS_SAMPLER_ID = 1;
    private final static int LIGHT_SAMPLER_ID = 1;

    private ShaderProgram lightShader, objectsShader, normalShader, finalShader;
    private ShaderProgram normalStubShader;

    private TargetSlot normalSlot;
    private TargetSlot lightSlot;
    private TargetSlot colorSlot;
    private TargetSlot refractionSlot;
    private Pass cameraPass;
    private Pass normalsPass, backNormalsPass, foreNormalsPass;
    private Pass normalStubPass, foreNormalStubPass;
    private Pass lightsPass;
    private Pass objectsPass, backObjectsPass, foreObjectsPass;
    private Pass debugPass;
    private Pass uiPass;
    private Pass particlesPass;
    private Pass refractionPass;

    @Override
    protected void setup() {
        normalSlot = context().getSlot("normals");
        lightSlot = context().getSlot("light");
        colorSlot = context().getSlot("color");
        refractionSlot = context().getSlot("refraction");

        cameraPass = newPass("setup-main-camera");
        normalsPass = newPass("game-normals");
        backNormalsPass = newPass("game-normals-bg");
        foreNormalsPass = newPass("game-normals-fg");
        normalStubPass = newPass("game-normals-stub");
        foreNormalStubPass = newPass("game-normals-stub-fg");
        lightsPass = newPass("game-light");
        objectsPass = newPass("game-objects");
        backObjectsPass = newPass("game-objects-bg");
        foreObjectsPass = newPass("game-objects-fg");
        debugPass = newPass("game-debug");
        particlesPass = newPass("game-particles");
        refractionPass = newPass("game-refraction");
        uiPass = newPass("ui");

        lightShader = loadShader(
                "shaders/deferred_light/light_pass_vs.glsl",
                "shaders/deferred_light/light_pass_ps.glsl"
        );
        objectsShader = loadShader(
                "shaders/deferred_light/objects_pass_vs.glsl",
                "shaders/deferred_light/objects_pass_ps.glsl"
        );
        normalShader = loadShader(
                "shaders/deferred_light/normal_pass_vs.glsl",
                "shaders/deferred_light/normal_pass_ps.glsl"
        );
        finalShader = loadShader(
                "shaders/deferred_light/final_pass_vs.glsl",
                "shaders/deferred_light/final_pass_ps.glsl"
        );
        normalStubShader = loadShader(
                "shaders/deferred_light/normal_pass_vs.glsl",
                "shaders/deferred_light/normal_pass_stub_ps.glsl"
        );
    }

    private final Vector3 tmp = new Vector3();

    @Override
    protected void draw() {
        ensureMatchingFBO(normalSlot, context().getOutputTarget());
        ensureMatchingFBO(lightSlot, context().getOutputTarget());
        ensureMatchingFBO(colorSlot, context().getOutputTarget());
        ensureMatchingFBO(refractionSlot, context().getOutputTarget());

        toTarget(normalSlot);
        clear();
        doPass(cameraPass);
        withShader(normalShader);
        doPass(backNormalsPass);
        doPass(normalsPass);
        doPass(foreNormalsPass);
        withShader(normalStubShader);
        doPass(normalStubPass);
        doPass(foreNormalStubPass);
        withShader(null);

        toTarget(lightSlot);
        bindTexture(NORMALS_SAMPLER_ID, normalSlot.get().asColorTexture().getTexture());
        clear();
        Batch batch = state().beginBatch();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        withShader(lightShader);
        lightShader.setUniformi("u_normalTexture", NORMALS_SAMPLER_ID);
        doPass(cameraPass);
        doPass(lightsPass);
        withShader(null);
        bindTexture(NORMALS_SAMPLER_ID, null);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        toTarget(colorSlot);
        clear();
        withShader(objectsShader);
        objectsShader.setUniformi("u_lightTexture", LIGHT_SAMPLER_ID);
        bindTexture(LIGHT_SAMPLER_ID, lightSlot.get().asColorTexture().getTexture());
        doPass(cameraPass);
        doPass(backObjectsPass);
        doPass(objectsPass);
        doPass(foreObjectsPass);
        withShader(null);
        bindTexture(LIGHT_SAMPLER_ID, null);

        doPass(particlesPass);

        toTarget(refractionSlot);
        gl.glClearColor(0,0,0,0);
        clear();
        batch = state().beginBatch();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        batch.enableBlending();
        doPass(refractionPass);

        toOutput();
        clear();
        batch = state().beginBatch();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.enableBlending();
        float pxPerUnit = tmp.set(1,0,0).rot(batch.getProjectionMatrix()).len();
        bindTexture(1, refractionSlot.get().asColorTexture().getTexture());
        withShader(finalShader);
        finalShader.setUniformi("u_refractionMap", 1);
        finalShader.setUniformf("u_targetSizeInv",
                1f / (float) context().getOutputTarget().getPixelsWidth(),
                1f / (float) context().getOutputTarget().getPixelsHeight()
        );
        finalShader.setUniformf("u_pxPerUnit", pxPerUnit);

        screenQuad(colorSlot.get().asColorTexture(), true);
        batch.flush();
        bindTexture(1, null);

        withShader(null);
        doPass(debugPass);
        doPass(uiPass);

//        screenQuad(normalSlot.get().asColorTexture(), true);
    }
}
