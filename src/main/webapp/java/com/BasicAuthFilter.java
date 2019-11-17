package main.webapp.java.com;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Base64;
import java.util.HashMap;

@Provider
public class BasicAuthFilter implements ContainerRequestFilter {
    private HashMap<String, String> userPass;
    private static final String SECURED = "/secure-date";
    public BasicAuthFilter() {
        userPass = new HashMap<>();
        populateUserPass();
    }

    private void populateUserPass() {
        userPass.put("UserName", "passw0rd");
        userPass.put("tomcat", "s3cret");
    }


    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        if (containerRequestContext.getUriInfo().getPath().contains(SECURED)) {
            String auth = containerRequestContext.getHeaderString("Authorization");
            auth = auth.trim();
            int ind = auth.indexOf(" ") + 1;
            String user = auth.substring(ind);
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
                containerRequestContext.abortWith(res);
            } else {
                System.out.println("==============> Sending request");
                Client c = ClientBuilder.newClient();
                Response res = c.target("https://www.google.com").request().get();
                System.out.println("==============> " + res);
                Response r = Response.status(res.getStatus())
                        .entity(res.getEntity())
                        .build();
                containerRequestContext.abortWith(r);
            }
        }
    }
}
