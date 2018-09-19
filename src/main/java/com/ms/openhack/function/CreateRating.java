package com.ms.openhack.function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.openhack.model.Rating;
import org.apache.commons.lang3.Range;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Azure Functions with HTTP Trigger.
 */
public class CreateRating {
    /**
     * This function listens at endpoint "/api/CreateRating". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/CreateRating
     * 2. curl {your host}/api/CreateRating?name=HTTP%20Query
     */

    @FunctionName("CreateRating")

    public HttpResponseMessage HttpTriggerJava(
            @CosmosDBOutput(name = "items",
                    databaseName = "openhackdb",
                    collectionName = "openhackcollection",
                    connectionStringSetting = "AzureCosmosDBConnectionString",
                    createIfNotExists = true)
                    OutputBinding<String> document,
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String body = request.getBody().get();
        context.getLogger().info(body);
        String documentContent = "";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Rating objRating = gson.fromJson(body, Rating.class);
        context.getLogger().info(objRating.toString());
        boolean isUserExist = this.getUser(objRating.getUserId());
        boolean isProductExist = this.getProduct(objRating.getProductId());
        Range<Integer> ratingRange = Range.between(0, 5);
        boolean validateRatingRange = ratingRange.contains(objRating.getRating());
        boolean finalValidation = isUserExist && isProductExist && validateRatingRange;
        context.getLogger().info("FINAL VALIDATION: "+finalValidation);
        if(finalValidation) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UUID guid = UUID.randomUUID();
            Date date = new Date();
            String timestamp = formatter.format(date);
            objRating.setId(guid.toString());
            objRating.setTimestamp(timestamp);
            context.getLogger().info("JSON:"+gson.toJson(objRating));
            documentContent = gson.toJson(objRating);
            document.setValue(documentContent);

        }
            //        String objProducts = this.getProducts();
//        TypeToken<List<Product>> token = new TypeToken<List<Product>>() {};
//        List<Product> products = gson.fromJson(objProducts, token.getType());
//        products.forEach(product->{
//            if(product.getProductId() == objRating.getProductid() )
//        });
        return request.createResponseBuilder(HttpStatus.OK).header("content-type", "application/json").body(documentContent).build();
    }


//    private String getDocument(String id, String timestamp, Rating objRating, ExecutionContext context) {
//        String document =  "{guid: " + "1" +
//                ", timestamp: " + timestamp +
//                ", userId: " + objRating.getUserId() +
//                ", productId: " + objRating.getProductId() +
//                ", locationName: " + objRating.getLocationName() +
//                ", rating: " + objRating.getRating() +
//                ", userNotes: " + objRating.getUserNotes() +
//                " }";
//
//
//
////        String document =  "{guid: " + "1" +
////                ", timestamp: " + "1" +
////                ", userId: " + "1" +
////                ", productId: " + "1" +
////                ", locationName: " + "1" +
////                ", rating: " + "1" +
////                ", userNotes: " + "1" +
////                " }";
//        context.getLogger().info("PRINTING DOCUMENT:"+document);
//        return document;
//
//    }



    private boolean getUser(String userId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        String userResponse = this.doHttpGet("https", "serverlessohuser.trafficmanager.net", "/api/GetUser", queryParams);
        return true;
    }

    private boolean getProduct(String productId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        String userResponse = this.doHttpGet("https", "serverlessohproduct.trafficmanager.net", "/api/GetProduct", queryParams);
        return true;
    }

    private String doHttpGet(String httpSchema, String host, String path, Map<String, String> queryParams) {
        StringBuffer result = new StringBuffer();
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme(httpSchema).setHost(host).setPath(path);
            queryParams.forEach((key, val) -> {
                builder.setParameter(key, val);
            });
            URI uri = builder.build();
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
        catch (Exception e) {
            System.out.println("Exception in doHttpGet: "+e);
        }
        return result.toString();

    }
    private String getProducts() {
        StringBuffer result = new StringBuffer();
        try {
            String url = "https://serverlessohproduct.trafficmanager.net/api/GetProducts";
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", "");
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
        catch (Exception e) {
            System.out.print("Exception: "+ e.toString());
        }
        finally {
            return result.toString();
        }
    }
}
