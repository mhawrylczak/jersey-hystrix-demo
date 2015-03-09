package mh.boot.jersey;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import java.util.Random;

/**
 * Created by wladyslaw.hawrylczak on 2015-03-05.
 */
public class HelloCommand extends HystrixCommand<String> {

    private final int size;
    private final Random random = new Random();

    public HelloCommand(){
        this(1024);
    }

    public HelloCommand(int size ){
        this(size, HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
    }

    public HelloCommand(int size, HystrixCommandProperties.ExecutionIsolationStrategy strategy) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(strategy))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(200)));
        this.size = size;
    }

    @Override
    protected String run() throws Exception {
        StringBuilder response = new StringBuilder();
        for(int i = 0; i < size; i++){
            char letter = (char)('a'+random.nextInt(24));
            response.append(letter);
        }
        return response.toString();
    }
}
