package com.cityquest.cityquest_backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class AuthIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    public void registerLoginAndAccessProtected() throws Exception {
        String base = "http://localhost:" + port;

        String payload = "{\"username\":\"inttest\",\"password\":\"pwd\"}";

        // register
        HttpResult reg = postJson(base + "/auth/register", payload);
        if (!(reg.status >= 200 && reg.status < 300)) {
            System.out.println("REGISTER STATUS: " + reg.status);
            System.out.println("REGISTER BODY: " + reg.body);
        }
        assertThat(reg.status).isBetween(200, 299);

        // login
        HttpResult login = postJson(base + "/auth/login", payload);
        assertThat(login.status).isBetween(200, 299);
        JsonNode node = JsonMapper.builder().build().readTree(login.body);
        String token = node.get("token").asText();
        assertThat(token).isNotEmpty();

        // call protected
        HttpResult hello = getWithAuth(base + "/api/hello", token);
        assertThat(hello.status).isBetween(200, 299);
        assertThat(hello.body).contains("inttest");
    }

    private HttpResult postJson(String urlStr, String json) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }
        int status = con.getResponseCode();
        InputStream is = (status >= 200 && status < 400) ? con.getInputStream() : con.getErrorStream();
        String body = "";
        if (is != null) {
            body = new String(is.readAllBytes());
        }
        con.disconnect();
        return new HttpResult(status, body);
    }

    private HttpResult getWithAuth(String urlStr, String token) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        int status = con.getResponseCode();
        InputStream is = (status >= 200 && status < 400) ? con.getInputStream() : con.getErrorStream();
        String body = new String(is.readAllBytes());
        con.disconnect();
        return new HttpResult(status, body);
    }

    private static class HttpResult {
        int status;
        String body;

        HttpResult(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }
}
