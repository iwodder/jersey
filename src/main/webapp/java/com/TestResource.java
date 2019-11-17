package main.webapp.java.com;

import main.webapp.java.com.customparams.MyDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "Hello from Jersey.";
    }


    @GET
    @Path("/date")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDate(@QueryParam("type") MyDate date) {
        return date.toString();
    }

    @GET
    @Path("/secure-date")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSecureDate(@QueryParam("type") MyDate date) {
        return date.toString();
    }
}
