package com.pla.plamoneyget;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PlaMoneyGet.MOD_ID)
public class MobLootHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Mob, UUID> lastAttackers = new HashMap<>();

    // 🔹 Track last player who attacked the mob
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

        if (!(mob instanceof Enemy)) return;

        double health = entity.getMaxHealth();
        if (health < 20) return;

        ServerPlayer pPlayer = null;
        if (event.getSource().getEntity() instanceof ServerPlayer directPlayer) {
            pPlayer = directPlayer; // Direct kill
        } else if (lastAttackers.containsKey(mob)) {
            UUID playerUUID = lastAttackers.get(mob);
            pPlayer = mob.getServer().getPlayerList().getPlayer(playerUUID);
        }

        if (pPlayer == null) return;

        if (health >= 20) {
            int moneyAmount = (int) health / 10;
            String moneyCommand = "sdmshop add " + pPlayer.getScoreboardName() + " " + moneyAmount;

            CommandSourceStack source = pPlayer.createCommandSourceStack();
            source = new CommandSourceStack(
                    Objects.requireNonNull(source.getEntity()),
                    source.getPosition(),
                    source.getRotation(),
                    source.getLevel(),
                    4,
                    source.getTextName(),
                    source.getDisplayName(),
                    source.getServer(),
                    source.getEntity()
            );
            try {
                Objects.requireNonNull(pPlayer.getServer()).getCommands().getDispatcher().execute(moneyCommand, source);
//                LOGGER.info("Added " + moneyAmount + " for " + pPlayer.getScoreboardName());

                MoneyPickupOverlay.addPickupMessage("◎ Money x" + moneyAmount, ChatFormatting.GREEN);

            } catch (CommandSyntaxException e) {
                LOGGER.error("Failed to execute command {}, error {}", moneyCommand, e);
            }
        }
    }
}
