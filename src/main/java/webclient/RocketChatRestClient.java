package webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.identityconnectors.framework.common.objects.Uid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class RocketChatRestClient implements CommandLineRunner {

    @Autowired
    InputStreamService inputStreamService;

    public static void main(String[] args) {
        SpringApplication.run(RocketChatRestClient.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        WebClient client = WebClient.create("http://10.0.14.54:3000", Collections.singletonList(new JacksonJaxbJsonProvider()))
                .header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
                .header("X-User-Id", "ANrfMv9N4B7dHJGcg");

        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry entry : client.getHeaders().entrySet()) {
            System.out.println("Key=" + entry.getKey() + "   Value=" + entry.getValue());
        }

        Response authResponse = auth(client);
        InputStream authInputStream = (InputStream) authResponse.getEntity();

        Response getAllUsersResponse = client.replacePath("/api/v1/users.list").get();
        InputStream allUsersInputStream = (InputStream) getAllUsersResponse.getEntity();

        Response createResponse = create(client);
        InputStream createInputStream = (InputStream) createResponse.getEntity();

        Response updateResponse = update(client, mapper);
        InputStream updateInputStream = (InputStream) updateResponse.getEntity();

        Response deleteResponse = delete(client, mapper);
        InputStream deleteInputStream = (InputStream) deleteResponse.getEntity();

        ArrayList<RocketChatUser> allUsers = inputStreamService.getListOfUsers(allUsersInputStream, mapper);
        Uid authUid = inputStreamService.getUid(authInputStream, mapper, "data", "userId");
        Uid createUid = inputStreamService.getUid(createInputStream, mapper, "user", "_id");
        Uid updateUid = inputStreamService.getUid(updateInputStream, mapper, "user", "_id");

        inputStreamService.saveResultToFile(allUsersInputStream, "allUsers.json");
        inputStreamService.saveResultToFile(createInputStream, "create.json");
        inputStreamService.saveResultToFile(updateInputStream, "update.json");
        inputStreamService.saveResultToFile(deleteInputStream, "delete.json");
        inputStreamService.saveResultToFile(authInputStream, "auth.json");
    }

    private Response auth(WebClient client) {
        client.replacePath("api/v1/login");

        Form form = new Form("user", "admin")
                .param("password", "projectRSIAM2015");

        return client.post(form);
    }

    private Response create(WebClient client) {
        client.replacePath("/api/v1/users.create");

        Form form = new Form("name", "Igor")
                .param("email", "igor@mail.ru")
                .param("password", "Igor")
                .param("username", "IgorIgor");

        return client.post(form);
    }

    //Если после этого метода вызвать auth, то будет ошибка "No serializer found for class javax.ws.rs.core.Form" Problem with writing the data, class javax.ws.rs.core.Form, ContentType: application/json
    // видимо нужно как то избавиться от client.type()
    private Response delete(WebClient client, ObjectMapper mapper) {
        client.replacePath("/api/v1/users.delete");

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("username", "IgorIgor");

        return client.type(MediaType.APPLICATION_JSON).post(objectNode);
    }

    private Response update(WebClient client, ObjectMapper mapper) {
        String passHash = DigestUtils.sha256Hex("projectRSIAM2015");

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