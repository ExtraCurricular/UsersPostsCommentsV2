package web.servisas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;


public class JsonTransformer implements ResponseTransformer {

	private static Gson gson = new Gson();

	@Override
	public String render(Object model) {
		return gson.toJson(model);
	}
	
	public static <T extends Object> T  fromJson(String json, Class<T> classe) {
		return gson.fromJson(json, classe);
    	}

}
