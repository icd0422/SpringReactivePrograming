package com.example.reactivepractice;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSubscriber implements Subscriber<Integer> {

    Subscriber delegate;

    DelegateSubscriber(Subscriber subscriber) {
        this.delegate = subscriber;
    }

    @Override
    public void onSubscribe(Subscription s) {
        delegate.onSubscribe(s);
    }

    @Override
    public void onNext(Integer t) {
        delegate.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        delegate.onError(t);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
    }
}
