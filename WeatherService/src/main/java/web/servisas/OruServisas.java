package web.servisas;

import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class OruServisas {

    	private static final int HTTP_NOT_FOUND = 404;
	private static final int POST_CREATED_RESPONSE = 201;
	//private static final int HTTP_CONFLICT = 409;
	private static final int BAD_REQUEST = 400;
	private static ServiceContainer sc = new ServiceContainer();
	OruServisas() {}

	public static Object getAllData(Request request, Response response) {
 		return sc.getAll();	
	}

 	public static String postData(Request request, Response response) {
		try{
			if (request.body().replaceAll("\\s+","").length() == 2)
				throw new Exception("Klaida! Tuscias elementas");
		}catch(Exception e){
			response.status(BAD_REQUEST);
			return "Klaida! Tuscias elementas";
		}
		JSONObject jsonObj = new JSONObject(request.body());
		try{
			if(jsonObj.isNull("temperature"))
				throw new Exception("Klaida! neįvedėte temperaturos");
		}catch(Exception e){
			response.status(BAD_REQUEST);
			return "Klaida! neįvedėte temperaturos";
		}
		try{
			if (!(jsonObj.isNull("date")) && !(isValidDate(jsonObj.optString("date"))))
				throw new Exception("Klaida! Blogas datos formatas");
			CityData cityObject = JsonTransformer.fromJson(request.body(), CityData.class);
			int addedId = sc.addData(cityObject); // addData nemeta jokio exception
			response.status(POST_CREATED_RESPONSE);
			response.header("Location", request.url() + "/" + String.valueOf(addedId));
 			return "Created";
		} catch (Exception e) {
			response.status(BAD_REQUEST);
		    	return "Klaida! Blogai nurodytas formatas";
        	}	
	}

	public static String putData(Request request, Response response) {
		try{
			if (request.body().replaceAll("\\s+","").length() == 2)
				throw new Exception("Klaida! Tuscias elementas");
		}catch(Exception e){
			response.status(BAD_REQUEST);
			return "Klaida! Tuscias elementas";
		}
		JSONObject jsonObj = new JSONObject(request.body());
		try{
			if(jsonObj.isNull("temperature"))
				throw new Exception("Klaida! neįvedėte temperaturos");
		}catch(Exception e){
			response.status(BAD_REQUEST);
			return "Klaida! neįvedėte temperaturos";
		}
		CityData cityObject;
		try{
			cityObject = JsonTransformer.fromJson(request.body(), CityData.class);
			if (!(jsonObj.isNull("date")) && !(isValidDate(jsonObj.optString("date"))))
				throw new Exception("Klaida! Blogas datos formatas");
		}catch (Exception e){
			response.status(BAD_REQUEST);
		    	return "Klaida! Blogai nurodytas formatas";
		}
		try{
            		int id = Integer.valueOf(request.params(":id"));
			sc.upData(id, cityObject);
 			return "Ok";
		} catch (Exception e) {
			response.status(HTTP_NOT_FOUND);
		    	return "Nepavyko rasti duomenų su tokiu id: " + request.params(":id");
        	}	
	}

	public static String deleteData(Request request, Response response) {
		try{
			sc.delData(Integer.valueOf(request.params(":id")));
	 		return "Ok";
		} catch (Exception e) {
			response.status(HTTP_NOT_FOUND);
		    	return "Nepavyko rasti duomenų su tokiu id: " + request.params(":id");
		}	
	}

	public static Object getCities(Request request, Response response){
		try{
			return sc.getUsingCity(request.params(":city"));
		} catch (Exception e) {
			response.status(HTTP_NOT_FOUND);
		    	return "Nepavyko rasti duomenų su tokiu miestu: " + request.params(":city");
		}	
	}
	
	public static Object getWithId(Request request, Response response) {
		try{
			return sc.getUsingId(Integer.valueOf(request.params(":id")));
		} catch (Exception e) {
			response.status(HTTP_NOT_FOUND);
		    	return "Nepavyko rasti duomenų su tokiu id: " + request.params(":id");
		}	
	}

	public static boolean isValidDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate);
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}
}
