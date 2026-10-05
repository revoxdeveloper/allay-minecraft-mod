package com.revoxdeveloper.allay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AllayClientMod implements ClientModInitializer {

    public static final String MOD_ID = "allay-client-mod";
    private static KeyBinding allayMenuKey;
    private static boolean menuOpen = false;

    @Override
    public void onInitializeClient() {
        // Register Keybinding (Right Shift)
        allayMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.allay.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.allay"
        ));

        // Tick event to detect key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (allayMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new AllayMenuScreen());
                } else {
                    client.setScreen(null);
                }
            }
        });
    }

    public static String getModId() {
        return MOD_ID;
    }
}
