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
import org.apache.commons.codec.digest.DigestUtils

import javax.ws.rs.core.MediaType

// Parameters:
// The connector sends us the following:
// client : CXF WebClient
//
// action: String correponding to the action (UPDATE/ADD_ATTRIBUTE_VALUES/REMOVE_ATTRIBUTE_VALUES)
//   - UPDATE : For each input attribute, replace all of the current values of that attribute
//     in the target object with the values of that attribute.
//   - ADD_ATTRIBUTE_VALUES: For each attribute that the input set contains, add to the current values
//     of that attribute in the target object all of the values of that attribute in the input set.
//   - REMOVE_ATTRIBUTE_VALUES: For each attribute that the input set contains, remove from the current values
//     of that attribute in the target object any value that matches one of the values of the attribute from the input set.

// log: a handler to the Log facility
//
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
//
// uid: a String representing the entry uid
//
// attributes: an Attribute Map, containg the <String> attribute name as a key
// and the <List> attribute value(s) as value.
//
// password: password string, clear text (only for UPDATE)
//
// options: a handler to the OperationOptions Map

log.info("Entering " + action + " Script");

passHash = DigestUtils.sha256Hex("projectRSIAM2015")
ObjectMapper mapper = new ObjectMapper()

//baseUri = client.getBaseURI();

client.header("X-Auth-Token", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ")
        .header("X-User-Id", "ANrfMv9N4B7dHJGcg")
        .header("X-2fa-code", passHash)
        .header("X-2fa-method", "password")
        .type(MediaType.APPLICATION_JSON_TYPE)
        .replacePath("/api/v1/users.update")

assert uid != null

switch (action) {
    case "UPDATE":
        switch (objectClass) {
            case "__ACCOUNT__":
                childNode = mapper.createObjectNode()

                def dataMap = new HashMap();

                Iterator it = attributes.entrySet().iterator()
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next()
                    String entryKey = (String) me.getKey()

                    String value = (String) me.getValue()
                    value = value.substring(1, value.length() - 1)
                    log.info("key=" + entryKey + " val=" + value)

                    dataMap.put(entryKey, value)
                }

                stringUid = uid.toString()
                log.info("stringUid=" + stringUid)

                def parentmap = [userId: stringUid, "data": dataMap]

                String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(parentmap);

                println "json= " + jsonResult
                response = client.post(jsonResult)

                if (response.getStatus() == 200) {
                    JsonNode node = mapper.readTree((InputStream) response.getEntity())
                    return node.get("user").get("_id").textValue()
                } else if (response.getStatus() == 400) {
                    log.error("Perhaps a user with this name has already been created!\n")
                }
            default:
                break
        }

        return uid;
        break

    case "ADD_ATTRIBUTE_VALUES":
        break


    default:
        break
}