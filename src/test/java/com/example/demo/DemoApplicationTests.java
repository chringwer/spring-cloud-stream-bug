package com.example.demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Import(TestChannelBinderConfiguration.class)
abstract class DemoApplicationTests
{
    @SpringBootTest
    public static class PartioningDisabled extends DemoApplicationTests
    {
    }

    @SpringBootTest(properties = "spring.cloud.stream.default.producer.partitionKeyExpression=headers['partitionKey']")
    public static class PartioningEnabled extends DemoApplicationTests
    {
    }

    @Autowired
    InputDestination input;

    @Autowired
    OutputDestination output;

    @Test
    void shouldUpperCaseInputString()
    {
        input.send(MessageBuilder.withPayload("abc").build(), "handler-in-0");

        final Message<byte[]> message = output.receive(1000, "handler-out-0");

        assertThat(new String(message.getPayload()), is("ABC"));
    }
}
