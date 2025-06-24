package com.pla.plamoneyget;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.sixik.sdmshoprework.SDMShopR;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PlaMoneyGet.MOD_ID)
public class MobLootHandler {
    private static final Map<Mob, UUID> lastAttackers = new HashMap<>();

    @SubscribeEvent
    public static void onMobHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        lastAttackers.put(mob, player.getUUID());
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!(entity instanceof Mob mob)) return;

        if (mob.level().isClientSide) return;

        if (!(mob instanceof Enemy)) return;

        double health = entity.getMaxHealth();
        if (health < 20) return;

        ServerPlayer pPlayer;
        if (event.getSource().getEntity() instanceof ServerPlayer directPlayer) {
            pPlayer = directPlayer;
        } else if (lastAttackers.containsKey(mob)) {
            UUID playerUUID = lastAttackers.get(mob);
            pPlayer = mob.getServer().getPlayerList().getPlayer(playerUUID);
        } else {
            pPlayer = null;
        }

        if (pPlayer == null) return;

        if (health >= 20) {
            int moneyAmount = (int) health / 10;
            SDMShopR.addMoney(pPlayer, moneyAmount);
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> pPlayer), new MoneyMessage(moneyAmount));
        }
    }
}
