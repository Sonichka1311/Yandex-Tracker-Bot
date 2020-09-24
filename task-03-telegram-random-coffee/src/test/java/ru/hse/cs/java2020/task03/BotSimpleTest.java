package ru.hse.cs.java2020.task03;

import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import static org.junit.Assert.*;

public class BotSimpleTest {
    String firstMessage = "";
    String secondMessage = "";
    String id;

    private class TestBot extends Bot {
        TestBot(DefaultBotOptions options) {
            super(options);
        }

        @Override
        public synchronized void sendMessage(String chatId, String text) {
            id = chatId;
            if (firstMessage.isEmpty()) {
                firstMessage = text;
            } else {
                secondMessage = text;
            }
        }
    }

    private Bot init(Database database) {
        Bot bot = new TestBot(null);
        bot.init(database);
        return bot;
    }

    private void testHelpFish(String state) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/help");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.getUserState(1L)).thenReturn(state);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.HELP, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    private void testWrongCountFish(String message) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn(message);

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitPage");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.WRONG_COUNT, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    private void testCommentWrongCountFish(String message) {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn(message);

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitPageComment");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.WRONG_COUNT, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testEmpty() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Bot bot = init(null);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NOT_UNDERSTAND, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testNotAuthorize() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("something");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.getUserState(1L)).thenReturn("main");
        Mockito.when(database.isAuthorized(1L)).thenReturn(false);

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NOT_AUTH, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testHelp() {
        testHelpFish("main");
        testHelpFish("waitToken");
    }


    @Test
    public void testStart() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/start");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.START, firstMessage);
        assertEquals(Constants.TOKEN, secondMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitToken");

        firstMessage = "";
        secondMessage = "";
    }

    @Test
    public void testSetToken() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("Some Token");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.getUserState(1L)).thenReturn("waitToken");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.COMPANY, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitCompany");

        firstMessage = "";
    }

    @Test
    public void testAuth() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("1234567");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.getUserState(1L)).thenReturn("waitCompany");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.WELCOME, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");
        Mockito.verify(database, Mockito.times(1)).setCompanyId(1L, "1234567");
        Mockito.verify(database, Mockito.times(1)).authorizeUser(1L);

        firstMessage = "";
    }

    @Test
    public void testCreateTask() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/create");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.QUEUE, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitQueue");

        firstMessage = "";
    }

    @Test
    public void testSetQueue() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("QUEUE");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitQueue");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.SUMMARY, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitSummary");
        Mockito.verify(database, Mockito.times(1)).setQueue(1L, "QUEUE");

        firstMessage = "";
    }

    @Test
    public void testSetSummary() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("task name");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitSummary");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.DESCRIPTION, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitDescription");
        Mockito.verify(database, Mockito.times(1)).setSummary(1L, "task name");

        firstMessage = "";
    }

    @Test
    public void testSetDescription() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("some task description");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitDescription");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.ASSIGNEE, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitAssignee");
        Mockito.verify(database, Mockito.times(1)).setDescription(1L, "some task description");

        firstMessage = "";
    }

    @Test
    public void testSetWrongAssignee() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("не знаю");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitAssignee");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.YES_OR_NO, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testGetTasks() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/tasks");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.TASKS_PAGE, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitPage");

        firstMessage = "";
    }

    @Test
    public void testSetWrongPage() {
        testWrongCountFish("-1");
        testWrongCountFish("abc");
        testWrongCountFish("26");
    }

    @Test
    public void testGetNoNextTasks() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("какая-то ерунда");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitNext", "main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NOT_UNDERSTAND, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    @Test
    public void testGetTask() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/task");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.WHICH_TASK, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitTask");

        firstMessage = "";
    }

    @Test
    public void testGetTaskWrong() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/task_wrong");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.SMTH_WRONG, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testGetComments() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/comments");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.WHICH_TASK_COM, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitTaskComment");

        firstMessage = "";
    }

    @Test
    public void testGetCommentsByName() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/comments_QUEUE_1234");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.COMMENT_PAGE, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitPageComment");
        Mockito.verify(database, Mockito.times(1)).setTask(1L, "QUEUE-1234");

        firstMessage = "";
    }

    @Test
    public void testGetCommentsWrong() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/comments_wrong");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.SMTH_WRONG, firstMessage);
        assertEquals("1", id);

        firstMessage = "";
    }

    @Test
    public void testSetCommentsTaskName() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("QUEUE_1234");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitTaskComment");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.COMMENT_PAGE, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "waitPageComment");
        Mockito.verify(database, Mockito.times(1)).setTask(1L, "QUEUE-1234");

        firstMessage = "";
    }

    @Test
    public void testSetCommentWrongPage() {
        testCommentWrongCountFish("-1");
        testCommentWrongCountFish("abc");
        testCommentWrongCountFish("26");
    }

    @Test
    public void testNoNextComment() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("какая-то ерунда");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitNextComment", "main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NOT_UNDERSTAND, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    @Test
    public void testEndNextComment() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("/end");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("waitNextComment", "main");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.NEED_HELP, firstMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }

    @Test
    public void testWrongState() {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getChatId()).thenReturn(1L);
        Mockito.when(msg.getText()).thenReturn("какая-то ерунда");

        Update update = Mockito.mock(Update.class);
        Mockito.when(update.getMessage()).thenReturn(msg);

        Database database = Mockito.mock(Database.class);
        Mockito.when(database.isAuthorized(1L)).thenReturn(true);
        Mockito.when(database.getUserState(1L)).thenReturn("wrongState");

        Bot bot = init(database);
        bot.onUpdateReceived(update);

        assertEquals(Constants.BUG, firstMessage);
        assertEquals(Constants.NEED_HELP, secondMessage);
        assertEquals("1", id);

        Mockito.verify(database, Mockito.times(1)).updateUserState(1L, "main");

        firstMessage = "";
    }
}
