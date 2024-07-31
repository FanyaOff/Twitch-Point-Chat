package com.fanya.twitch_point_chat;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TpcModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Twitch API Config"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory twitchapi = builder.getOrCreateCategory(Text.literal("Setup"));
            SubCategoryBuilder twitchApiSubCategory = entryBuilder.startSubCategory(Text.literal("Don't open this on stream"));
            twitchApiSubCategory.setExpanded(false);
            twitchApiSubCategory.add(entryBuilder.startStrField(Text.literal("Token"), TwitchPointsChat.CONFIG.getToken())
                    .setDefaultValue(TwitchPointsChat.CONFIG.getToken())
                    .setSaveConsumer(newValue -> TwitchPointsChat.CONFIG.setToken(newValue))
                    .build()
            );
            twitchApiSubCategory.add(entryBuilder.startStrField(Text.literal("Twitch Username"), TwitchPointsChat.CONFIG.getUsername())
                    .setDefaultValue(TwitchPointsChat.CONFIG.getUsername())
                    .setSaveConsumer(newValue -> TwitchPointsChat.CONFIG.setUsername(newValue))
                    .build()
            );
            twitchapi.addEntry(entryBuilder.startStrField(Text.literal("Reward name"), TwitchPointsChat.CONFIG.getRewardName())
                    .setDefaultValue(TwitchPointsChat.CONFIG.getRewardName())
                    .setSaveConsumer(newValue -> TwitchPointsChat.CONFIG.setRewardName(newValue))
                    .build()
            );

            twitchapi.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Sound Alert"), TwitchAPI.isAlertEnabled)
                    .setDefaultValue(TwitchPointsChat.CONFIG.getIsAlertEnabled())
                    .setSaveConsumer(newValue -> TwitchPointsChat.CONFIG.setIsAlertEnabled(newValue))
                    .build()
            );

            twitchapi.addEntry(twitchApiSubCategory.build());

            builder.setSavingRunnable(() -> {
                try {
                    if (TwitchPointsChat.twitchClient != null) {
                        TwitchPointsChat.LOGGER.info("Closing Twitch API");
                        TwitchPointsChat.twitchClient.close();
                    }
                    TwitchPointsChat.LOGGER.info("Reinitializing Twitch API");
                    TwitchPointsChat.twitchClient = TwitchClientBuilder.builder()
                            .withEnablePubSub(true)
                            .withEnableHelix(true)
                            .withChatAccount(TwitchPointsChat.credential)
                            .build();

                    TwitchPointsChat.channelId = TwitchAPI.getTwitchId(TwitchPointsChat.twitchClient);
                    TwitchPointsChat.LOGGER.info("Channel ID: {}", TwitchPointsChat.channelId);
                    TwitchPointsChat.twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(TwitchPointsChat.credential, TwitchPointsChat.channelId);
                    TwitchAPI.subscribeToEvents(TwitchPointsChat.twitchClient, TwitchPointsChat.mc);
                } catch (Exception exception){
                    TwitchPointsChat.LOGGER.error("ERROR WHILE INIT TWITCH API (IGNORE IT IF YOU LAUNCH MOD FOR FIRST TIME): {}", exception.getMessage());
                }
            });

            return builder.build();
        };
    }
}
