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
        // Review Id
        // <th>Review Date</th>
        // <th>Review Author</th>
        // <th>Review Rating</th>
        // <th>Review Title</th>
        // <th>Review Text<
        console.log(reviewsArray[i])
        let idCell = newRow.insertCell(0);
        idCell.innerHTML = reviewsArray[i]["reviewId"]

        let dateCell = newRow.insertCell(1);
        dateCell.innerHTML = reviewsArray[i]["date"]

        let userCell = newRow.insertCell(2)
        userCell = reviewsArray[i]["user"]

        let titleCell = newRow.insertCell(3)
        titleCell.innerHTML = reviewsArray[i]["title"]

        let textCell = newRow.insertCell(4)
        textCell.innerHTML = reviewsArray[i]["reviewText"]
    }





}