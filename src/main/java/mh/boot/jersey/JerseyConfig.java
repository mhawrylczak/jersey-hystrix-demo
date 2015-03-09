package mh.boot.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Created by wladyslaw.hawrylczak on 2015-03-05.
 */
@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(HelloResource.class);
    }
}
