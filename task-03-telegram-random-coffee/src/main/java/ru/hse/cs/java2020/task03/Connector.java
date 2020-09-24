package ru.hse.cs.java2020.task03;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Connector {
    private final String trackerUrl = "https://api.tracker.yandex.net";
    private final HttpClient client;

    private String oauthToken;
    private String companyId;

    Connector(String oauthToken, String companyId) {
        this.oauthToken = oauthToken;
        this.companyId = companyId;

        client = HttpClient.newHttpClient();
    }

    public JSONObject create(String task) {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(trackerUrl + "/v2/issues/"))
            .header("Authorization", "OAuth " + oauthToken)
            .header("X-Org-Id", companyId)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(task))
            .build();

        try {
            return new JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body());
        } catch (Exception exp) {
            System.out.printf("Exception while send request for method create to ya.tracker: %s\n", exp.toString());
            return new JSONObject();
        }
    }

    public JSONObject me() {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(trackerUrl + "/v2/myself/"))
            .header("Authorization", "OAuth " + oauthToken)
            .header("X-Org-Id", companyId)
            .build();

        try {
            return new JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body());
        } catch (Exception exp) {
            System.out.printf("Exception while send request for method me to ya.tracker: %s\n", exp.toString());
            return new JSONObject();
        }
    }

    public JSONArray getAll(String query) {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(trackerUrl + "/v2/issues/_search/?order=+"))
            .header("Authorization", "OAuth " + oauthToken)
            .header("X-Org-Id", companyId)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(query))
            .build();

        try {
            String resp = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            return new JSONArray(resp);
        } catch (Exception exp) {
            System.out.printf("Exception while send request for method getAll to ya.tracker: %s\n", exp.toString());
            return new JSONArray();
        }
    }

    public JSONArray getComments(String key) {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(trackerUrl + "/v2/issues/" + key + "/comments"))
            .header("Authorization", "OAuth " + oauthToken)
            .header("X-Org-Id", companyId)
            .header("Content-Type", "application/json")
            .GET()
            .build();

        try {
            String resp = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            return new JSONArray(resp);
        } catch (Exception exp) {
            System.out.printf("Exception while send request for method getAll to ya.tracker: %s\n", exp.toString());
            return new JSONArray();
        }
    }

    public JSONObject getPerson(String string) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(string))
                .header("Authorization", "OAuth " + oauthToken)
                .header("X-Org-Id", companyId)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            String resp = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            return new JSONObject(resp);
        } catch (Exception exp) {
            System.out.printf("Exception while send request for method getAll to ya.tracker: %s\n", exp.toString());
            return new JSONObject();
        }
    }
}
