package web.servisas;

import static spark.Spark.*;

import java.io.IOException;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class Main {
   
	public static void main(String[] args) throws IOException {
		OruServisas service = new OruServisas();
		port(5000);

		path("/locations", () -> {

			get("", (request, response) -> {
				return service.getAllData(request, response);
			}, new JsonTransformer());

			get("/byCity/:city", (request, response) -> {
				return service.getCities(request, response);
			}, new JsonTransformer());

			get("/:id", (request, response) -> {
				return service.getWithId(request, response);
			}, new JsonTransformer());

			post("", (request, response) -> {
				return service.postData(request, response);
			} , new JsonTransformer());

			put("/:id", (request, response) -> {
				return service.putData(request, response);
			}, new JsonTransformer());

			delete("/:id", (request, response) -> {
				return service.deleteData(request, response);
			}, new JsonTransformer());

        	});
	
		exception(Exception.class, (e, request, response) -> {
			response.status(HTTP_BAD_REQUEST);
			e.printStackTrace();
		});
	
		after((request, response) -> response.type("application/json"));

	}
    
}
