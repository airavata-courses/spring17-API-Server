/*
*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*
*/

package org.apache.airavata.api.client;

import org.apache.airavata.api.credentials.CredentialManagementService;
import org.apache.airavata.api.gateway.management.GatewayManagementService;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestAiravataClient {


    public static void main(String [] args) {
        System.out.println("Testing Airavata API");

        ConsulRegister cr = new ConsulRegister();
        cr.register();

        ScheduledFuture future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ConsulHealthCheck(), 1, 1, TimeUnit.SECONDS);

        CredentialManagementService.Client credentialManagementClient = null;
        GatewayManagementService.Client gatewatManagementClient = null;
        try {
            credentialManagementClient = MockAiravataClientFactory.createCredentialManagementClient("localhost", 9190);
            System.out.println("SSH Key is " + credentialManagementClient.generateAndRegisterSSHKeys("test", "test"));

            gatewatManagementClient = MockAiravataClientFactory.createGatewayManagementClient("localhost", 9190);
            System.out.println("Test Gateway Name is " + gatewatManagementClient.registerGateway("test"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class ConsulRegister {
    public static Consul consul = Consul.builder().build(); // connect to Consul on localhost
    public static AgentClient agentClient = consul.agentClient();

    public void register(){
        String serviceName = "API-Server";
        String serviceId = "1";
        String fabio_tag = "urlprefix-/APIServer";

        agentClient.register(9190, 3L, serviceName, serviceId, fabio_tag);
    }

}

class ConsulHealthCheck implements Runnable {
    @Override
    public void run() {
        //Process scheduled task here
        try {
            ConsulRegister.agentClient.pass("1");
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}

