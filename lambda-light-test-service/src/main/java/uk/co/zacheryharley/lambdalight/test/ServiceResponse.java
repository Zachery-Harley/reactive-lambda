package uk.co.zacheryharley.lambdalight.test;

import java.util.UUID;

public class ServiceResponse {
    public enum Source {
        LAMBDA, SQS, UNDEFINED
    }

    private final String responseId;
    private final Source source;
    private final String resourceName;
    private final String message;
    private final int statusCode;
    private final boolean success;

    public ServiceResponse(ServiceResponseBuilder builder) {
        this.responseId = UUID.randomUUID().toString();
        this.source = builder.source;
        this.resourceName = builder.resourceName;
        this.message = builder.message;
        this.statusCode = builder.statusCode;
        this.success = builder.success;
    }

    public Source getSource() {
        return source;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponseId() {
        return responseId;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
            "source='" + source + '\'' +
            ", resourceName='" + resourceName + '\'' +
            ", message='" + message + '\'' +
            ", statusCode=" + statusCode +
            ", success=" + success +
            '}';
    }

    public static ServiceResponseBuilder builder() {
        return new ServiceResponseBuilder();
    }

    public static ServiceResponseBuilder lambda() {
        return new ServiceResponseBuilder()
            .withSource(Source.LAMBDA);
    }

    public static class ServiceResponseBuilder {
        private Source source = Source.UNDEFINED;
        private String resourceName = "UNDEFINED";
        private String message = "UNDEFINED";
        private int statusCode = -1;
        private boolean success = true;

        public ServiceResponseBuilder withSource(Source source) {
            this.source = source;
            return this;
        }

        public ServiceResponseBuilder withResourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        public ServiceResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ServiceResponseBuilder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ServiceResponseBuilder isSuccess(boolean success){
            this.success = success;
            return this;
        }

        public ServiceResponse build() {
            return new ServiceResponse(this);
        }
    }
}
