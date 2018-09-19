package com.ms.openhack.function;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GetRatings {
    /**
     * This function listens at endpoint "/api/GetRatings". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GetRatings
     * 2. curl {your host}/api/GetRatings?name=HTTP%20Query
     */
    @FunctionName("GetRatings")
    public HttpResponseMessage HttpTriggerJava(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
//            @CosmosDBInput(name = "items",
//                    databaseName = "openhackdb",
//                    collectionName = "openhackcollection",
//                    connectionStringSetting = "AzureCosmosDBConnectionString"
//                    sqlQuery = "select * from items where items.userId = {Query.id}"
//            ) DocumentClient documentClient,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String cosmosDBUri = System.getenv("DOCUMENTDB_URI");
        String cosmosDBKey = System.getenv("DOCUMENTDB_KEY");
        String cosmosDbConnectionString = System.getenv("AzureCosmosDBConnectionString");
        String pattern = "AccountEndpoint=(.*);AccountKey=(.*);";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cosmosDbConnectionString);
        if (m.find()) {
            cosmosDBUri = m.group(1);
            cosmosDBKey = m.group(2);
        }
        // Parse query parameter
        String query = request.getQueryParameters().get("userId");
        String userId = request.getBody().orElse(query);
        DocumentClient documentClient = new DocumentClient(cosmosDBUri, cosmosDBKey, null, null);
        List<Document> results = documentClient
                .queryDocuments(
                        "dbs/" + "openhackdb" + "/colls/" + "openhackcollection",
                        "SELECT * FROM items WHERE items.userId = '"+ userId+ "'",
                        null).getQueryIterable().toList();

        return request.createResponseBuilder(HttpStatus.OK).header("content-type", "application/json").body(results.toString()).build();


    }
}
