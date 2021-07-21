package webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.json.simple.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class WebClientRunner implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebClientRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        WebClient client = WebClient.create("http://10.0.14.54:3000", Collections.singletonList(new JacksonJaxbJsonProvider()))
                .header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
                .header("X-User-Id", "ANrfMv9N4B7dHJGcg");

        Response authResponse = auth(client);
        saveResultToFile(authResponse, "auth.json");

// надо подружить два inputStream
        Response getAllUsersResponse = client.replacePath("/api/v1/users.list").get();
        saveResultToFile(getAllUsersResponse, "allUsers.json");
//        ArrayList<RocketUser> allUsers = getListOfUsers(getAllUsersResponse);

        Response createResponse = create(client);
        saveResultToFile(createResponse, "create.json");

        Response updateResponse = update(client);
        saveResultToFile(updateResponse, "update.json");

        Response deleteResponse = delete(client);
        saveResultToFile(deleteResponse, "delete.json");
    }

    private ArrayList<RocketUser> getListOfUsers(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = (InputStream) response.getEntity();

        Map map = mapper.readValue(stream, Map.class);
        JSONObject object = new JSONObject(map);

        ArrayList users = (ArrayList) object.get("users");
        ArrayList<RocketUser> rocketUsers = new ArrayList<>();

        for (Object o : users) {
            LinkedHashMap oo = (LinkedHashMap) o;

            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(oo);
            RocketUser user = gson.fromJson(jsonElement, RocketUser.class);
            rocketUsers.add(user);
        }
        return rocketUsers;
    }

    private Response auth(WebClient client) {
        client.replacePath("api/v1/login");

        Form form = new Form("user", "admin")
                .param("password", "projectRSIAM2015");

        return client.post(form);
    }

    private void saveResultToFile(Response response, String path) throws IOException {
        OutputStream writer = new FileOutputStream("c:/users/user-adm/responses/" + path);

        InputStream stream = (InputStream) response.getEntity();

        try {
            IOUtils.copy(stream, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
            stream.close();
        }
    }

    private Response create(WebClient client) {
        client.replacePath("/api/v1/users.create");

        Form form = new Form("name", "Igor")
                .param("email", "igor@mail.ru")
                .param("password", "Igor")
                .param("username", "IgorIgor");

        return client.form(form);
    }

    //Если после этого метода вызвать auth, то будет ошибка "No serializer found for class javax.ws.rs.core.Form" Problem with writing the data, class javax.ws.rs.core.Form, ContentType: application/json
    // видимо нужно как то избавиться от client.type()
    private Response delete(WebClient client) {
        client.replacePath("/api/v1/users.delete");

        ObjectNode jsonNode = new ObjectMapper().createObjectNode();
        jsonNode.put("username", "IgorIgor");

        return client.type(MediaType.APPLICATION_JSON).post(jsonNode);
    }

    private Response update(WebClient client) {
        String passHash = DigestUtils.sha256Hex("projectRSIAM2015");
        ObjectMapper mapper = new ObjectMapper();

        client.replacePath("/api/v1/users.update")
                .header("X-2fa-code", passHash)
                .header("X-2fa-method", "password")
                .type(MediaType.APPLICATION_JSON);

        // возможно проблемы с обновлением списка email, username, password
        ObjectNode childNode = mapper.createObjectNode();
        childNode.put("name", "IgorIgor");

        ObjectNode parentNode = mapper.createObjectNode();
        parentNode.put("userId", "X5EuzoKqthim27vWn");
        parentNode.set("data", childNode);

        return client.post(parentNode);
    }
}
