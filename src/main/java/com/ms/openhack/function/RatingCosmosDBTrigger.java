package com.ms.openhack.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Cosmos DB trigger.
 */
public class RatingCosmosDBTrigger {
    /**
     * This function will be invoked when there are inserts or updates in the specified database and collection.
     */
//    @FunctionName("RatingCosmosDBTrigger")
//    public void cosmosDBHandler(
//        @CosmosDBTrigger(
//            name = "items",
//            databaseName = "openhackdb",
//            collectionName = "openhackcollection",
//            leaseCollectionName="leaseopenhackcollection",
//            connectionStringSetting = "AccountEndpoint=https://openhack.documents.azure.com:443/;AccountKey=GWGt2fOEjo0L1qQPQwISLYRHkK8zIRo3hN0Mizk3EIUfA6Ah9KVvZIR12uG36UfvmwTMd8cvR2u2eQr3l9wSLQ==;",
//            createLeaseCollectionIfNotExists = true
//        )
//        Object[] items,
//        final ExecutionContext context
//    ) {
//        context.getLogger().info("Java Cosmos DB trigger function executed.");
//        context.getLogger().info("Documents count: " + items.length);
//    }
}
