package web.servisas;

import java.util.ArrayList;

public class ServiceContainer{

	private ArrayList<CityData> cityArray = new ArrayList<CityData>();

	ServiceContainer(){
		cityArray.add(new CityData(1, 0, "Vilnius", "2018-02-02"));
		cityArray.add(new CityData(2, 15, "Kaunas", "2018-04-02"));
		cityArray.add(new CityData(3, -20, "Vilnius", "2018-12-30"));
	}
	
	public void delData(int id) throws Exception{
		boolean check=false;
		for (int i = 0; i < cityArray.size(); i++) {
			if(cityArray.get(i).getId() == id){
				check=true;
				cityArray.remove(i);
				break;
			}
		}
		if(check==false){throw new Exception("Tokios reikšmės nėra");}
	}
	
	public void upData(int id, CityData cityObj) throws Exception {
		boolean check=false;
		for (int i = 0; i < cityArray.size(); i++) {
			if(cityArray.get(i).getId() == id){
				check=true;
				cityObj.setId(id);
				cityArray.set(i, cityObj);
				break;
			}
		}
		if(check==false){throw new Exception("Tokios reikšmės nėra");}
	}
	public Object getUsingId(int id) throws Exception{
		boolean check=false;
		int index=-1;
		for (int i = 0; i < cityArray.size(); i++) {
			if(cityArray.get(i).getId() == id){
				check=true;
				index = i;
				break;
			}
		}
		if(check==false){throw new Exception("Tokios reikšmės nėra");}
		return cityArray.get(index);
	}

	public int addData(CityData cityObj)  {
		int id = cityArray.size()==0 ? 1 : cityArray.get(cityArray.size()-1).getId()+1;
		cityObj.setId(id);
		cityArray.add(cityObj);
		return id;
	}

	public ArrayList<CityData> getAll(){
		return cityArray;
	}

	public ArrayList<CityData> getUsingCity(String s) throws Exception {
		boolean check=false;
		ArrayList<CityData> cities = new ArrayList<CityData>();
		for (int i = 0; i < cityArray.size(); i++) {
			if(cityArray.get(i).getCity() != null && cityArray.get(i).getCity().equalsIgnoreCase(s)){
				check=true;
				cities.add(cityArray.get(i));
			}
		}
		if(check==false){throw new Exception("Nerasta nei vieno tokio miesto");}
		return cities;
	}

}
