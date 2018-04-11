Norint paleisti servisą reikia paleisiti komandas:

`docker build -t webservisas:1 .`

`docker run -d -p 5000:5000 webservisas:1`

Servisas pasiekiamas: http://localhost:5000/locations
Šiuo adresu pasiekiami visų lokacijų duomenys

Gauti visas lokacijas pagal miestą su get metodu : http://localhost:5000/locations/byCity/{name_of_city}

Gauti visas konkrečią lokaciją pagal id su get metodu : http://localhost:5000/locations/{location_ID}

Ištrinti lokaciją pagal nurodytą ID su delete metodu: http://localhost:5000/locations/{location_ID}

Atnaujinti lokaciją pagal nurodytą ID su put metodu: http://localhost:5000/locations/{location_ID}


body nurodyti, duomenų struktūrą pvz:
```JSON
{
  "temperature": 19,
  "city": "Vilnius",
  "date": "2018-02-05"
}
```

Pridėti lokacija su post metodu : http://localhost:5000/locations/ 


body nurodyti, duomenų struktūrą pvz:
```JSON
{
  "temperature": -20,
  "city": "Praha",
  "date": "2018-12-31"
}
```
