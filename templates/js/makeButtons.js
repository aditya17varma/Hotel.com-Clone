function makeButtons(reviewCount, hotelId) {
    let ul = document.getElementById("pagination-table");

    console.log(reviewCount)
    let numButtons = reviewCount / 5
    let mod = reviewCount % 5
    if (mod > 0){
        numButtons += 1;
    }

    console.log(numButtons)

    for (let i = 0; i < numButtons + 1; i++){
        let li = document.createElement("li");
        li.appendChild(document.createTextNode((i + 1).toString()))
        li.setAttribute("href", "#")
        li.setAttribute("class","page-link")
        li.setAttribute("onlclick", fetchReviews('$hotelId', i))
        // li.addEventListener("click", fetchReviews(hotelId, i), false)
        ul.appendChild(li)

    }



}