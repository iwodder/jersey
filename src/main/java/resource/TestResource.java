package java.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class TestResource {

    @GET
    @Path("/hello")
    public String getHello() {
        return "Hello from Jersey.";
    }
}
