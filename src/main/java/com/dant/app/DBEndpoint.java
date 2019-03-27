package com.dant.app;

import com.dant.entity.ResponseError;
import com.dant.entity.TableEntity;
import com.dant.exception.BadRequestException;
import com.dant.utils.Log;
import db.structure.Database;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;


@Path("/db")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DBEndpoint {

    @PUT
    @Path("/tables")
    public Response createTables(ArrayList<TableEntity> allTables, @DefaultValue("null") @HeaderParam("InternalToken") String InternalToken) {
        boolean addImmediatly = InternalToken.equals(Database.getInstance().config.SuperSecretPassphrase);
        try {
            ArrayList<ResponseError> allErrors = Database.getInstance().addTables(allTables, addImmediatly);
            if (allErrors != null)
                return com.dant.utils.Utils.generateResponse(400, "error", "application/json", allErrors);

            if (!addImmediatly)
               db.network.Network.broadcast("zbeb", allTables);

            return com.dant.utils.Utils.generateResponse(200, "ok", "application/json", "table successfully inserted");
        } catch (Exception exp) { //Todo improve on error handling by catching only specific exception types
            Log.error(exp);
            throw new BadRequestException("invalid table body, check your args");
        }
    }

    @GET
    @Path("/tables")
    public Response getTables() {
        return Database.getInstance().getTables();
    }

    @GET
    @Path("/tables/{tableName}")
    public Response getTable(@PathParam("tableName") String tableName) {
        return Database.getInstance().getTable(tableName);
    }

    @DELETE
    @Path("/tables/{tableName}")
    public Response deleteTable(@PathParam("tableName") String tableName) {
        return Database.getInstance().deleteTable(tableName);
    }
}
