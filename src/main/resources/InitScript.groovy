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
import org.identityconnectors.common.security.GuardedString
import org.identityconnectors.common.security.SecurityUtil

import javax.ws.rs.core.Form

println("Entering init Script")

String userName = cfg.getUsername()
GuardedString guardedPassword = cfg.getPassword()
String password = SecurityUtil.decrypt(guardedPassword)

println("userName = " + userName)
println("password = " + password)

Form form = new Form("user", userName)
        .param("password", password)

WebClient webClient = WebClient.create(cfg.getBaseAddress())
        .type("application/x-www-form-urlencoded")
        .replacePath("api/v1/login")

authResponse = webClient.post(form)

if (authResponse.getStatus() == 200) {
    ObjectMapper mapper = new ObjectMapper()
    JsonNode node = mapper.readTree((InputStream) authResponse.getEntity())
    String userId = node.get("data").get("userId").textValue()
    String authToken = node.get("data").get("authToken").textValue()

    println("userId = " + userId)
    println("authToken = " + authToken)

    webClient.header("X-Auth-Token", authToken)
            .header("X-User-Id", userId)

    return webClient
} else {
    throw new RuntimeException("Could not authenticate, status code =  " + authResponse.getStatus());
}

return null



