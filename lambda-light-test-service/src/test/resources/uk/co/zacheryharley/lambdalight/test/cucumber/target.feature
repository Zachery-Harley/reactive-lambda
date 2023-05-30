Feature: Aggregation After Staging

  Background:
    Given the lambda "order-generator" triggers are all disabled
    And the lambda "order-distributor" triggers are all disabled
    And there are 0 messages on the SQS queue "staging-queue"

  Scenario: A WDH order is staged
    Given the local objects are uploaded to the s3 bucket "upload-test-bucket" with prefix "test/aggregation1/"
      | objects/aggregationA/fileA |
      | objects/aggregationA/fileB |
      | objects/aggregationA/fileC |
      | objects/aggregationA/fileD |

    When the aggregation request is sent to the SQS queue "my-queue"
      | source | datasource         | afn      | customer | file 0                  | file 1                  | file 2                  | file 3                  |
      | S3     | upload-test-bucket | fileName | test     | test/aggregation1/fileA | test/aggregation1/fileB | test/aggregation1/fileC | test/aggregation1/fileD |

    Then the s3 prefix "staging" will contain 4 files
    Then the object "aggregation/test/aggregation-1" is present in the s3 bucket "staging-bucket"
    And the s3 object's size will be 156 bytes

    Then




