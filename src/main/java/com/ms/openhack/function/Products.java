package com.ms.openhack.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;


/**
 * Azure Functions with HTTP Trigger.
 */
public class Products {
    /**
     * This function listens at endpoint "/api/Products". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/Products
     * 2. curl {your host}/api/Products?name=HTTP%20Query
     */
    @FunctionName("Products")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
            route = "products/{productId}")
                    HttpRequestMessage<Optional<String>> request,
            @BindingName("productId") String productId,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {

            return request.createResponseBuilder(HttpStatus.OK).body(productId.toString()).build();
        }
    }


}
