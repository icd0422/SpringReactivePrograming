package com.example.reactivepractice;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReactivePracticeApplication {

    @RestController
    class Controller {

        @RequestMapping("/hello")
        Publisher<String> test(@RequestParam String name) {
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> s) {
                    s.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            s.onNext("hello" + name);
                            s.onComplete();
                        }

                        @Override
                        public void cancel() {
                        }
                    });
                }
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactivePracticeApplication.class, args);
    }
}
