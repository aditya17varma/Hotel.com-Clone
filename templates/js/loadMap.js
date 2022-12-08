async function loadMap(lat, long, hotelName) {

    let latitude = parseFloat(lat);
    console.log(latitude)
    let longitude = parseFloat(long);
    console.log(longitude)

    const map = L.map('map', {
        center: [latitude, longitude],
        zoom: 5
    });

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    var marker = L.marker([latitude, longitude]).addTo(map);

    var popup = L.popup()
        .setLatLng([latitude, longitude])
        .setContent(hotelName)
        .openOn(map);

}