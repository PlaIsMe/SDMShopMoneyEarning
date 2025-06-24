package com.pla.plamoneyget;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MoneyMessage {
    private final int amount;

    public MoneyMessage(int amount) {
        this.amount = amount;
    }

    public static void encode(MoneyMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.amount);
    }

    public static MoneyMessage decode(FriendlyByteBuf buf) {
        return new MoneyMessage(buf.readInt());
    }

    public static void handle(MoneyMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                MoneyPickupOverlay.addPickupMessage("◎ Money x" + msg.amount, ChatFormatting.GREEN);
            })
        );
        ctx.get().setPacketHandled(true);
    }
}
