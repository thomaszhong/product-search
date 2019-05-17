app.controller("myCtrl", function($scope, $http, $window) {
    //Initialize values
    $scope.category = "default";
    $scope.location = "here";
    $scope.tableDisplay = "results";
    $scope.detailView = false;
    $scope.displayResults = true;
    //Get dummy array function
    $scope.getNumber = function(num) {
        return new Array(num);
    };
    //Sleep function
    $scope.sleep = function(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    };
    //Get current location
    $http.get("http://ip-api.com/json")
        .then(function(response) {
            $scope.currZipCode = response.data["zip"];
        });
    //Auto complete zip code function
    $scope.getZipCode = function(text) {
        return $http.get("http://productsearch-nodejs-env.4p5p7nryyd.us-west-1.elasticbeanstalk.com/getZipCode", {params: {"zipCode" : text}})
            .then(function(response) {
                return response.data;
            });
    };
    //Reset button
    $scope.reset = async function() {
        $scope.displayResults = false;
        //Input form
        $scope.inputForm.keyword.$touched = false;
        $scope.keyword = "";
        $scope.category = "default";
        $scope.new = false;
        $scope.used = false;
        $scope.unspecified = false;
        $scope.local = false;
        $scope.free = false;
        $scope.distance = "";
        $scope.location = "here";
        $scope.selectedItem = "";
        $scope.searchText = "";
        $scope.inputForm.zipCode.$touched = false;
        //Search results
        $scope.tableDisplay = "results";
        $scope.detailView = false;
        $scope.itemsAll = undefined;
        $scope.itemsList = undefined;
        $scope.numOfPages = undefined;
        $scope.currPage = undefined;
        $scope.itemId = undefined;
        $scope.itemOtherInfo = undefined;
        $scope.itemDetails = undefined;
        $scope.searchPhotoList = undefined;
        $scope.hasGooglePhotos = false;
        $scope.similarItemsList = undefined;
        $scope.similarItemsListOriginal = undefined;
        $scope.currTab = undefined;
        $scope.starColor = undefined;
        $scope.showAllSimilarItems = undefined;
        $scope.sortBy = undefined;
        await $scope.sleep(400);
        $scope.displayResults = true;
    };
    //Search products table and pagination
    $scope.searchProducts = function() {
        $scope.showProgressBar = true;
        $scope.detailView = false;
        $scope.tableDisplay = "results";
        var zipCode = $scope.searchText ? $scope.searchText : $scope.currZipCode;
        var distance = $scope.distance ? $scope.distance : 10;
        $http.get("http://productsearch-nodejs-env.4p5p7nryyd.us-west-1.elasticbeanstalk.com/searchProducts", {params: {"keyword" : $scope.keyword, "category" : $scope.category,
                "new" : $scope.new, "used" : $scope.used, "unspecified" : $scope.unspecified, "local" : $scope.local,
                "free" : $scope.free, "distance" : distance, "zipCode" : zipCode}})
            .then(function(response) {
                //Slice response into group of 10
                $scope.itemsAll = [];
                var i, itemsJSON = response.data;
                for (i = 0; i < itemsJSON.length; i += 10) {
                    var tempArray = itemsJSON.slice(i, i + 10);
                    $scope.itemsAll.push(tempArray);
                }
                $scope.numOfPages = $scope.itemsAll.length;
                $scope.itemsList = $scope.itemsAll[0];
                $scope.currPage = 0;
                $scope.showProgressBar = false;
            });
    };
    $scope.getPage = function(pageNumber) {
        $scope.itemsList = $scope.itemsAll[pageNumber];
        $scope.currPage = pageNumber;
    };
    //Share item with Facebook
    $scope.shareWithFacebook = function() {
        var shareURL = "https://www.facebook.com/sharer/sharer.php?quote="
            + encodeURIComponent("Buy " + $scope.itemDetails["title"] + " at $" + $scope.itemDetails["price"] + " from link below")
            + "&u=" + $scope.itemDetails["itemURL"];
        window.open(shareURL);
    };
    //Item details table
    $scope.getItemDetails = function(title, itemId, index, source) {
        $scope.showProgressBarSlide = true;
        $scope.detailView = true;
        if (source == 'results') {
            $scope.itemOtherInfo = $scope.itemsList[index];
        } else {
            $scope.itemOtherInfo = $scope.localStorageList[index];
        }
        $scope.itemId = itemId;
        $http.get("http://productsearch-nodejs-env.4p5p7nryyd.us-west-1.elasticbeanstalk.com/getItemDetails", {params: {"itemId" : itemId}})
            .then(function(response) {
               $scope.itemDetails = response.data;
            });
        $http.get("http://productsearch-nodejs-env.4p5p7nryyd.us-west-1.elasticbeanstalk.com/getGoogleSearchPhotos", {params: {"title" : title}})
            .then(function(response) {
                var searchPhotosList = response.data;
                if (searchPhotosList.length > 0) {
                    $scope.hasGooglePhotos = true;
                    $scope.searchPhotoList = [
                        [searchPhotosList[0], searchPhotosList[3]],
                        [searchPhotosList[1], searchPhotosList[4], searchPhotosList[6]],
                        [searchPhotosList[2], searchPhotosList[5], searchPhotosList[7]]
                    ];
                } else {
                    $scope.hasGooglePhotos = false;
                }
            });
        $http.get("http://productsearch-nodejs-env.4p5p7nryyd.us-west-1.elasticbeanstalk.com/getSimilarItems", {params: {"itemId" : itemId}})
            .then(function(response) {
                $scope.similarItemsList = response.data;
                $scope.similarItemsListOriginal = $scope.similarItemsList.slice();
                $scope.showProgressBarSlide = false;
            });
        $scope.currTab = "product";
        var score = parseFloat($scope.itemOtherInfo["feedbackScore"]);
        if (score < 10) {
            $scope.starColor = "no-star";
        } else if (score < 50) {
            $scope.starColor = "yellow-star";
        } else if (score < 100) {
            $scope.starColor = "blue-star";
        } else if (score < 500) {
            $scope.starColor = "turquoise-star";
        } else if (score < 1000) {
            $scope.starColor = "purple-star";
        } else if (score < 5000) {
            $scope.starColor = "red-star";
        } else if (score < 10000){
            $scope.starColor = "green-star";
        } else if (score < 25000) {
            $scope.starColor = "yellow-star";
        } else if (score < 50000) {
            $scope.starColor = "turquoise-star";
        } else if (score < 100000) {
            $scope.starColor = "purple-star";
        } else if (score < 500000) {
            $scope.starColor = "red-star";
        } else if (score < 1000000) {
            $scope.starColor = "green-star";
        } else {
            $scope.starColor = "silver-star";
        }
    };
    $scope.getTab = function(tab) {
        if (tab == "similarProducts") {
            $scope.showAllSimilarItems = false;
            $scope.sortBy = {property : "default", order : "ascending"};
        }
        $scope.currTab = tab;
    };
    $scope.openStoreURL = function() {
        $window.open($scope.itemOtherInfo["buyProductAt"]);
    };
    $scope.openItemURL = function (itemURL) {
        $window.open(itemURL);
    };
    $scope.toggleSimilarItemsView = function() {
        $scope.showAllSimilarItems = !$scope.showAllSimilarItems;
    };
    $scope.sortSimilarItems = function() {
        if ($scope.sortBy.property == "default") {
            $scope.similarItemsList = $scope.similarItemsListOriginal.slice();
        } else {
            $scope.similarItemsList.sort(function(a, b) {
                var multiplier;
                switch ($scope.sortBy.order) {
                    case "ascending":
                        multiplier = 1;
                        break;
                    default:
                        multiplier = -1;
                        break;
                }
                switch ($scope.sortBy.property) {
                    case "title":
                        return multiplier * (a["title"].localeCompare(b["title"]));
                    case "days":
                        return multiplier * (parseFloat(a["daysLeft"]) - parseFloat(b["daysLeft"]));
                    case "price":
                        return multiplier * (parseFloat(a["price"]) - parseFloat(b["price"]));
                    default:
                        return multiplier * (parseFloat(a["shipping"]) - parseFloat(b["shipping"]));
                }
            });
        }
    };
    $scope.toggleListDetailView = function () {
        $scope.detailView = !$scope.detailView;
    };
    //Local Storage
    $scope.loadLocalStorage = function() {
        var tempList = [];
        var sum = 0;
        for (var i = 0; i < localStorage.length; i++) {
            var item = JSON.parse(localStorage.getItem(localStorage.key(i)));
            sum += parseFloat(item["price"]);
            tempList.push(item);
        }
        $scope.localStorageList = tempList;
        $scope.localStorageSum = sum;
    };
    $scope.setLocalStorage = function(index) {
        var tempItem;
        if (index == -1) {
            tempItem = $scope.itemOtherInfo;
        } else {
            tempItem = $scope.itemsList[index];
        }
        var itemId = tempItem["itemId"];
        localStorage.setItem(itemId, JSON.stringify(tempItem));
        $scope.loadLocalStorage();
    };
    $scope.removeLocalStorage = function(itemId) {
        localStorage.removeItem(itemId);
        $scope.loadLocalStorage();
    };
    $scope.containsLocalStorage = function(itemId) {
        return localStorage.getItem(itemId) != null;
    };
    $scope.loadLocalStorage();
});