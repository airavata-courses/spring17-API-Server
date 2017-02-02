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

import java.util.Comparator;
import java.util.Optional;

import org.apache.airavata.api.credentials.CredentialManagementService;
import org.apache.airavata.api.gateway.management.GatewayManagementService;
import org.apache.curator.generated.CuratorException;
import org.apache.curator.generated.CuratorProjection;
import org.apache.curator.generated.CuratorService;
import org.apache.curator.generated.DiscoveryInstance;
import org.apache.curator.generated.DiscoveryProjection;
import org.apache.curator.generated.DiscoveryServiceLowLevel;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class TestAiravataClient {
	private static TTransport tTransport;

	private static DiscoveryServiceLowLevel.Client discoveryServiceClient;


	private static CuratorProjection curatorProjection;

	private static DiscoveryProjection discoveryProjection;

    public static void main(String [] args) throws CuratorException, TException {
        System.out.println("Testing Airavata API");
        tTransport = new TSocket("localhost", 9090);
		tTransport.open();
		TProtocolFactory tProtocolFactory = new TBinaryProtocol.Factory();
		TProtocol tProtocol = tProtocolFactory.getProtocol(tTransport);
		discoveryServiceClient = new DiscoveryServiceLowLevel.Client(tProtocol);
		curatorProjection = new CuratorService.Client(tProtocol).newCuratorProjection("main");
		Optional<DiscoveryInstance> discoveryInstance=discoveryServiceClient.queryForInstances(curatorProjection, discoveryProjection, "api-server").stream().min(Comparator.comparingDouble((DiscoveryInstance instance)->Double.parseDouble(new String(instance.getPayload()))));
        CredentialManagementService.Client credentialManagementClient = null;
        GatewayManagementService.Client gatewatManagementClient = null;
        try {
            credentialManagementClient = MockAiravataClientFactory.createCredentialManagementClient(discoveryInstance.map(DiscoveryInstance::getAddress).orElse("localhost"), discoveryInstance.map(DiscoveryInstance::getPort).orElse(9190));
            System.out.println("SSH Key is " + credentialManagementClient.generateAndRegisterSSHKeys("test", "test"));

            gatewatManagementClient = MockAiravataClientFactory.createGatewayManagementClient("localhost", 9190);
            System.out.println("Test Gateway Name is " + gatewatManagementClient.registerGateway("test"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
