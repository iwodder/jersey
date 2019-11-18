package main.webapp.java.com;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import main.webapp.java.KeyGenerator;
import main.webapp.java.com.customparams.MyDate;
import sun.security.provider.SecureRandom;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.*;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;

@Path("/test")
@Resource
@Singleton
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
    @Secured
    public String getSecureDate(@QueryParam("type") MyDate date) {
        return date.toString();
    }

    @GET
    @Path("/init")
    public Response getToken() {
        long exp = Instant.now().getEpochSecond() + 3600L;
        SecretKey pk = KeyGenerator.getPrivateKey();
        String jwt = Jwts.builder()
                .setIssuer("com.ice.water")
                .setSubject("test token")
                .claim("privs", "admin")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.ofEpochSecond(exp)))
                .signWith(pk, SignatureAlgorithm.HS256)
                .compact();
        String encoded = Base64.getUrlEncoder().encodeToString(jwt.getBytes());
        Response res = Response.status(Response.Status.OK)
                .entity(encoded).build();
        return res;
    }
}
