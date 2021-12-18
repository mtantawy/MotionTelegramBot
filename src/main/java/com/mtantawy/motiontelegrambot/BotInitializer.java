package com.mtantawy.motiontelegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class BotInitializer {

    public BotInitializer(Bot bot, Environment environment) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            bot.sendBootMessage();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("an error occurred while initializing the bot", e);
        }
    }
}
