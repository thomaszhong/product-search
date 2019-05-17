//Import modules
var express = require("express");
var bodyParser = require("body-parser");
var request = require("request");
var cors = require("cors");

//Configure express
var app = express();
app.use(bodyParser.json());
app.use(cors());
//Get requests
app.get("/getZipCode", function(req, res) {
    console.log("get zip code called");
    var url = "http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith=" + req.query.zipCode
        + "&username=zhongxuwei1994&country=US&maxRows=5";
    request(url, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            var zipCodesJSON = JSON.parse(body);
            zipCodesJSON = zipCodesJSON["postalCodes"];
            var zipCodesList = [];
            zipCodesJSON.forEach(function(entry) {
                zipCodesList.push(entry["postalCode"]);
            });
            res.send(JSON.stringify(zipCodesList));
        }
    });
});
app.get("/searchProducts", function(req, res) {
    var url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0"
        + "&SECURITY-APPNAME=XuweiZho-productS-PRD-da6d4ee64-da7baa08&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=50"
        + "&keywords=" + encodeURIComponent(req.query.keyword);
    if (req.query.category != "default") {
        var categoryId;
        switch (req.query.category) {
            case "art":
                categoryId = 550;
                break;
            case "baby":
                categoryId = 2984;
                break;
            case "books":
                categoryId = 267;
                break;
            case "clothing":
                categoryId = 11450;
                break;
            case "computers":
                categoryId = 58058;
                break;
            case "health":
                categoryId = 26395;
                break;
            case "music":
                categoryId = 11233;
                break;
            case "games":
                categoryId = 1249;
                break;
        }
        url += "&categoryId=" + categoryId;
    }

    var filterCounter = 0;
    if (req.query.nearbySearch == "true") {
        url += "&buyerPostalCode=" + req.query.zipCode;
        url += "&itemFilter(" + filterCounter + ").name=MaxDistance&itemFilter(" + filterCounter++ +").value=" + req.query.distance;
    }
    if (req.query.local == "true") {
        url += "&itemFilter(" + filterCounter + ").name=LocalPickupOnly&itemFilter(" + filterCounter++ + ").value=true";
    }
    if (req.query.free == "true") {
        url += "&itemFilter(" + filterCounter + ").name=FreeShippingOnly&itemFilter(" + filterCounter++ + ").value=true";
    }
    url += "&itemFilter(" + filterCounter + ").name=HideDuplicateItems&itemFilter(" + filterCounter++ + ").value=true";
    if (req.query.new == "true" || req.query.used == "true" || req.query.unspecified == "true") {
        var valueArrayCounter = 0;
        url += "&itemFilter(" + filterCounter + ").name=Condition";
        if (req.query.new == "true") {
            url += "&itemFilter(" + filterCounter + ").value(" + valueArrayCounter++ + ")=New";
        }
        if (req.query.used == "true") {
            url += "&itemFilter(" + filterCounter + ").value(" + valueArrayCounter++ + ")=Used";
        }
        if (req.query.unspecified == "true") {
            url += "&itemFilter(" + filterCounter + ").value(" + valueArrayCounter + ")=Unspecified";
        }
    }
    url += "&outputSelector(0)=SellerInfo&outputSelector(1)=StoreInfo";
    console.log(url);
    request(url, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            var itemsJSON = JSON.parse(body);
            var itemsList = [];
            if (itemsJSON["findItemsAdvancedResponse"][0]["searchResult"]) {
                itemsJSON = itemsJSON["findItemsAdvancedResponse"][0]["searchResult"][0]["item"];
                if (itemsJSON) {
                    itemsJSON.forEach(function(entry) {
                        var itemData = {
                            title : entry["title"][0],
                            titleShort : entry["title"][0],
                            price : entry["sellingStatus"][0]["currentPrice"][0]["__value__"],
                            seller : entry["sellerInfo"][0]["sellerUserName"][0].toUpperCase(),
                            itemId : entry["itemId"][0],
                            shippingLocations : entry["shippingInfo"][0]["shipToLocations"][0],
                            expeditedShipping : entry["shippingInfo"][0]["expeditedShipping"][0],
                            oneDayShipping : entry["shippingInfo"][0]["oneDayShippingAvailable"][0],
                            returnAccepted : entry["returnsAccepted"][0],
                            feedbackScore : entry["sellerInfo"][0]["feedbackScore"][0],
                            popularity : entry["sellerInfo"][0]["positiveFeedbackPercent"][0],
                            topRated : entry["sellerInfo"][0]["topRatedSeller"][0],
                        };
                        if (entry["postalCode"]) {
                            itemData["zipCode"] = entry["postalCode"][0];
                        } else {
                            itemData["zipCode"] = "N/A";
                        }
                        if (entry["galleryURL"]) {
                            itemData["photo"] = entry["galleryURL"][0];
                        }
                        if (itemData["titleShort"].length > 35) {
                            var tempString = itemData["titleShort"].substring(0, 35);
                            var length = 35;
                            while (tempString.charAt(length - 1) != " ") {
                                length--;
                            }
                            itemData["titleShort"] = tempString.substring(0, length) + " ...";
                        }
                        if (!itemData["zipCode"]) {
                            itemData["zipCode"] = "N/A";
                        }
                        itemData["popularity"] = parseFloat(itemData["popularity"]);
                        if (entry["storeInfo"]) {
                            itemData["storeName"] = entry["storeInfo"][0]["storeName"][0];
                            itemData["buyProductAt"] = entry["storeInfo"][0]["storeURL"][0];
                        }
                        if (entry["shippingInfo"][0]["handlingTime"]) {
                            itemData["handlingTime"] = entry["shippingInfo"][0]["handlingTime"][0] + " Day";
                        }
                        if (entry["shippingInfo"][0]["shippingServiceCost"]) {
                            itemData["shipping"] = entry["shippingInfo"][0]["shippingServiceCost"][0]["__value__"];
                            if (itemData["shipping"] == 0) {
                                itemData["shipping"] = "Free Shipping";
                            } else {
                                itemData["shipping"] = "$" + itemData["shipping"];
                            }
                        } else {
                            itemData["shipping"] = "N/A";
                        }
                        if (entry["condition"]) {
                            itemData["condition"] = entry["condition"][0]["conditionDisplayName"][0];
                        } else {
                            itemData["condition"] = "N/A";
                        }
                        itemsList.push(itemData);
                    });
                }
            }
            res.send(JSON.stringify(itemsList));
        }
    });
});
app.get("/getItemDetails", function(req, res) {
    var url = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=XuweiZho-productS-PRD-da6d4ee64-da7baa08"
        + "&siteid=0&version=967&ItemID=" + req.query.itemId + "&IncludeSelector=Description,Details,ItemSpecifics";
    console.log(url);
    request(url, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            var itemDetailsJSON = JSON.parse(body);
            itemDetailsJSON = itemDetailsJSON["Item"];
            var itemDetails = {
                title : itemDetailsJSON["Title"],
                photos : itemDetailsJSON["PictureURL"],
                subtitle : itemDetailsJSON["Subtitle"],
                price : itemDetailsJSON["CurrentPrice"]["Value"],
                location : itemDetailsJSON["Location"],
                itemURL : itemDetailsJSON["ViewItemURLForNaturalSearch"],
                globalShipping : itemDetailsJSON["GlobalShipping"]
            };
            if (itemDetailsJSON["ReturnPolicy"]["ReturnsAccepted"] == "ReturnsNotAccepted") {
                itemDetails["returnPolicy"] = "Returns Not Accepted";
            } else {
                itemDetails["returnPolicy"] = "Returns Accepted";
                itemDetails["returnsWithin"] = itemDetailsJSON["ReturnPolicy"]["ReturnsWithin"];
                itemDetails["refundMode"] = itemDetailsJSON["ReturnPolicy"]["Refund"];
                itemDetails["shippedBy"] = itemDetailsJSON["ReturnPolicy"]["ShippingCostPaidBy"];
            }
            if (itemDetailsJSON["ItemSpecifics"]) {
                itemDetails["itemSpecifics"] = itemDetailsJSON["ItemSpecifics"]["NameValueList"];
            }
            res.send(JSON.stringify(itemDetails));
        }
    });
});
app.get("/getGoogleSearchPhotos" , function(req, res) {
    var url = "https://www.googleapis.com/customsearch/v1?q=" + encodeURIComponent(req.query.title) + "&cx=008495671265096608735:9e3x35nh7ds"
        + "&imgSize=huge&imgType=news&num=8&searchType=image&key=AIzaSyBmGMmNa2as2c8zP29ZnBk9MFr4WbVDlwo";
    request(url, function(error, response, body) {
       if (!error && response.statusCode == 200) {
           var photosSearchJSON = JSON.parse(body);
           photosSearchJSON = photosSearchJSON["items"];
           var photosList = [];
           if (photosSearchJSON) {
               photosSearchJSON.forEach(function(entry) {
                   photosList.push(entry["link"]);
               });
           }
           res.send(JSON.stringify(photosList));
       }
    });
});
app.get("/getSimilarItems", function(req, res) {
    var url = "http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService"
        + "&SERVICE-VERSION=1.1.0&CONSUMER-ID=XuweiZho-productS-PRD-da6d4ee64-da7baa08&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD"
        + "&itemId=" + req.query.itemId + "&maxResults=20";
    request(url, function(error, response, body) {
       if (!error && response.statusCode == 200) {
           var similarItemsJSON = JSON.parse(body);
           similarItemsJSON = similarItemsJSON["getSimilarItemsResponse"]["itemRecommendations"]["item"];
           var similarItemsList = [];
           if (similarItemsJSON) {
               similarItemsJSON.forEach(function(entry) {
                   var similarItem = {
                       photo : entry["imageURL"],
                       title : entry["title"],
                       price : entry["buyItNowPrice"]["__value__"],
                       shipping : entry["shippingCost"]["__value__"],
                       daysLeft : entry["timeLeft"].match(/(?<=P)\d+(?=D)/)[0],
                       itemURL : entry["viewItemURL"]
                   };
                   similarItemsList.push(similarItem);
               });
           }
           res.send(JSON.stringify(similarItemsList));
       }
    });
});
//Start Server
app.listen(process.env.PORT || 3000);