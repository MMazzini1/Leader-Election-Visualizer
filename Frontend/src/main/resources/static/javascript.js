$( document ).ready(function() {
    console.log( "ready!" );

    // find elements on the page
    var banner = $("#banner-message");
    var button = $("#submit_button");
    var searchBox = $("#search_text");
    var numResultsBox = $("#num_results");
    var minScoreBox = $("#min_score");
    var resultsTable = $("#results table tbody");
    var resultsWrapper = $("#results");
    var noResultsError = $("#no_results_error");


    const interval = setInterval(function() {
        console.log("Getting cluster state")
        getClusterState()
    }, 1000);


    // handle search click
    button.on("click", function(){
        banner.addClass("alt");
        console.log("KILLING NODE")

        // send request to the server
        getClusterState()
      });









    function getClusterState(){
        $.ajax({
            method : "GET",
            contentType: "application/json",
            url: "/cluster/summary",
            dataType: "json",
            success: onHttpResponse,
            error: onNotFound
        });
    }


    function createRequest() {
        var searchQuery = searchBox.val();
        var minScore = parseFloat(minScoreBox.val(), 10);
        if (isNaN(minScore)) {
            minScore = 0;
        }

        var maxNumberOfResults = parseInt(numResultsBox.val());

        if (isNaN(maxNumberOfResults)) {
            maxNumberOfResults = Number.MAX_SAFE_INTEGER;
        }

        // Search request to the server
        var frontEndRequest = {
            search_query: searchQuery,
            min_score: minScore,
            max_number_of_results: maxNumberOfResults
        };

        return JSON.stringify(frontEndRequest);
    }

    function onHttpResponse(data, status) {
        if (status === "success" ) {
            console.log(data);
            addResults(data)
        } else {
            alert("Error connecting to the server " + status);
        }
    }

    function onNotFound(data, status) {
        resultsTable.children( 'tr:not(:first)' ).remove();

        resultsTable.append("<tr>" +
            "<td id='aa'>" + "LEADER" +"</td>" +
            "<td>" + "UNAVAILABLE" + "</td>" +
            "<td>" + "REELECTION" + "</td>" +
            "<td>" + "IN"+ "</td>" +
            "<td>" + "PROGRESS" + "</td>" +
            "</tr>");

    }




    /*
        Add results from the server to the html or how an error message
     */
    function addResults(data) {

        resultsTable.children( 'tr:not(:first)' ).remove();

        data.forEach(data => {

            row = data
            var id = row.leaderElectionZnode;
            var followingId = row.predecessorLeaderElectionZnode;
            var status = row.clusterStatus
            var address = row.adress

            var button = document.createElement("button");
            button.innerText = "KILL";
            button.addEventListener("click", function() {
                $.ajax({
                    method : "POST",
                    contentType: "application/json",
                    url: "http://localhost:8080/kill?address=" + address,
                    dataType: "json",
                });
            })

            resultsTable.append("<tr>" +
                "<td id='aa'>" + id +"</td>" +
                "<td>" + status + "</td>" +
                "<td>" + followingId + "</td>" +
                "<td>" + address + "</td>" +
                "<td id=" + id + "></td>" +
                "</tr>");

            $("#" + id).append(button)



            // resultsTable.append(button);
        })

      /*  for (var i = 0 ; i < data.length; i++) {


            row = data[i]
            var id = row.leaderElectionZnode;
            var followingId = row.predecessorLeaderElectionZnode;
            var status = row.clusterStatus
            var address = row.adress


            resultsTable.append("<tr>" +
                "<td id='aa'>" + id +"</td>" +
                "<td>" + status + "</td>" +
                "<td>" + followingId + "</td>" +
                "<td>" + address + "</td>" +
                "<td> <div>\n" +
                "                    <button id=\"submit_button\">KILL</button>\n" +
                "                </div></td>" +
                + "</td>" +
                "</tr>");


       /!*     $("#aa").click(function ()
            {
                var test = $('<button/>',
                    {
                        text: 'Test',
                        click: function () { alert('hi'); }
                    });

                var parent = $('<tr><td></td></tr>').children().append(test).end();

                $("#addNodeTable tr:last").before(parent);
            });
*!/

        }
*/


    }
});
