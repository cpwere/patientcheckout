package com.cp.aws.lambda.apis.patientmanagement;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.cp.aws.lambda.apis.patientmanagement.event.PatientCheckoutEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PatientCheckoutLambda {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handler(S3Event event) {
        System.out.println("Processing s3 event");

//        String log = String.format(" region= %s user_identify= %s", event.getRecords().get(0).getAwsRegion(),
//                event.getRecords().get(0).getUserIdentity().getPrincipalId());

//        System.out.println("" + log);

        //AmazonS3 s3 = buildClient();
        Region region = s3.getRegion();
        System.out.println("S3Client region: " + region.toString() + " region_name= " + s3.getRegionName());

//        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                            .build()

        event.getRecords().forEach(record -> {
            S3ObjectInputStream s3inputStream = s3.getObject(record.getS3().getBucket().getName(),
                                record.getS3().getObject().getKey())
                            .getObjectContent();
            try {
                List<PatientCheckoutEvent> listOfEvents = Arrays
                        .asList(objectMapper.readValue(s3inputStream, PatientCheckoutEvent[].class));
                System.out.println(listOfEvents);

                listOfEvents.forEach(checkoutEvent -> {
                    try {
                        sns.publish(System.getenv("PATIENT_CHECKOUT_TOPIC"),
                                objectMapper.writeValueAsString(checkoutEvent));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private AmazonS3 buildClient() {
        String key = "EXAMPLE"; //"123";
        String secret = "EXAMPLE"; //"123";
            AWSCredentials credentials = new BasicAWSCredentials(key, secret);

            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:4566"
                            , null);

            return AmazonS3ClientBuilder
                    .standard()
                    //.withEndpointConfiguration(endpointConfig)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    //.enablePathStyleAccess()
                    .withRegion(Regions.fromName("eu-west-1"))
                    .build();

    }
    private AmazonSNS buildClient() {
        String key = "EXAMPLE"; //"123";
        String secret = "EXAMPLE"; //"123";
        AWSCredentials credentials = new BasicAWSCredentials(key, secret);

        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration("http://localhost:4566"
                        , null);

        return AmazonS3ClientBuilder
                .standard()
                //.withEndpointConfiguration(endpointConfig)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                //.enablePathStyleAccess()
                .withRegion(Regions.fromName("eu-west-1"))
                .build();

    }
}
