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
// action: String correponding to the action ("CREATE" here)
// log: a handler to the Log facility
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// id: The entry identifier (ConnId's "Name" atribute. (most often matches the uid))
// attributes: an Attribute Map, containg the <String> attribute name as a key
// and the <List> attribute value(s) as value.
// password: password string, clear text
// options: a handler to the OperationOptions Map

log.info("Entering " + action + " Script\n");

WebClient webClient = client
ObjectMapper mapper = new ObjectMapper()

switch (objectClass) {
    case "__ACCOUNT__":
        webClient.replacePath("api/v1/users.create")
                .type("application/x-www-form-urlencoded")
                .header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
                .header("X-User-Id", "ANrfMv9N4B7dHJGcg")

        Form form = new Form()

        Iterator it = attributes.entrySet().iterator()
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next()
            String entryKey = (String) me.getKey()

            String value = (String) me.getValue()
            value = value.substring(1, value.length() - 1)

            if (entryKey.equals("name")) {
                form.param("name", value)
                log.info("key=" + entryKey + " val=" + value)

            } else if (entryKey.equals("email")) {
                form.param("email", value)
                log.info("key=" + entryKey + " val=" + value)

            } else if (entryKey.equals("password")) {
                form.param("password", value)
                log.info("key=" + entryKey + " val=" + value)

            } else if (entryKey.equals("username")) {
                form.param("username", value)
                log.info("key=" + entryKey + " val=" + value)
            }
        }

        response = webClient.post(form)

        if (response.getStatus() == 200) {
            JsonNode node = mapper.readTree((InputStream) response.getEntity())
            return node.get("user").get("_id").textValue()
        } else if (response.getStatus() == 400) {
            log.error("Perhaps a user with this name has already been created!\n")
        } else {
            throw new RuntimeException("Could not create! status code =  " + response.getStatus() + "\n")
        }
        break

    default:
        throw new RuntimeException()
}

return null
