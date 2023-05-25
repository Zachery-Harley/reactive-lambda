Feature: Sanity Test
  This feature exists to sanity check the linking up of cucumber resources

  Background:
#    Given the lambda "EchoFunction" triggers are all disabled

  Scenario Outline: Run the echo lambda
    When the SQS message "<message>" is sent to the lambda "EchoFunction"

    Then the lambda "EchoFunction" will respond successfully
    And the response payload will be "<response>"

    Examples:
      | message                          | response                   |
      | {\"mustBeJson\":\"Hello World\"} | {\"batchItemFailures\":[]} |
      | {\"IgnoredKey\":\"Hello World\"} | {\"batchItemFailures\":[]} |