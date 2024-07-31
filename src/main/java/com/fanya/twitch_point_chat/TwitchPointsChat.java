package com.fanya.twitch_point_chat;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TwitchPointsChat implements ModInitializer {
    public static OAuth2Credential credential;
    public static TwitchClient twitchClient;
    public static final Logger LOGGER = LoggerFactory.getLogger("twitch_point_chat");
    public static Config CONFIG;
    public static String channelId;
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing configs");
        CONFIG = Config.getInstance();
        credential = new OAuth2Credential("twitch", CONFIG.getToken());
        try {
            twitchClient = TwitchClientBuilder.builder()
                    .withEnablePubSub(true)
                    .withEnableHelix(true)
                    .withChatAccount(credential)
                    .build();
            channelId = TwitchAPI.getTwitchId(twitchClient);
            LOGGER.info("Channel ID: {}", channelId);
            twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, channelId);
            TwitchAPI.subscribeToEvents(twitchClient, mc);
        } catch (Exception exception){
            LOGGER.error("ERROR WHILE INIT TWITCH API (IGNORE IT IF YOU LAUNCH MOD FOR FIRST TIME): {}", exception.getMessage());
        }
    }
}
