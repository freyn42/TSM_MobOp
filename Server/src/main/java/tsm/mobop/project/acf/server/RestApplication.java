package tsm.mobop.project.acf.server;

import tsm.mobop.project.acf.server.ri.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RestApplication extends Application {
    public Set<Class<?>> getClasses(){
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(CityRI.class);
        s.add(CountryRI.class);
        s.add(ContinentRI.class);
        s.add(GroupRI.class);
        s.add(DeviceRI.class);
        s.add(UsageRI.class);

        return s;
    }
}