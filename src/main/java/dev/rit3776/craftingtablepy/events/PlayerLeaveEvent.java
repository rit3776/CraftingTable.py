package dev.rit3776.craftingtablepy.events;

import java.util.HashMap;
import java.util.Map;

import dev.rit3776.craftingtablepy.PythonBridge;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerLeaveEvent {
    public static void register(PythonBridge bridge) {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            
            Map<String, Object> data = new HashMap<>();
            data.put("player", player.getName().getString());
            
            bridge.sendEvent("on_player_join", data);
        });
    }
}
