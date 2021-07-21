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
import org.apache.cxf.jaxrs.client.WebClient

// Parameters:
// The connector sends the following:
// client : CXF WebClient
// action: a string describing the action ("DELETE" here)
// log: a handler to the Log facility
// objectClass: a String describing the Object class (__ACCOUNT__ / __GROUP__ / other)
// options: a handler to the OperationOptions Map
// uid: String for the unique id that specifies the object to delete

log.info("Entering " + action + " Script");

WebClient webClient = client;

assert uid != null

switch ( objectClass ) {
case "__ACCOUNT__":
  webClient.path("/users/" + uid);
  webClient.delete();
  break

default:
  break
}
