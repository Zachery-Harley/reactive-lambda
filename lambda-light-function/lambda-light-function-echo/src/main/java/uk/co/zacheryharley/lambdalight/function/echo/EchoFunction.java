package uk.co.zacheryharley.lambdalight.function.echo;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootConfiguration
public class EchoFunction implements ApplicationContextInitializer<GenericApplicationContext> {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(EchoFunction.class, args);
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean("eventHandler", FunctionRegistration.class,
            () -> new FunctionRegistration<>(eventHandler())
                .type(EchoEventHandler.getFunctionType())
        );
    }

    public EchoEventHandler eventHandler() {
        return new EchoEventHandler();
    }

}
