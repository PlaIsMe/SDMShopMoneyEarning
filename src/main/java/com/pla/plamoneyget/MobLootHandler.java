package com.pla.plamoneyget;

import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.sixik.sdmeconomy.economyData.CurrencyPlayerData;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = PlaMoneyGet.MOD_ID)
public class MobLootHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Mob, UUID> lastAttackers = new HashMap<>();

    @SubscribeEvent
    public static void onMobHurt(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        lastAttackers.put(mob, player.getUUID());
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!(entity instanceof Mob mob)) return;

        if (!(mob instanceof Enemy)) return;

        double health = entity.getMaxHealth();
        if (health < 20) return;

        ServerPlayer pPlayer = null;
        if (event.getSource().getEntity() instanceof ServerPlayer directPlayer) {
            pPlayer = directPlayer;
        } else if (lastAttackers.containsKey(mob)) {
            UUID playerUUID = lastAttackers.get(mob);
            pPlayer = mob.getServer().getPlayerList().getPlayer(playerUUID);
        }

        if (pPlayer == null) return;

        if (health >= 20) {
            int moneyAmount = (int) health / 10;
            ErrorCodes result = CurrencyPlayerData.SERVER.addCurrencyValue(pPlayer, "sdmcoin", moneyAmount);
            switch (result) {
                case SUCCESS -> {
                    PacketDistributor.sendToPlayer(pPlayer, new Data(String.valueOf(moneyAmount)));
                }
                case NOT_FOUND -> LOGGER.error("§cCurrency sdmcoin not found.");
                case FAIL -> LOGGER.error("§cFailed to add currency due to internal error.");
                default -> LOGGER.error("§cUnknown error: " + result);
            }

        }
    }
}
