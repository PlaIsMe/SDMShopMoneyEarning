package com.pla.plamoneyget;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Integer> DIVISOR;
    public static ForgeConfigSpec.ConfigValue<Integer> MIN_HEALTH;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLACK_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITE_LIST;

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
}