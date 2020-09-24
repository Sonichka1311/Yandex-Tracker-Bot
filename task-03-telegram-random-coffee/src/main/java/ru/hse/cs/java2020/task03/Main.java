package ru.hse.cs.java2020.task03;

import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            Bot bot = new Bot(botOptions);
            Database db = new Database();
            Runtime.getRuntime().addShutdownHook(new Thread(db::close));
            bot.init(db);
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            System.out.println("Error:" + e);
        }
    }
}
