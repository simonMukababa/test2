package gcfv2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;


public class HelloHttpFunction implements HttpFunction {
  private static final String INCOMING_GOODS_TOPIC = "projects/sok-tst-svc/topics/t-qa-sap-sync-incoming-goods-queue";
    private static final String PURCHASE_ORDER = "purchase_order";

    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        try {
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            System.out.println("Message as string: " + requestBody);

            // Parse JSON request body
            JsonNode jsonNode = new ObjectMapper().readTree(requestBody);

            // Check if the notificationType property exists
            if (jsonNode.has("data") && jsonNode.get("data").has("document_type")) {
                String documentType = jsonNode.get("data").get("document_type").asText();
                String message;

                switch (documentType) {
                    case PURCHASE_ORDER:
                        // Publish message since the document type is "purchase_order"
                        List<PubsubMessage> pubsubMessages = new ArrayList<>();
                        ByteString messageData = ByteString.copyFromUtf8(jsonNode.toString());
                        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                                .setData(messageData)
                                .build();
                        pubsubMessages.add(pubsubMessage);
                        publishMessages(INCOMING_GOODS_TOPIC, pubsubMessages);
                        message = "Message: " + pubsubMessages.size() + " messages published.";
                        System.out.println("Message published to topic " + INCOMING_GOODS_TOPIC);
                        break;
                    default:
                        message = "No matching webhook for " + documentType;
                        break;
                }

                response.getWriter().write(message);
            }else {
                System.out.println("Invalid notification payload or missing document_type property");
            }
        } catch (Exception e) {
            System.err.println("Received error while publishing: " + e.getMessage());
            response.getWriter().write("Error occurred during message publish to Pub/Sub. " + e.getMessage());
        }
    }

    private void publishMessages(String topic, List<PubsubMessage> messages) {
        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(ProjectTopicName.parse(topic)).build();
            for (PubsubMessage message : messages) {
                publisher.publish(message);
            }
            System.out.println("Messages published to topic " + topic + ", message count: " + messages.size() + ".");
        } catch (Exception e) {
            // Handle the exception
            System.err.println("Error occurred during message publish: " + e.getMessage());
        } finally {
            // Close the publisher in the finally block to ensure resource cleanup
            if (publisher != null) {
                try {
                    publisher.shutdown();
                } catch (Exception e) {
                    // Handle the exception
                    System.err.println("Error occurred while closing the publisher: " + e.getMessage());
                }
            }
        }
    }
}
