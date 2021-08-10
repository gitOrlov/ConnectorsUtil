package webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
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
        WebClient client = WebClient.create("http://10.0.14.54:3000")
                .header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
                .header("X-User-Id", "ANrfMv9N4B7dHJGcg");

        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry entry : client.getHeaders().entrySet()) {
            System.out.println("Key=" + entry.getKey() + "   Value=" + entry.getValue());
        }

        Response authResponse = auth(client);
        InputStream authInputStream = (InputStream) authResponse.getEntity();

        Response oneUserResponse = client.replacePath("/api/v1/users.info").query("userId", "jGPfuzGRTevZSp4Ce").get();//Адриан
        InputStream oneUserInputStream = (InputStream) oneUserResponse.getEntity();

        Response getAllUsersResponse = client.replacePath("/api/v1/users.list").get();
        InputStream allUsersInputStream = (InputStream) getAllUsersResponse.getEntity();

        Response createResponse = create(client, mapper);
        InputStream createInputStream = (InputStream) createResponse.getEntity();

        Response updateResponse = update(client, mapper);
        InputStream updateInputStream = (InputStream) updateResponse.getEntity();

        Response deleteResponse = delete(client);
        InputStream deleteInputStream = (InputStream) deleteResponse.getEntity();

//        ArrayList<RocketChatUser> allUsers = inputStreamService.getListOfUsers(allUsersInputStream, mapper);
//        Uid authUid = inputStreamService.getUid(authInputStream, mapper, "data", "userId");
//        Uid createUid = inputStreamService.getUid(createInputStream, mapper, "user", "_id");
//        Uid updateUid = inputStreamService.getUid(updateInputStream, mapper, "user", "_id");
//        Uid oneUserUid = inputStreamService.getUid(oneUserInputStream, mapper, "user", "_id");

        inputStreamService.saveResultToFile(allUsersInputStream, "allUsers.json");
        inputStreamService.saveResultToFile(createInputStream, "create.json");
        inputStreamService.saveResultToFile(updateInputStream, "update.json");
        inputStreamService.saveResultToFile(deleteInputStream, "delete.json");
        inputStreamService.saveResultToFile(authInputStream, "auth.json");
        inputStreamService.saveResultToFile(oneUserInputStream, "oneUser.json");
    }

    private Response auth(WebClient client) {
        client.replacePath("api/v1/login")
                .type(MediaType.APPLICATION_FORM_URLENCODED);

        Form form = new Form("user", "admin")
                .param("password", "projectRSIAM2015");

        return client.post(form);
    }

    private Response create(WebClient client, ObjectMapper mapper) throws JsonProcessingException {
        client.replacePath("/api/v1/users.create")
                .type(MediaType.APPLICATION_JSON_TYPE);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", "igor@mail.ru");
        dataMap.put("name", "Igor");
        dataMap.put("password", "Igor");
        dataMap.put("username", "IgorIgor");
        dataMap.put("roles", new String[]{"owner"});

        String jsonResult = mapper.writeValueAsString(dataMap);
        System.out.println("jsonResult = " + jsonResult);

        return client.post(jsonResult);
    }

    private Response delete(WebClient client) {
        client.replacePath("/api/v1/users.delete")
                .type(MediaType.APPLICATION_FORM_URLENCODED);

        Form form = new Form("username", "IgorIgor");
        return client.post(form);
    }

    private Response update(WebClient client, ObjectMapper mapper) throws JsonProcessingException {
        String passHash = DigestUtils.sha256Hex("projectRSIAM2015");

        client.header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
                .header("X-User-Id", "ANrfMv9N4B7dHJGcg")
                .header("X-2fa-code", passHash)
                .header("X-2fa-method", "password")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .replacePath("/api/v1/users.update");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "Неизменный2");
        dataMap.put("username", "change2");
        dataMap.put("password", "password");
        dataMap.put("roles", new String[]{"owner"});

        Map<String, Object> emails = new HashMap<>();
        emails.put("address", "newemail@mail.ru");
        emails.put("verified", false);
        dataMap.put("emails", new Map[]{emails});

        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("userId", "7QYRqqRT9fyGEoZz7");
        parentMap.put("data", dataMap);

        String jsonResult = mapper.writeValueAsString(parentMap);

        System.out.println("jsonResult = " + jsonResult);

        return client.post(jsonResult);
    }
}