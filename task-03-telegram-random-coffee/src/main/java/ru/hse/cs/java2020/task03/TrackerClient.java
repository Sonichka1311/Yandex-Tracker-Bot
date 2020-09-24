package ru.hse.cs.java2020.task03;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerClient {
    private static final String TRACKER_URL = "https://tracker.yandex.ru/";
    private static final String STAFF_URL = "https://staff.yandex.ru/";

    private String oauthToken;
    private String companyId;

    TrackerClient(String oauthToken, String companyId) {
        this.oauthToken = oauthToken;
        this.companyId = companyId;
    }

    public String createTask(String summary, String queue, String description, boolean assigner) {
        Connector connector = new Connector(oauthToken, companyId);

        JSONObject task = new JSONObject();
        task.put("summary", summary);
        task.put("queue", queue);
        task.put("description", description);
        if (assigner) {
            JSONObject resp = connector.me();
            int id;
            try {
                id = resp.getInt("uid");
            } catch (JSONException exp) {
                System.out.printf("Something were wrong in TrackerClient->createTasks->resp.getString: %s\n", exp.toString());
                return null;
            }
             task.put("assignee", id);
        }

        JSONObject resp = connector.create(task.toString());
        try {
            return resp.getString("key");
        } catch (JSONException exp) {
            System.out.printf("Something were wrong in TrackerClient->createTask->resp.getString: %s\n", exp.toString());
            return null;
        }
    }

    public String getTasks(Integer start, Integer stop) {
        Connector connector = new Connector(oauthToken, companyId);
        JSONObject resp = connector.me();
        int id;
        try {
            id = resp.getInt("uid");
        } catch (JSONException exp) {
            System.out.printf("Something were wrong in TrackerClient->getTasks->resp.getString%s\n", exp.toString());
            return null;
        }

        JSONObject req = new JSONObject().put("filter", new JSONObject().put("assignee", id));

        JSONArray respArray = connector.getAll(req.toString());
        int arraySize = respArray.length();
        if (arraySize == 0) {
            return "Задач не найдено. \nЧтобы создать задачу, нажми /create ";
        }
        try {
            StringBuilder res = new StringBuilder();
            for (int i = start; i < Math.min(arraySize, stop); ++i) {
                JSONObject task = respArray.getJSONObject(i);
                res
                    .append(i + 1)
                    .append(". *")
                    .append(task.getString("key"))
                    .append("*: [")
                    .append(task.getString("summary"))
                    .append("](")
                    .append(TRACKER_URL)
                    .append(task.getString("key"))
                    .append(") /task\\_")
                    .append(task.getString("key").replace("-", "\\_"))
                    .append("\n");
            }
            res.append("\n");
            if (stop < arraySize) {
                res.append("Нажми /next для перехода на следующую страницу\n");
            }
            res.append("Нажми /end для завершения просмотра\n");
            System.out.println(res.toString());
            return res.toString();
        } catch (JSONException exp) {
            System.out.printf("Something were wrong in TrackerClient->createTask: %s\n", exp.toString());
            return null;
        }
    }

    public String getTask(String key) {
        Connector connector = new Connector(oauthToken, companyId);

        JSONObject req = new JSONObject().put("filter", new JSONObject().put("key", key));

        JSONArray respArray = connector.getAll(req.toString());
        System.out.println(respArray);
        try {
            if (respArray.length() == 1) {
                StringBuilder res = new StringBuilder();
                JSONObject task = respArray.getJSONObject(0);
                JSONObject authorInfo = connector.getPerson(task.getJSONObject("createdBy").getString("self"));
                res
                    .append("*Задача*: ")
                    .append("[")
                    .append(task.getString("summary"))
                    .append("](")
                    .append(TRACKER_URL)
                    .append(task.getString("key"))
                    .append(")\n")
                    .append("*Описание*: ")
                    .append(task.getString("description"))
                    .append("\n")
                    .append("*Автор*: [")
                    .append(authorInfo.getString("display"))
                    .append("](")
                    .append(STAFF_URL)
                    .append(authorInfo.getString("login"))
                    .append("?org_id=")
                    .append(companyId)
                    .append(")\n")
                    .append("*Исполнитель*: ");
                try {
                    JSONObject assignee = task.getJSONObject("assignee");
                    JSONObject assigneeInfo = connector.getPerson(assignee.getString("self"));
                    res
                        .append("[")
                        .append(assigneeInfo.getString("display"))
                        .append("](")
                        .append(STAFF_URL)
                        .append(assigneeInfo.getString("login"))
                        .append("?org_id=")
                        .append(companyId)
                        .append(")\n");
                } catch (JSONException e) {
                    res.append("--\n");
                }
                res.append("*Наблюдатели*: ");
                try {
                    JSONArray followers = task.getJSONArray("followers");
                    for (int i = 0; i < followers.length(); ++i) {
                        JSONObject follower = followers.getJSONObject(i);
                        System.out.println(follower);
                        JSONObject followerInfo = connector.getPerson(follower.getString("self"));
                        System.out.println(followerInfo);
                        res
                            .append("[")
                            .append(followerInfo.getString("display"))
                            .append("](")
                            .append(STAFF_URL)
                            .append(followerInfo.getString("login"))
                            .append("?org_id=")
                            .append(companyId);
                        if (i != followers.length() - 1) {
                            res.append("), ");
                        } else {
                            res.append(")\n");
                        }
                    }
                } catch (JSONException e) {
                    res.append("--\n");
                }
                res
                    .append("*Комментарии*: /comments\\_")
                    .append(task.getString("key").replace("-", "\\_"));
                return res.toString();
            } else {
                return null;
            }
        } catch (JSONException exp) {
            System.out.printf("Something were wrong in TrackerClient->createTask: %s\n", exp.toString());
            return null;
        }
    }

    public String getComments(String task, int start, int stop) {
        Connector connector = new Connector(oauthToken, companyId);

        JSONArray comments = connector.getComments(task);
        StringBuilder res = new StringBuilder();

        try {
            int arraySize = comments.length();
            if (arraySize == 0) {
                throw new Exception("Empty comments");
            }
            for (int i = start; i < Math.min(arraySize, stop); ++i) {
                JSONObject comment = comments.getJSONObject(i);
                JSONObject authorInfo = connector.getPerson(comment.getJSONObject("createdBy").getString("self"));
                res
                    .append("[")
                    .append(authorInfo.getString("display"))
                    .append("](")
                    .append(STAFF_URL)
                    .append(authorInfo.getString("login"))
                    .append("?org_id=")
                    .append(companyId)
                    .append("):\n")
                    .append(comment.getString("text"))
                    .append("\n\n");
            }
            if (stop < arraySize) {
                res.append("Нажми /next для перехода на следующую страницу\n");
            }
            res.append("Нажми /end для завершения просмотра\n");
        } catch (Exception e) {
            res
                .append("К задаче ")
                .append(task)
                .append(" нет комментариев\n");
        }
        return res.toString();
    }
}
