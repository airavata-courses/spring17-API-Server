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

package org.apache.airavata.api.server;

import java.nio.ByteBuffer;

import org.apache.airavata.api.credentials.CredentialManagementService;
import org.apache.airavata.api.gateway.management.GatewayManagementService;
import org.apache.airavata.api.handlers.CredentialManagementHandler;
import org.apache.airavata.api.handlers.GatewayManagementHandler;
import org.apache.curator.generated.CuratorException;
import org.apache.curator.generated.CuratorProjection;
import org.apache.curator.generated.CuratorService;
import org.apache.curator.generated.DiscoveryInstance;
import org.apache.curator.generated.DiscoveryProjection;
import org.apache.curator.generated.DiscoveryService;
import org.apache.curator.generated.DiscoveryServiceLowLevel;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockAiravataAPIServer {

    private final static Logger logger = LoggerFactory.getLogger(MockAiravataAPIServer.class);

    public static CredentialManagementHandler credentialManagementHandler;
    public static CredentialManagementService.Processor credentialManagementProcessor;

    public static GatewayManagementHandler gatewayManagementHandler;
    public static GatewayManagementService.Processor gatewayManagementProcessor;

	private static TTransport tTransport;

	private static DiscoveryServiceLowLevel.Client discoveryServiceClient;

	private static DiscoveryInstance discoveryInstance;

    public static void main(String [] args) {
        try {
        	register();
            credentialManagementHandler = new CredentialManagementHandler();
            credentialManagementProcessor = new CredentialManagementService.Processor(credentialManagementHandler);

            gatewayManagementHandler = new GatewayManagementHandler();
            gatewayManagementProcessor = new GatewayManagementService.Processor(gatewayManagementHandler);

            TMultiplexedProcessor airavataServerProcessor = new TMultiplexedProcessor();

            airavataServerProcessor.registerProcessor("CredentialManagementService",credentialManagementProcessor);
            airavataServerProcessor.registerProcessor("GatewayManagementService",gatewayManagementProcessor);

            TServerTransport serverTransport = new TServerSocket(9190);

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(airavataServerProcessor));

            System.out.println("Starting Mock Airavata API server...");
            server.serve();

        } catch (Exception x) {
            x.printStackTrace();
        }finally {
			if(tTransport!=null){
				tTransport.close();
			}
		}
    }
    
    
    private static void register() throws CuratorException, TException{
    	tTransport = new TSocket("localhost", 9090);
    	tTransport.open();
    	TProtocolFactory tProtocolFactory=new TBinaryProtocol.Factory();
    	TProtocol tProtocol=tProtocolFactory.getProtocol(tTransport);
    	discoveryServiceClient = new DiscoveryServiceLowLevel.Client(tProtocol);
    	CuratorProjection curatorProjection=new CuratorService.Client(tProtocol).newCuratorProjection("main");
    	DiscoveryService.Client discoveryServiceOrClient=new DiscoveryService.Client(tProtocol);
    	discoveryInstance = discoveryServiceOrClient.makeDiscoveryInstance("MicroService", ByteBuffer.allocate(1), 9190);
    	discoveryInstance.address="localhost";
    	DiscoveryProjection discoveryProjection=discoveryServiceOrClient.startDiscovery(curatorProjection, "services", discoveryInstance);
    	discoveryServiceClient.registerInstance(curatorProjection, discoveryProjection, discoveryInstance);
    	
    }


}
