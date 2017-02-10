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
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

public class LoadTestAiravataClient extends AbstractJavaSamplerClient implements Serializable{

    private static final String METHOD_TAG = "method";
    private static final String ARG1_TAG = "arg1";
    private static final String ARG2_TAG = "arg2";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTestAiravataClient.class);

    @Override
    public Arguments getDefaultParameters() {

        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument(METHOD_TAG,"test");
        defaultParameters.addArgument(ARG1_TAG,"arg1");
        defaultParameters.addArgument(ARG2_TAG,"arg2");

        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String method = javaSamplerContext.getParameter(METHOD_TAG);
        String arg1 = javaSamplerContext.getParameter(ARG1_TAG);
        String arg2 = javaSamplerContext.getParameter(ARG2_TAG);

//        FunctionalityForSampling functionalityForSampling = new FunctionalityForSampling();

        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();

        try {
//            String message = functionalityForSampling.testFunction(arg1,arg2);
        	int portNum = Integer.parseInt(arg2);
        	sendRequest(arg1, portNum);
            sampleResult.sampleEnd();;
            sampleResult.setSuccessful(Boolean.TRUE);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseMessage("OK");
        } catch (Exception e) {
            LOGGER.error("Request was not successfully processed",e);
            sampleResult.sampleEnd();
            sampleResult.setResponseMessage(e.getMessage());
            sampleResult.setSuccessful(Boolean.FALSE);

        }

        return sampleResult;
    }
    
	private void sendRequest(String host, int port) throws Exception{
		CredentialManagementService.Client credentialManagementClient = null;
		GatewayManagementService.Client gatewatManagementClient = null;
//		try {
		credentialManagementClient = MockAiravataClientFactory.createCredentialManagementClient(host,
				port);
		LOGGER.info("SSH Key is " + credentialManagementClient.generateAndRegisterSSHKeys("test", "test"));
//		System.out.println("SSH Key is " + credentialManagementClient.generateAndRegisterSSHKeys("test", "test"));

		gatewatManagementClient = MockAiravataClientFactory.createGatewayManagementClient(host, port);
		LOGGER.info("Test Gateway Name is " + gatewatManagementClient.registerGateway("test"));
//		System.out.println("Test Gateway Name is " + gatewatManagementClient.registerGateway("test"));

//		} catch (Exception e) {
//			throws new Exce
//		}
	}
    
//    public static void main(String [] args) {
//        System.out.println("Load Testing Airavata API");
//
//
//        CredentialManagementService.Client credentialManagementClient = null;
//        GatewayManagementService.Client gatewatManagementClient = null;
//        try {
//            credentialManagementClient = MockAiravataClientFactory.createCredentialManagementClient("52.14.96.95", 9000);
//            System.out.println("SSH Key is " + credentialManagementClient.generateAndRegisterSSHKeys("test", "test"));
//
//            gatewatManagementClient = MockAiravataClientFactory.createGatewayManagementClient("52.14.96.95", 9000);
//            System.out.println("Test Gateway Name is " + gatewatManagementClient.registerGateway("test"));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
