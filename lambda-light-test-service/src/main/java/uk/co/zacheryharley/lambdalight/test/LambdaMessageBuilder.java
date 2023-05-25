package uk.co.zacheryharley.lambdalight.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LambdaMessageBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SQS_RECEIVE_MESSAGE = "{\n" +
        "  \"Records\": [\n" +
        "    {\n" +
        "      \"messageId\": \"19dd0b57-b21e-4ac1-bd88-01bbb068cb78\",\n" +
        "      \"receiptHandle\": \"MessageReceiptHandle\",\n" +
        "      \"body\": \"Hello from SQS!\",\n" +
        "      \"attributes\": {\n" +
        "        \"ApproximateReceiveCount\": \"1\",\n" +
        "        \"SentTimestamp\": \"1523232000000\",\n" +
        "        \"SenderId\": \"123456789012\",\n" +
        "        \"ApproximateFirstReceiveTimestamp\": \"1523232000001\"\n" +
        "      },\n" +
        "      \"messageAttributes\": {},\n" +
        "      \"md5OfBody\": \"{{{md5_of_body}}}\",\n" +
        "      \"eventSource\": \"aws:sqs\",\n" +
        "      \"eventSourceARN\": \"arn:aws:sqs:us-east-1:123456789012:MyQueue\",\n" +
        "      \"awsRegion\": \"us-east-1\"\n" +
        "    }\n" +
        "  ]\n" +
        "}";

    public static String getSqsReceiveMessageWithBody(String message) {
        try {
            ObjectNode rootNode = (ObjectNode) MAPPER.readTree(SQS_RECEIVE_MESSAGE);
            ObjectNode messageNode = (ObjectNode) rootNode.get("Records").get(0);

            messageNode.put("body", message);
            return rootNode.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Bad message");
        }
    }

}
