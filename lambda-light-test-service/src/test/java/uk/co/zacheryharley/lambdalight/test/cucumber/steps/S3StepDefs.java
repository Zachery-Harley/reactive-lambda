package uk.co.zacheryharley.lambdalight.test.cucumber.steps;

import io.cucumber.java8.En;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DigestUtils;
import uk.co.zacheryharley.lambdalight.test.resource.S3Object;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.co.zacheryharley.lambdalight.test.TestContext.*;
import static uk.co.zacheryharley.lambdalight.test.resource.AwsResource.getBucket;

public class S3StepDefs implements En {
    public S3StepDefs() {
        Given("the local object {string} is uploaded to the s3 bucket {string}", (String path, String bucket) -> {
            executeRevertibleAction(getBucket(bucket).actions().uploadObject(path, path));
        });

        Then("download the object {string} from the s3 bucket {string}", (String key, String bucket) -> {
            executeRevertibleAction(getBucket(bucket).getObject(key).actions().download());
        });

        Then("the object {string} is present in the s3 bucket {string}", (String key, String bucket) -> {
            assertThat(getBucket(bucket).getObject(key).exists(), is(true));
            setActiveResource(getBucket(bucket).getObject(key));
        });

        Then("the object {string} is not present in the s3 bucket {string}", (String key, String bucket) -> {
            assertThat(getBucket(bucket).getObject(key).exists(), is(false));
        });

        When("the object {string} becomes present in the s3 bucket {string}", (String key, String bucket) -> {
            boolean result = getBucket(bucket).getObject(key).monitor().exists()
                .until().havingValue(1)
                .wait(Duration.ofSeconds(30));

            assertThat(result, is(true));
            setActiveResource(getBucket(bucket).getObject(key));
        });

        Then("the downloaded s3 object's size will be {long} bytes", (Long sizeBytes) -> {
            Path downloadPath = getActiveResource(S3Object.ACTIVE_RESOURCE_LOCAL_PATH);
            assertThat("No active download path. Download object from s3 bucket before assertions", downloadPath, notNullValue());
            assertThat(Files.size(downloadPath), is(sizeBytes));
        });

        Then("the s3 object's size will be {long} bytes", (Long sizeBytes) -> {
            S3Object activeObject = getActiveResource(S3Object.class);
            assertThat("No active object. Assert object is present in bucket to make active", activeObject, notNullValue());
            assertThat(activeObject.getSize(), is(sizeBytes));
        });

        Then("the downloaded s3 object's md5 will match the local files {string}", (String path) -> {
            Path downloadPath = getActiveResource(S3Object.ACTIVE_RESOURCE_LOCAL_PATH);
            assertThat("No active download path. Download object from s3 bucket before assertions", downloadPath, notNullValue());

            Path localPath = new ClassPathResource(path).getFile().toPath();
            String downloadMd5 = DigestUtils.md5DigestAsHex(Files.newInputStream(downloadPath));
            String localMd5 = DigestUtils.md5DigestAsHex(Files.newInputStream(localPath));
            assertThat(downloadMd5, is(localMd5));
        });

    }
}
