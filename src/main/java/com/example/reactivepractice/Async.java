package com.example.reactivepractice;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Async {
    public static void main(String[] args) {
        Publisher<Integer> originPublisher = new Publisher() {
            @Override
            public void subscribe(Subscriber s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        log.debug("request");
                        s.onNext(1);
                        s.onNext(2);
                        s.onNext(3);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> logSubscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext : " + integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError");
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };

        Publisher<Integer> subscribeOnPublisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                ExecutorService executorService = Executors.newSingleThreadExecutor(
                        new CustomizableThreadFactory() {
                            @Override
                            public String getThreadNamePrefix() {
                                return "subOn-";
                            }
                        }
                );
                executorService.execute(() -> {
                    originPublisher.subscribe(s);
                });
            }
        };

        Publisher<Integer> publishOnPublisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                subscribeOnPublisher.subscribe(new Subscriber<Integer>() {

                    ExecutorService executorService = Executors.newSingleThreadExecutor(
                            new CustomizableThreadFactory() {
                                @Override
                                public String getThreadNamePrefix() {
                                    return "pubOn-";
                                }
                            }
                    );

                    @Override
                    public void onSubscribe(Subscription s) {
                        originSubscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        executorService.execute(() -> originSubscriber.onNext(integer));
                    }

                    @Override
                    public void onError(Throwable t) {
                        executorService.execute(() -> originSubscriber.onError(t));

                    }

                    @Override
                    public void onComplete() {
                        executorService.execute(() -> originSubscriber.onComplete());
                    }
                });
            }
        };

        publishOnPublisher.subscribe(logSubscriber);
        log.debug("exit");
    }
}
