package uk.co.zacheryharley.lambdalight.test.resource;

import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import uk.co.zacheryharley.lambdalight.test.MonitorPoint;
import uk.co.zacheryharley.lambdalight.test.monitor.sqs.MessageCountMonitor;

import java.util.Arrays;
import java.util.List;

import static uk.co.zacheryharley.lambdalight.test.config.LambdaConfiguration.getSqsClient;

public class SqsQueue {
    private final String queueName;
    private final List<String> filters;

    public SqsQueue(String queueName, String... filters) {
        this.queueName = queueName;
        this.filters = Arrays.asList(filters);
    }

    public int getNumberOfMessages() {
        GetQueueAttributesResponse response = getSqsClient().getQueueAttributes(request -> request
            .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
            .queueUrl(queueName));

        return Integer.parseInt(response.attributes().get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES));
    }

    public void deleteMessage(String receiptHandle) {
        getSqsClient().deleteMessage(request -> request.queueUrl(queueName).receiptHandle(receiptHandle));
    }

    public Message getNextMessage(boolean delete) {
        while (true) {
            ReceiveMessageResponse messages = getSqsClient().receiveMessage(request -> request
                .messageAttributeNames(filters)
                .queueUrl(this.queueName)
                .maxNumberOfMessages(1));

            if (!messages.hasMessages()) {
                return null;
            }

            Message received = messages.messages().get(0);
            if (isMessageValid(received)) {
                if (delete) {
                    deleteMessage(received.receiptHandle());
                }
                return received;
            }
        }
    }

    private boolean isMessageValid(Message message) {
        return filters.stream()
            .allMatch(filter -> message.messageAttributes().containsKey(filter));
    }

    public MonitorPoints monitorPoints() {
        return new MonitorPoints(this);
    }

    public static class MonitorPoints {
        private SqsQueue queue;

        public MonitorPoints(SqsQueue queue) {
            this.queue = queue;
        }

        public MonitorPoint messageCount() {
            return new MessageCountMonitor(queue);
        }
    }


}
