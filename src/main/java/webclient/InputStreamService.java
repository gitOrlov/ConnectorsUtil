package webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.identityconnectors.framework.common.objects.Uid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public interface InputStreamService {
    Uid getUid(InputStream inputStream, ObjectMapper mapper, String first, String second) throws IOException;

    ArrayList<RocketChatUser> getListOfUsers(InputStream inputStream, ObjectMapper mapper) throws IOException;

    void saveResultToFile(InputStream inputStream, String path) throws IOException;
}
