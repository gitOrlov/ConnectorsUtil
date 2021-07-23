/**
 * Copyright (C) 2016 ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.cxf.jaxrs.client.WebClient

import javax.ws.rs.core.Form

// Parameters:
// The connector sends us the following:
// client : CXF WebClient
// action: String correponding to the action ("AUTHENTICATE" here)
// log: a handler to the Log facility
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// username: username
// password: password string, clear text or GuardedString depending on configuration
// options: a handler to the OperationOptions Map

log.info("Entering " + action + " Script");

WebClient webClient = client;
ObjectMapper mapper = new ObjectMapper();

switch (objectClass) {
    case "__ACCOUNT__":
        webClient.replacePath("api/v1/login")
        webClient.type("application/x-www-form-urlencoded")

        Form form = new Form("user", username)
                .param("password", password)

        response = webClient.post(form)

        if (response.getStatus() == 200) {
            JsonNode node = mapper.readTree((InputStream) response.getEntity())
            return node.get("data").get("userId").textValue()
        } else {
            throw new RuntimeException("Could not authenticate, status code =  " + response.getStatus());
        }
        break

    default:
        throw new RuntimeException();
}
