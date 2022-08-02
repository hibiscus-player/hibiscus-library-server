package me.mrgazdag.hibiscus.library.coreapi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestCoreApi implements CoreApi {
    private static final String PROFILE_ID_HEADER = "X-Profile-ID";
    private static final String SERVER_KEY_HEADER = "X-Server-Key";

    private final String baseUrl;

    public RestCoreApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public ProfileData getProfile(String profileId, String serverKey) throws IOException {
        URL url = null;
        try {
            url = new URL(baseUrl + "/api/v1/validateProfile");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(PROFILE_ID_HEADER, profileId);
        con.setRequestProperty(SERVER_KEY_HEADER, serverKey);
        int status = con.getResponseCode();
        // Read response
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            sb.append(br.readLine());
        }
        br.close();
        JSONObject obj = new JSONObject(sb.toString());
        boolean valid = obj.getBoolean("valid");

        if (status == HttpURLConnection.HTTP_OK && valid) {
            return new ProfileData(obj.getString("profileId"), obj.getString("displayName"), obj.getString("photoUrl"));
        } else {
            return null;
        }
    }
}
