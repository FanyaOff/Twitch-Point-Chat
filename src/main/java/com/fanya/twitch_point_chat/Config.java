package com.fanya.twitch_point_chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    private static final String CONFIG_FILE_NAME = "tpc.json";
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);
    private static Config instance;

    private String token = "Token";
    private String rewardName = "Twitch Reward Name";
    private String username = "username";
    private boolean isAlertEnabled = true;
    private String customMessage = "§5[TWITCH]§r §b{username}§r§f: {message}";

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    public static Config loadConfig() {
        Gson gson = new Gson();
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return gson.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Config();
    }

    public void saveConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        saveConfig();
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
        saveConfig();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        saveConfig();
    }

    public boolean getIsAlertEnabled() {
        return isAlertEnabled;
    }

    public void setIsAlertEnabled(Boolean isAlertEnabled) {
        this.isAlertEnabled = isAlertEnabled;
        saveConfig();
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
        saveConfig();
    }
}
