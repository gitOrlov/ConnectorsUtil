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
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.cxf.jaxrs.ext.search.ConditionType

import javax.ws.rs.core.Response

// Parameters:
// The connector sends the following:
// client : CXF WebClient
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// action: a string describing the action ("SEARCH" here)
// log: a handler to the Log facility
// options: a handler to the OperationOptions Map
// query: a handler to the Query Map
//
// The Query map describes the filter used (via FIQL's ConditionType):
//
// query = [ operation: "EQUALS", left: attribute, right: "value" ]
// query = [ operation: "GREATER_THAN", left: attribute, right: "value" ]
// query = [ operation: "GREATER_OR_EQUALS", left: attribute, right: "value" ]
// query = [ operation: "LESS_THAN", left: attribute, right: "value" ]
// query = [ operation: "LESS_OR_EQUALS", left: attribute, right: "value" ]
// query = null : then we assume we fetch everything
//
// AND and OR filter just embed a left/right couple of queries.
// query = [ operation: "AND", left: query1, right: query2 ]
// query = [ operation: "OR", left: query1, right: query2 ]
//
// Returns: A list of Maps. Each map describing one row.
// !!!! Each Map must contain a '__UID__' and '__NAME__' attribute.
// This is required to build a ConnectorObject.

def buildConnectorObject(node) {

    print("Node" + node.toPrettyString())

    return [
            __UID__         : node.get("_id").textValue(),
            __NAME__        : node.get("_id").textValue(),
            _id             : node.get("_id").textValue(),
            createdAt       : node.get("createdAt").textValue(),
            username        : node.get("username").textValue(),
            status          : node.get("status").textValue(),
            type            : node.get("type").textValue(),
            name            : node.get("name").textValue(),
            active          : node.get("active").booleanValue(),
            lastLogin       : node.get("lastLogin").textValue(),
            statusConnection: node.get("statusConnection").textValue(),
            utcOffset       : node.get("utcOffset").intValue(),
            roles           : node.get("roles").get(0).textValue()
    ]
}

log.info("Entering " + action + " Script")

ObjectMapper mapper = new ObjectMapper()

def result = []

switch (objectClass) {
    case "__ACCOUNT__":
        log.info("query toString=" + query.toString())

        //Пока что здесь получение только одного пользотвателя, надо в зависимости от параметров запроса сделать получение или одного или всех пользователей
        if (query.get("left").equals("__UID__") && query.get("conditionType").equals(ConditionType.EQUALS)) {
            client.replacePath("/api/v1/users.info")

            String right = (String) query.get("right")
            log.info("right=" + right)

            Response oneUserResponse = client.query("userId", right).get()

            log.info("Response code=" + oneUserResponse.getStatus())

            if (oneUserResponse.getStatus() == 200) {
                JsonNode jsonNode = mapper.readTree((InputStream) oneUserResponse.getEntity())
                ObjectNode objectNode = jsonNode.get("user")
                result.add(buildConnectorObject(objectNode))
            }
        } else {
            log.info("ELSE!")
        }

//  if (query == null || (!query.get("left").equals("__UID__") && !query.get("conditionType").equals("EQUALS"))) {
//    client.path("/users");
//    Response response = client.get();
//    ArrayNode node = mapper.readTree(response.getEntity());
//
//    println node;
//    for (i = 0; i < node.size(); i++) {
//      result.add(buildConnectorObject(node.get(i)));
//    }
//  } else {
//    client.path("/users/" + query.get("right"));
//    Response response = client.get();
//    if (response.getStatus() == 200) {
//      ObjectNode node = mapper.readTree(response.getEntity());
//      println node;
//      result.add(buildConnectorObject(node));
//    } else {
//      log.warn("Could not read object {0}", query.get("right"));
//    }
//  }

        break

    default:
        result
}

return result
