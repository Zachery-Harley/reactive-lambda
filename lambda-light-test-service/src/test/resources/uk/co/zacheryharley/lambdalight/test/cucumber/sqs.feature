Feature: SQS Testing Support

  Scenario: Wait for no messages left on queue
    Given there are 0 messages on the SQS queue "my-test-queue"
    Then there will be 0 messages on the SQS queue "my-test-queue"

  Scenario: Wait for a message on the queue and consume it
    Given there are 0 messsages on the SQS queue "my-test-queue"

    When the SQS message "Hello World" is sent to the SQS queue "my-test-queue"

    Then the queue will contain a SQS message with the body "Hello World"
