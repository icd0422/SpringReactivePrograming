package com.example.reactivepractice;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;

public class PubSub {

    public static void main(String[] args) {
        Publisher<Integer> publisher = new Publisher<Integer>() {

            Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);

            @Override
            public void subscribe(Subscriber s) {

                Iterator<Integer> it = iterable.iterator();

                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            while (n-- > 0) {
                                if (it.hasNext()) {
                                    s.onNext(it.next());
                                } else {
                                    s.onComplete();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            s.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };


        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                this.subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext " + item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);
    }


}
