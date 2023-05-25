package uk.co.zacheryharley.lambdalight.test.monitor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.zacheryharley.lambdalight.test.Action;
import uk.co.zacheryharley.lambdalight.test.MonitorPoint;
import uk.co.zacheryharley.lambdalight.test.resource.Lambda;
import uk.co.zacheryharley.lambdalight.test.resource.SqsQueue;

import static org.junit.jupiter.api.Assertions.*;

class SQSMonitorPointTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQSMonitorPointTest.class);

    @Test
    void name() throws InterruptedException {
        //State should be Active: https://docs.aws.amazon.com/lambda/latest/dg/functions-states.html
        Lambda testLambda = new Lambda("EchoFunction");
        SqsQueue queue = new SqsQueue("ExitQueue");
//
//        Action action = testLambda.actions().disableAllTriggers();
//        action.fire();
//        action.revert();
//
//        MonitorPoint monitor = queue.monitorPoints().messageCount();
//
//        while(true) {
//            Thread.sleep(1000);
//            System.out.println(monitor.refresh());
//        }

//        Lambda lambdaA = new Lambda(lambdaName);
//        MonitorPoint monitorPoint = lambdaA.monitorPoints().logs();
//        MonitorPoint exitQueue = new Sqs("queueName").monitorPoint().messages();
//




//        SmokeTest
//            .setup()
//                .action(lambdaA.disableTrigger())
//                .action(lambdaA.setEnvironment("SMOKE_TEST", "true"))
//            .yeild()
//                .condition(lambda.conditions().allStopped())
//                .condition(lambda.triggers().allDisabled())
//            .trigger(lambda.triggers().sqsEvent("MyEvent"))
//            .yeild()
//                .condition(exitQueue.conditions().eventCount(1))
//            .assertion(test ->
//                test.assert(monitorPoint).containsLog("Successful Run");
////                test.assert(monitorPoint).containsJsonLog("Message", "Successful Run");
//                test.assert(exitQueue).messageCount(1);
//            )
    }
}