package mh.boot.jersey;

import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.stereotype.Component;
import rx.schedulers.Schedulers;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import static rx.Observable.just;

@Component
@Path("hello")
public class HelloResource {

    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    private LongAdder count = new LongAdder();
    private LongAdder time = new LongAdder();

    @GET
    @Path("test")
    public void test(@Suspended AsyncResponse response) throws Exception {

    }

    @GET
    @Path("sync")
    public String helloSync() throws Exception {

        String str =  new HelloCommand().run();
        return str + str;//+" count "+count+ " time "+time+" nano  "+(time.longValue()/count.longValue());
    }

    @GET
    @Path("async")
    public void helloAsync(@Suspended AsyncResponse response) throws Exception {
//        System.out.println("Resource thread "+Thread.currentThread());
        new HelloCommand().observe().map(str -> {
//            System.out.println("Map thread "+Thread.currentThread());
            return str + str;
        }).subscribe(
                (result) -> {
//                    System.out.println("Observer thread "+Thread.currentThread());
//                    count.increment();
//                    long nano = System.nanoTime();
                    response.resume(result);
//                    time.add(System.nanoTime() - nano);
                }, (err) -> {
                    response.resume(err);
                });
    }

    @GET
    @Path("async2")
    public void helloAsync2(@Suspended AsyncResponse response) throws Exception {
//        System.out.println("Resource thread "+Thread.currentThread());
        new HelloCommand().observe().map(str -> {
//            System.out.println("Map thread " + Thread.currentThread());
            return str + str;
        }).observeOn(Schedulers.io())
                .subscribe(
                        (result) -> {
//                            System.out.println("Observer thread " + Thread.currentThread());
                            response.resume(result);
                        }, (err) -> {
                            response.resume(err);
                        });
    }

    @GET
    @Path("async3")
    public void helloAsync3(@Suspended AsyncResponse response) throws Exception {
        System.out.println("Resource thread "+Thread.currentThread());
        new HelloCommand().observe().map(str -> {
            System.out.println("Map thread " + Thread.currentThread());
            return str + str;
        }).observeOn(Schedulers.from(executorService))
                .subscribe(
                        (result) -> {
                            System.out.println("Observer thread " + Thread.currentThread());
                            response.resume(result);
                        }, (err) -> {
                            response.resume(err);
                        });
    }

    @GET
    @Path("letters")
    public void lettersAsync(@QueryParam("n")int size, @Suspended AsyncResponse response) throws Exception {
        new HelloCommand(size).observe().subscribe(
                (result) -> {
                    System.out.println("Observer thread " + Thread.currentThread());
//                    count.increment();
//                    long nano = System.nanoTime();
                    response.resume(result);
//                    System.out.println(" for size "+size+" nano time :"+(System.nanoTime() - nano));
                }, (err) -> {
                    response.resume(err);
                });
    }

    @GET
    @Path("letters2")
    public void lettersAsync2(@QueryParam("n")int size, @Suspended AsyncResponse response) throws Exception {
        new HelloCommand(size).observe().observeOn(Schedulers.from(executorService)).subscribe(
                (result) -> {
//                    System.out.println("Observer thread " + Thread.currentThread());
//                    count.increment();
//                    long nano = System.nanoTime();
                    response.resume(result);
//                    System.out.println(" for size "+size+" nano time :"+(System.nanoTime() - nano));
                }, (err) -> {
                    response.resume(err);
                });
    }

    @GET
    @Path("letterss")
    public void lettersSemaphore(@QueryParam("n")int size, @Suspended AsyncResponse response) throws Exception {
        System.out.println("Method thread " + Thread.currentThread());
        new HelloCommand(size, HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE).observe().subscribe(
                (result) -> {
                    System.out.println("Observer thread " + Thread.currentThread());
//                    count.increment();
//                    long nano = System.nanoTime();
                    response.resume(result);
//                    System.out.println(" for size "+size+" nano time :"+(System.nanoTime() - nano));
                }, (err) -> {
                    response.resume(err);
                });
    }
}
