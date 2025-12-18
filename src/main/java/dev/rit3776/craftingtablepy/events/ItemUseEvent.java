package dev.rit3776.craftingtablepy.events;

import java.util.HashMap;
import java.util.Map;

import dev.rit3776.craftingtablepy.PythonBridge;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.TypedActionResult;

public class ItemUseEvent {
    private static final Map<String, Long> lastUseTime = new HashMap<>();

    public static void register(PythonBridge bridge) {
        
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            String key = player.getUuidAsString() + ":" + stack.getItem().toString();
            long now = System.currentTimeMillis();
            if (lastUseTime.containsKey(key) && now - lastUseTime.get(key) < 50) { // 50ms debounce
                return TypedActionResult.pass(stack);
            }
            lastUseTime.put(key, now);

            NbtCompound nbt = stack.getNbt();

            Map<String, Object> data = new HashMap<>();
            data.put("player", player.getName().getString());
            data.put("item_id", stack.getItem().toString());
            data.put("world", world.getRegistryKey().getValue().toString());
            data.put("hand", hand.toString());
            data.put("count", stack.getCount());
            data.put("nbt", nbt != null ? nbt.asString() : null);

            bridge.sendEvent("item_use", data);

            return TypedActionResult.pass(stack);
        });
    }
}
