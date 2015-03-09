package mh.boot.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by wladyslaw.hawrylczak on 2015-03-06.
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(callService(10000));
    }

    private static long callService(int bps) throws IOException, InterruptedException {
        URL url = new URL("http://localhost:8080/hello/letters?n=128000");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        long startTime = System.currentTimeMillis();
        long bytesRead = 0;
        try(InputStream is = connection.getInputStream()){
            int nextByte;
            while(-1 != is.read()){
                bytesRead++;
                long currTime = System.currentTimeMillis();
                long expectedTime = startTime + (bytesRead/bps)*1000;
                if (currTime < expectedTime){
                    Thread.sleep(expectedTime-currTime);
                }
            }
        }
        return bytesRead;
    }
}
