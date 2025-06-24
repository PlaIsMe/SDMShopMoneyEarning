package com.pla.plamoneyget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.*;

@EventBusSubscriber(modid = PlaMoneyGet.MOD_ID, value = Dist.CLIENT)
public class MoneyPickupOverlay {
    private static final List<PickupMessage> messages = new ArrayList<>();
    private static final int MESSAGE_LIFETIME = 3000; // 3 seconds

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int x = mc.getWindow().getGuiScaledWidth() - 100; // Bottom right
        int y = mc.getWindow().getGuiScaledHeight() - 40;

        long currentTime = System.currentTimeMillis();
        messages.removeIf(msg -> currentTime - msg.timestamp > MESSAGE_LIFETIME);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = mc.font;
        for (PickupMessage msg : messages) {
            guiGraphics.drawString(font, msg.text, x, y, msg.color);
            y -= 12;
        }
    }

    public static void addPickupMessage(String message, ChatFormatting color) {
        messages.add(new PickupMessage(
                Component.literal(message).withStyle(style -> style.withColor(color)),
                System.currentTimeMillis(),
                color.getColor()
        ));
    }

    private static class PickupMessage {
        final Component text;
        final long timestamp;
        final int color;

        PickupMessage(Component text, long timestamp, int color) {
            this.text = text;
            this.timestamp = timestamp;
            this.color = color;
        }
    }
}
