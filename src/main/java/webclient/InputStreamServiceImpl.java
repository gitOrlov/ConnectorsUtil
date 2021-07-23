package webclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.cxf.helpers.IOUtils;
import org.identityconnectors.framework.common.objects.Uid;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class InputStreamServiceImpl implements InputStreamService {
    public Uid getUid(InputStream inputStream, ObjectMapper mapper, String first, String second) throws IOException {
        JsonNode node = mapper.readTree(inputStream);
        String userId = node.get(first).get(second).textValue();
        return new Uid(userId);
    }

    public ArrayList<RocketChatUser> getListOfUsers(InputStream inputStream, ObjectMapper mapper) throws IOException {
        Map map = mapper.readValue(inputStream, Map.class);
        JSONObject object = new JSONObject(map);

        ArrayList users = (ArrayList) object.get("users");
        ArrayList<RocketChatUser> rocketChatUsers = new ArrayList<>();

        for (Object o : users) {
            LinkedHashMap oo = (LinkedHashMap) o;

            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(oo);
            RocketChatUser user = gson.fromJson(jsonElement, RocketChatUser.class);
            rocketChatUsers.add(user);
        }
        return rocketChatUsers;
    }

    public void saveResultToFile(InputStream inputStream, String path) throws IOException {
        OutputStream writer = new FileOutputStream("c:/users/user-adm/responses/" + path);

        try {
            IOUtils.copy(inputStream, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
            inputStream.close();
        }
    }
}
