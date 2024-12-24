package com.fanya.twitch_point_chat;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.Objects;

public class TwitchAPI {
    public static boolean isAlertEnabled = true;
    public static void subscribeToEvents(TwitchClient client, MinecraftClient mc){
        client.getEventManager().onEvent(RewardRedeemedEvent.class, rewardRedeemedEvent -> {
            String rewardName = TwitchPointsChat.CONFIG.getRewardName();
            TwitchPointsChat.LOGGER.info(rewardRedeemedEvent.getRedemption().getReward().getTitle() + " | " + rewardName);
            if (Objects.equals(rewardRedeemedEvent.getRedemption().getReward().getTitle(), rewardName)){
                TwitchPointsChat.LOGGER.info("true");
                if (mc.player != null) {
                    String text = rewardRedeemedEvent.getRedemption().getUserInput();
                    String username = rewardRedeemedEvent.getRedemption().getUser().getDisplayName();
                    String customMessage = TwitchPointsChat.CONFIG.getCustomMessage()
                            .replace("{username}", username)
                            .replace("{message}", text);

                    if (TwitchPointsChat.CONFIG.getIsAlertEnabled()) {
                        mc.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 10F, 0F);
                    }
                    mc.player.sendMessage(Text.literal(customMessage), false);
                }
            }
        });
    }

    public static String getTwitchId(TwitchClient twitchClient){
         return twitchClient.getHelix().getUsers(TwitchPointsChat.CONFIG.getToken(), null, Collections.singletonList(TwitchPointsChat.CONFIG.getUsername())).execute().getUsers().get(0).getId();
    }
}
