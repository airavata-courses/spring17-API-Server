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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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
import org.apache.http.util.ByteArrayBuffer;
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
	private static final long WAITTIME = 1;

	public static CredentialManagementHandler credentialManagementHandler;
	public static CredentialManagementService.Processor credentialManagementProcessor;

	public static GatewayManagementHandler gatewayManagementHandler;
	public static GatewayManagementService.Processor gatewayManagementProcessor;

	private static TTransport tTransport;

	private static DiscoveryServiceLowLevel.Client discoveryServiceClient;

	private static DiscoveryInstance discoveryInstance;
	private static Thread thread;

	private static CuratorProjection curatorProjection;

	private static DiscoveryProjection discoveryProjection;

	public static void main(String[] args) throws InterruptedException {
		try {
			int port;
			if(args.length>0){
				port=Integer.parseInt(args[0]);
			}else{
				port=9190;
			}
			register(port);
			credentialManagementHandler = new CredentialManagementHandler();
			credentialManagementProcessor = new CredentialManagementService.Processor(credentialManagementHandler);

			gatewayManagementHandler = new GatewayManagementHandler();
			gatewayManagementProcessor = new GatewayManagementService.Processor(gatewayManagementHandler);

			TMultiplexedProcessor airavataServerProcessor = new TMultiplexedProcessor();

			airavataServerProcessor.registerProcessor("CredentialManagementService", credentialManagementProcessor);
			airavataServerProcessor.registerProcessor("GatewayManagementService", gatewayManagementProcessor);

			TServerTransport serverTransport = new TServerSocket(port);

			TServer server = new TThreadPoolServer(
					new TThreadPoolServer.Args(serverTransport).processor(airavataServerProcessor));

			System.out.println("Starting Mock Airavata API server...");
			server.serve();

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			if (tTransport != null) {
				tTransport.close();
			}
			thread.interrupt();
			int i = 0;
			while (thread.isAlive()) {
				Thread.sleep(1000);
				i++;
				if (i > 300) {
					break;
				}
			}
		}
	}

	private static void register(int port) throws CuratorException, TException {
		Runnable runnable = () -> {
			while (!Thread.interrupted()) {
				try {
					updateDiscoveryInstancePayload();
					discoveryServiceClient.updateInstance(curatorProjection, discoveryProjection, discoveryInstance);
					try {
						Thread.sleep(WAITTIME * 60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				} catch (CuratorException e) {
					e.printStackTrace();
				} catch (TException e) {
					e.printStackTrace();
				}
			}
		};
		thread = new Thread(runnable);
		String appName = System.getenv("APP_NAME");
		tTransport = new TSocket("localhost", 9090);
		tTransport.open();
		TProtocolFactory tProtocolFactory = new TBinaryProtocol.Factory();
		TProtocol tProtocol = tProtocolFactory.getProtocol(tTransport);
		discoveryServiceClient = new DiscoveryServiceLowLevel.Client(tProtocol);
		curatorProjection = new CuratorService.Client(tProtocol).newCuratorProjection("main");
		DiscoveryService.Client discoveryServiceOrClient = new DiscoveryService.Client(tProtocol);
		discoveryInstance = discoveryServiceOrClient.makeDiscoveryInstance("api-server", ByteBuffer.allocate(1),
				port);
		updateDiscoveryInstancePayload();
		if (appName != null) {
			discoveryInstance.address = appName;
		} else {
			discoveryInstance.address = "localhost";
		}
		discoveryInstance.port = 9090;
		discoveryProjection = discoveryServiceOrClient.startDiscovery(curatorProjection, "services", discoveryInstance);
		discoveryServiceClient.registerInstance(curatorProjection, discoveryProjection, discoveryInstance);
		thread.start();

	}

	private static void updateDiscoveryInstancePayload() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		byte[] bytes = Double.toString(operatingSystemMXBean.getSystemLoadAverage()).getBytes();
		discoveryInstance.payload = ByteBuffer.wrap(bytes);
	}

}
