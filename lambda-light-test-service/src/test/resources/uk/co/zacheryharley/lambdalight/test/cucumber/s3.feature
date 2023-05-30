Feature: S3 Testing Support

  Scenario: Upload and download object
    Given the local object "objects/testDocument.txt" is uploaded to the s3 bucket "upload-test-bucket-001"

    Then download the object "objects/testDocument.txt" from the s3 bucket "upload-test-bucket-001"

    Then the downloaded s3 object's size will be 11 bytes
    And the downloaded s3 object's md5 will match the local files "objects/testDocument.txt"

  Scenario: Upload and reference object
    Given the local object "objects/testDocument.txt" is uploaded to the s3 bucket "upload-test-bucket-001"

    Then the object "objects/testDocument.txt" is present in the s3 bucket "upload-test-bucket-001"
    And the s3 object's size will be 11 bytes

  Scenario: Wait for object to become available
    Given the object "testDocument.txt" is not present in the s3 bucket "upload-test-bucket-001"

    When the object "testDocument.txt" becomes present in the s3 bucket "upload-test-bucket-001"
    Then the s3 object's size will be 11 bytes

