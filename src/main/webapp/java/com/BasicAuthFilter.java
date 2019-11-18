package main.webapp.java.com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import main.webapp.java.KeyGenerator;
import org.reflections.Reflections;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.json.Json;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.*;

@Provider
public class BasicAuthFilter implements ContainerRequestFilter {
    private HashMap<String, String> userPass;
    private List<String> securedResources;

    public BasicAuthFilter() throws IOException {
        populateUserPass();
        getSecuredResources();
    }

    private void populateUserPass() {
        userPass = new HashMap<>();
        userPass.put("UserName", "passw0rd");
        userPass.put("tomcat", "s3cret");
    }

    private void getSecuredResources() throws IOException {
        securedResources = new ArrayList<>();
        Reflections reflections = new Reflections("main.webapp.java.com");
        Set<Class<?>> securedClasses = reflections.getTypesAnnotatedWith(Resource.class);
        System.out.println("===========> Secured classes were:" + securedClasses);
        for (Class cls : securedClasses) {
            for (Method m : cls.getMethods()) {
                Secured sCls = m.getAnnotation(Secured.class);
                if (sCls != null) {
                    Path p = m.getAnnotation(Path.class);
                    securedResources.add(p.value());
                }
            }
        }
    }


    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        if (isSecuredResource(containerRequestContext.getUriInfo().getPath())) {
            String auth = containerRequestContext.getHeaderString("Authorization");
            int ind = auth.indexOf(" ") + 1;
            String authType = auth.substring(0, ind);
            auth = auth.trim();
            String token = auth.substring(ind);
            if ("basic".equalsIgnoreCase(authType)){
                performBasicAuth(containerRequestContext,token);
            } else {
                try {
                    performBearerAuth(containerRequestContext, token);
                    return;
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

        }
    }

    private boolean isSecuredResource(String url) {
        if (securedResources == null || securedResources.size() == 0 ) {
            return true;
        } else {
            for (String res : securedResources) {
                if (url.contains(res)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void performBasicAuth(ContainerRequestContext ctx, String user) {
        byte[] decoded = Base64.getDecoder().decode(user);
        StringBuilder sb = new StringBuilder();
        for (byte b : decoded) {
            sb.append((char) b);
        }
        String s = sb.toString();
        String[] parts = s.split(":");
        String val = userPass.get(parts[0]);
        if (!(val != null && val.equals(parts[1]))) {
            Response res = Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User cannot access the resource")
                    .build();
            ctx.abortWith(res);
        } else {
            System.out.println("==============> Sending request");
            Client c = ClientBuilder.newClient();
            Response res = c.target("https://www.google.com").request().get();
            System.out.println("==============> " + res);
            Response r = Response.status(res.getStatus())
                    .entity(res.getEntity())
                    .build();
            ctx.abortWith(r);
        }
    }

    private void performBearerAuth(ContainerRequestContext ctx, String token) throws IOException {
        SecretKey pk = KeyGenerator.getPrivateKey();
        System.out.println(token);
        byte[] bytes = Base64.getDecoder().decode(token);
        String base64Token = new String(bytes);
        System.out.println(base64Token);
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(pk)
                .parseClaimsJws(base64Token);
        Date exp = jws.getBody().getExpiration();

        if (Date.from(Instant.now()).after(exp)) {
            Response r = Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Error validating the JWT")
                    .build();
            ctx.abortWith(r);
        }
    }
}
