package com.example.demo;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@SpringBootApplication
public class DemoApplication
{
    public static void main(final String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public Function<Tuple2<Flux<Message<String>>, Flux<Message<String>>>, Tuple2<Flux<Message<String>>, Flux<Message<String>>>> handler()
    {
        return input ->
        {
            final Flux<Message<String>> out1 = input.getT1().map(buildMessage(String::toUpperCase));
            final Flux<Message<String>> out2 = input.getT2().map(buildMessage(String::toLowerCase));

            return Tuples.of(out1, out2);
        };
    }

    private UnaryOperator<Message<String>> buildMessage(final UnaryOperator<String> operator)
    {
        return m -> MessageBuilder.withPayload(operator.apply(m.getPayload()))
                                  .setHeader("partitionKey", m.getPayload())
                                  .build();
    }
}
