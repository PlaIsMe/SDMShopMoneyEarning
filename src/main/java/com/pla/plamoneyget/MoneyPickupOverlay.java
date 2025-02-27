package com.pla.plamoneyget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class MoneyPickupOverlay {
    private static final List<PickupMessage> messages = new ArrayList<>();
    private static final int MESSAGE_LIFETIME = 3000; // 3 seconds

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int x = mc.getWindow().getGuiScaledWidth() - 100; // Bottom right
        int y = mc.getWindow().getGuiScaledHeight() - 40;

        long currentTime = System.currentTimeMillis();
        messages.removeIf(msg -> currentTime - msg.timestamp > MESSAGE_LIFETIME);

        PoseStack poseStack = event.getMatrixStack();
        Font font = mc.font;
        for (PickupMessage msg : messages) {
            font.draw(poseStack, msg.text, x, y, msg.color);
            y -= 12;
        }
    }

    public static void addPickupMessage(String message, ChatFormatting color) {
        messages.add(new PickupMessage((TextComponent) new TextComponent(message).withStyle(color), System.currentTimeMillis(), color.getColor()));
    }

    private static class PickupMessage {
        final TextComponent text;
        final long timestamp;
        final int color;

        PickupMessage(TextComponent text, long timestamp, int color) {
            this.text = text;
            this.timestamp = timestamp;
            this.color = color;
        }
    }
}
