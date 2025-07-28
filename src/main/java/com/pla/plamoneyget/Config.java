package com.pla.plamoneyget;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

@EventBusSubscriber(modid = PlaMoneyGet.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Integer> DIVISOR;
    public static ModConfigSpec.ConfigValue<Integer> MIN_HEALTH;
    public static ModConfigSpec.ConfigValue<List<? extends String>> BLACK_LIST;
    public static ModConfigSpec.ConfigValue<List<? extends String>> WHITE_LIST;


    static {
        DIVISOR = BUILDER.comment(
                        "Divisor for money you got from mob.",
                        "Default: 10 => money = mob health / 10")
                .define("divisor", 10);
        MIN_HEALTH = BUILDER.comment(
                        "Minimum health of a hostile mob that can drop money.",
                        "Default: 20")
                .define("minHealth", 20);
        BLACK_LIST = BUILDER.comment(
                        "List of mob that couldn't drop money.")
                .defineList("blackList", List.of(), entry -> entry instanceof String);
        WHITE_LIST = BUILDER.comment(
                        "List of mob that should drop money",
                        "The mob is not necessary to be hostile (you can add villager here).")
                .defineList("whiteList", List.of(), entry -> entry instanceof String);
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
    }
}