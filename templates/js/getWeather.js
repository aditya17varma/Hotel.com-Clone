async function getWeather(hotelId) {
    let response = await fetch('/weather'+'?hotelId='+hotelId, {method: 'get'})

    let weatherJSON = await response.json();
    console.log(weatherJSON)

    let currentWeather = JSON.stringify(weatherJSON["current_weather"]);


    document.getElementById("weather").innerHTML = currentWeather;

}