package dev.rit3776.craftingtablepy.events;

import java.util.HashMap;
import java.util.Map;

import dev.rit3776.craftingtablepy.PythonBridge;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public class ChatEvent {
    public static void register(PythonBridge bridge) {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, typeKey) -> {
            String playerName = sender.getName().getString();
            String content = message.getContent().getString();

            Map<String, Object> data = new HashMap<>();
            data.put("player", playerName);
            data.put("message", content);
            data.put("type", typeKey.toString());

            bridge.sendEvent("chat", data);
        });
    }
}
