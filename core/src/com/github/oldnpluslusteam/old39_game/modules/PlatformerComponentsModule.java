package com.github.oldnpluslusteam.old39_game.modules;

import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.ioc.modules.Module;
import com.github.oldnpluslusteam.old39_game.components.PlayerController;
import com.github.oldnpluslusteam.old39_game.components.StateAnimationSetter;

import java.util.Map;

public class PlatformerComponentsModule implements Module {
    @Override
    public void init() {
        Map<String, Class> map = IoC.resolve("component type aliases");

        map.put("player controller", PlayerController.Decl.class);

        map.put("state animation setter", StateAnimationSetter.Decl.class);
    }

    @Override
    public void shutdown() {

    }
}
