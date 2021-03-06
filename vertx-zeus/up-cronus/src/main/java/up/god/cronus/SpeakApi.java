package up.god.cronus;

import io.vertx.core.json.JsonObject;
import io.vertx.up.annotations.EndPoint;

import javax.ws.rs.BodyParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@EndPoint
@Path("/cronus")
public class SpeakApi {

    @Path("/speak")
    @POST
    public JsonObject speak(@BodyParam final JsonObject data) {
        System.out.println("Hello: " + data.encode());
        return data;
    }
}
