async function loadMap(lat, long, hotelName) {

    let latitude = parseFloat(lat);
    console.log(latitude)
    let longitude = parseFloat(long);
    console.log(longitude)

    const map = L.map('map', {
        center: [latitude, longitude],
        zoom: 13.5
    });


    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    var marker = L.marker([latitude, longitude]).addTo(map);

    var popup = L.popup()
        .setLatLng([latitude, longitude])
        .setContent(hotelName)
        .openOn(map);

}