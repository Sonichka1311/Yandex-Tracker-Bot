package ru.hse.cs.java2020.task03;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private Database database;

    Bot(DefaultBotOptions options) { }

    public void init(Database database) {
        this.database = database;
    }

    @Override
    public String getBotUsername() {
        return "ya_traker_1311_bot";
    }

    @Override
    public String getBotToken() {
        return "1228554069:AAHaehzO0cOR2dBNaCFz2rpL8qeZXM0v6HA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long userId = update.getMessage().getChatId();
        String message;
        try {
            message = update.getMessage().getText();
            if (message.isEmpty()) {
                throw new Exception("Empty message");
            }
        } catch (Exception exp) {
            sendMessage(userId.toString(), Constants.NOT_UNDERSTAND);
            return;
        }

        State state;
        if (message.equals("/start") || message.equals("/end")) {
            state = (message.equals("/start")) ? new StateStart(userId, this) : new StateEnd(userId, this);
            state.action(message);
            return;
        }

        if (!database.isAuthorized(update.getMessage().getChatId())) {
            String st = database.getUserState(userId);
            if (!st.equals("waitToken") && !st.equals("waitCompany")) {
                state = new StateUnauthorized(userId, this);
                state.action(message);
                return;
            }
        }

        if (message.equals("/help")) {
            state = new StateHelp(userId, this);
            state.action(message);
            return;
        }

        String userState = database.getUserState(userId);
        switch (userState) {
            case "main":
                state = new StateMain(userId, this);
                break;
            case "waitToken": // wait for oauth-token
                new StateGetToken(userId, this).action(message);
                return;
            case "waitCompany": // wait for company id
                new StateGetCompanyId(userId, this).action(message);
                return;
            case "waitQueue": // wait for queue
                state = new StateGetQueue(userId, this);
                break;
            case "waitSummary": // wait for summary
                state = new StateGetSummary(userId, this);
                break;
            case "waitDescription": // wait to description
                state = new StateGetDescription(userId, this);
                break;
            case "waitAssignee": // wait to assignee
                state = new StateGetAssignee(userId, this);
                break;
            case "waitPage": // wait for number of tasks in one message
                state = new StateGetPageNumber(userId, this);
                break;
            case "waitNext": // wait for /next
                if (message.equals("/next")) {
                    message = database.getPage(userId).toString();
                    state = new StateGetTasksByPage(userId, this);
                } else {
                    database.updateUserState(userId, "main");
                    onUpdateReceived(update);
                    return;
                }
                break;
            case "waitTask": // wait for task key
                state = new StateGetTaskWithKey(userId, this);
                break;
            case "waitTaskComment": // wait for task key
                message = message.replace('_', '-');
                state = new StateGetCommentTaskKey(userId, this);
                break;
            case "waitPageComment": // wait for number
                state = new StateGetCommentPageNumber(userId, this);
                break;
            case "waitNextComment": // wait for /next
                if (message.equals("/next")) {
                    message = database.getPage(userId).toString();
                    state = new StateGetCommentsByPage(userId, this);
                } else {
                    database.updateUserState(userId, "main");
                    onUpdateReceived(update);
                    return;
                }
                break;
            default:
                database.updateUserState(userId, "main");
                sendMessage(userId.toString(), Constants.BUG);
                state = new StateRemind(userId, this);
        }
        state.action(message);

    }

    public synchronized void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode(ParseMode.MARKDOWN);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Can't send message:" + e.toString());
        }
    }

    public Database getDatabase() {
        return database;
    }
}
