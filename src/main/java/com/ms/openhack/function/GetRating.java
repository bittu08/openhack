package com.ms.openhack.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GetRating {
    /**
     * This function listens at endpoint "/api/GetRating". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GetRating
     * 2. curl {your host}/api/GetRating?name=HTTP%20Query
     */
    @FunctionName("GetRating")
    public HttpResponseMessage HttpTriggerJava(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(name = "items",
                    databaseName = "openhackdb",
                    collectionName = "openhackcollection",
                    connectionStringSetting = "AzureCosmosDBConnectionString",
                    id = "{Query.ratingId}"
                    ) Optional<String> item,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        return request.createResponseBuilder(HttpStatus.OK).header("content-type", "application/json").body(item.orElse("Not found")).build();
    }
}
