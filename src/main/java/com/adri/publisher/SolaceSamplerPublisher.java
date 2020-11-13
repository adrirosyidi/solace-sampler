package com.adri.publisher;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolaceSamplerPublisher extends AbstractJavaSamplerClient {

    private static final String ARG1_TAG = "Hostname";
    private static final String ARG2_TAG = "Username";
    private static final String ARG3_TAG = "VPN";

    private static final Logger LOGGER = LoggerFactory.getLogger(SolaceSamplerPublisher.class);

    @Override
    public Arguments getDefaultParameters() {

        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument(ARG1_TAG,"hostname");
        defaultParameters.addArgument(ARG2_TAG,"username");
        defaultParameters.addArgument(ARG3_TAG,"vpn");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String arg1 = javaSamplerContext.getParameter(ARG1_TAG);
        String arg2 = javaSamplerContext.getParameter(ARG2_TAG);
        String arg3 = javaSamplerContext.getParameter(ARG3_TAG);
        Publisher publisher = new Publisher();
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();

        try {
            publisher.sendMsg(arg1, arg2, arg3);
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(Boolean.TRUE);
            sampleResult.setResponseCodeOK();
        } catch (Exception e) {
            LOGGER.error("Request was not successfully processed",e);
            sampleResult.sampleEnd();
            sampleResult.setResponseMessage(e.getMessage());
            sampleResult.setSuccessful(Boolean.FALSE);

        }

        return sampleResult;
    }
}
