package com.example.reactivepractice;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub2 {
    public static void main(String[] args) {

        Publisher<Integer> originPublisher =
                getIntegerPublisher(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));

        Publisher<Integer> mapPublisher = getMapPublisher(originPublisher, a -> a * 10);
        Publisher<Integer> mapPublisher2 = getMapPublisher(mapPublisher, a -> -1 * a);
        Publisher<Integer> sumPublisher = getSumPublisher(originPublisher);
        Publisher<Integer> reducePublisher = getReducePublisher(originPublisher, 0, (a, b) -> a + b);

        Subscriber<Integer> subscriber = getLogSubscriber();

        reducePublisher.subscribe(subscriber);
    }

    static Publisher<Integer> getReducePublisher(Publisher<Integer> originPublisher, int init, BiFunction<Integer, Integer, Integer> bf) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new DelegateSubscriber(originSubscriber) {

                    int initValue = init;

                    @Override
                    public void onNext(Integer i) {
                        initValue = bf.apply(initValue, i);
                    }

                    @Override
                    public void onComplete() {
                        originSubscriber.onNext(initValue);
                        originSubscriber.onComplete();
                    }
                });
            }
        };
    }

    static Publisher<Integer> getMapPublisher(Publisher<Integer> originPublisher, Function<Integer, Integer> function) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originsSubscriber) {
                originPublisher.subscribe(
                        new DelegateSubscriber(originsSubscriber) {
                            @Override
                            public void onNext(Integer integer) {
                                originsSubscriber.onNext(integer);
                            }
                        }
                );
            }
        };
    }

    static Publisher<Integer> getSumPublisher(Publisher<Integer> originPublisher) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> originSubscriber) {
                originPublisher.subscribe(new DelegateSubscriber(originSubscriber) {
                    int sum = 0;

                    @Override
                    public void onNext(Integer t) {
                        sum += t;
                    }

                    @Override
                    public void onComplete() {
                        originSubscriber.onNext(sum);
                        originSubscriber.onComplete();
                    }
                });
            }
        };
    }

    static Publisher<Integer> getIntegerPublisher(Iterable<Integer> it) {
        return new Publisher<Integer>() {
            Iterable<Integer> iterable = it;

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iterable.forEach(i -> {
                                subscriber.onNext(i);
                            });
                            subscriber.onComplete();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

    static Subscriber<Integer> getLogSubscriber() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext :" + integer);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }
}
