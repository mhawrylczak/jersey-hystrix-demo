package mh.boot.jersey;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static rx.Observable.just;

/**
 * Created by wladyslaw.hawrylczak on 2015-03-07.
 */
public class SchedulersDemoTest {

    public static void main(String[] args) throws InterruptedException {
        Observable.create((Subscriber<? super String> subscriber) -> {
            subscriber.onNext(Thread.currentThread().getName());
            subscriber.onCompleted();
        }).subscribe(message -> {
            System.out.println("Observer thread " + message);
            System.out.println("Observable thread " + Thread.currentThread().getName());
        });

        createObservable()
                .subscribeOn(Schedulers.newThread())
                .subscribe(message -> {
                    System.out.println("Observer thread " + message);
                    System.out.println("Observable thread " + Thread.currentThread().getName());
                });

        Thread.sleep(1000);

        createObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .subscribe(message -> {
                    System.out.println("Observer3 thread " + message);
                    System.out.println("Observable3 thread " + Thread.currentThread().getName());
                });

        Thread.sleep(1000);

        createObservable()
                .observeOn(Schedulers.computation())
                .subscribe(message -> {
                    System.out.println("Observer4 thread " + message);
                    System.out.println("Observable4 thread " + Thread.currentThread().getName());
                });

        Thread.sleep(1000);

    }

    public static Observable<String> createObservable(){
        return Observable.create((Subscriber<? super String> subscriber) -> {
                subscriber.onNext(Thread.currentThread().getName());
                subscriber.onCompleted();
            }
        );
    }
}
