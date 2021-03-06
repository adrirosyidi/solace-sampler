package com.adri.subscriber.topic;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolaceSamplerSubscriberTopic extends AbstractJavaSamplerClient {

    private static final String ARG1_TAG = "Hostname";
    private static final String ARG2_TAG = "Username";
    private static final String ARG3_TAG = "VPN";
    private static final String ARG4_TAG = "Password";
    private static final String ARG5_TAG = "Topic";

    private static final Logger LOGGER = LoggerFactory.getLogger(SolaceSamplerSubscriberTopic.class);

    @Override
    public Arguments getDefaultParameters() {

        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument(ARG1_TAG,"hostname");
        defaultParameters.addArgument(ARG2_TAG,"username");
        defaultParameters.addArgument(ARG4_TAG, "");
        defaultParameters.addArgument(ARG3_TAG,"vpn");
        defaultParameters.addArgument(ARG5_TAG, "topic");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String arg1 = javaSamplerContext.getParameter(ARG1_TAG);
        String arg2 = javaSamplerContext.getParameter(ARG2_TAG);
        String arg3 = javaSamplerContext.getParameter(ARG3_TAG);
        String arg4 = javaSamplerContext.getParameter(ARG4_TAG);
        String arg5 = javaSamplerContext.getParameter(ARG5_TAG);
        Subscriber subscriber = new Subscriber();
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();

        try {
            subscriber.retrieveMsg(arg1, arg2, arg3, arg4, arg5);
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
