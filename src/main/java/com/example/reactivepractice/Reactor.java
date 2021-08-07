package com.example.reactivepractice;

import reactor.core.publisher.Flux;

public class Reactor {
    public static void main(String[] args) {
        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.next(4);
            e.complete();
        })
                .log()
                .map(i -> i * 10)
                .reduce(0, (a, b) -> a + b)
                .log()
                .subscribe(i -> System.out.println(i));
    }
}
