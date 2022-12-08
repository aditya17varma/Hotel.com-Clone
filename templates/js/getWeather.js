async function getWeather(hotelId) {
    let response = await fetch('/weather'+'?hotelId='+hotelId, {method: 'get'})

    let weatherJSON = await response.json();

    let currentWeather = weatherJSON["current_weather"];
    console.log(currentWeather)

    let heading = "<h4>Current Weather</h4><br>"
    let temp = "<b>Temperature: " + JSON.stringify(currentWeather["temperature"]) + "</b><br>";
    let wind = "<b>WindSpeed: " + JSON.stringify(currentWeather["windspeed"]) + "</b><br>";
    let windDir = "<b>Wind Direction: " + JSON.stringify(currentWeather["winddirection"]) + "</b><br>";

    let result = heading.concat(temp, wind, windDir);
    console.log(result)

    document.getElementById("weather").innerHTML = result;

}