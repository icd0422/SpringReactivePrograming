package com.example.reactivepractice.duality;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        //pull();
        push();
        /*
        옵져버 패턴의 한계
        1.Complete??
        2.Error??
         */
    }


    static void pull() {
        Iterable<Integer> intIterable = () -> new Iterator<Integer>() {

            int i = 0;
            static final int MAX = 10;

            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        Iterator<Integer> it = intIterable.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    static void push() {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable observable = new IntObservable();
        observable.addObserver(observer);


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(observable);

        System.out.println(Thread.currentThread().getName() + " Exit");
        executorService.shutdown();
    }


    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }
}
