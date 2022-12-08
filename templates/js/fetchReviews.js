async function fetchReviews(hotelId) {
    let response = await fetch('/reviews'+'?hotelId='+hotelId, {method: 'get'});

    let infoJSON = await response.json();
    console.log(infoJSON);

    let reviewsArray = infoJSON["hotelReviews"]["reviews"];
    console.log(reviewsArray);

    document.getElementById("tableBody").innerHTML = '';
    tbody = document.getElementById("tableBody");


    for (let i = 0; i < reviewsArray.length; i++){
        let newRow = tbody.insertRow(tbody.length);
        // Review Id, Review Date, Review Author, Review Rating, Review Title, Review Text
        let tempJSON = reviewsArray[i]
        console.log(tempJSON)
        let idCell = newRow.insertCell(0);
        idCell.innerHTML = reviewsArray[i]["reviewId"]

        let dateCell = newRow.insertCell(1);
        dateCell.innerHTML = reviewsArray[i]["date"]

        let userCell = newRow.insertCell(2)
        let user = tempJSON["user"]
        userCell.innerHTML = user;

        let ratingCell = newRow.insertCell(3)
        ratingCell.innerHTML = reviewsArray[i]["rating"]

        let titleCell = newRow.insertCell(4)
        titleCell.innerHTML = reviewsArray[i]["title"]

        let textCell = newRow.insertCell(5)
        textCell.innerHTML = reviewsArray[i]["reviewText"]
    }





}