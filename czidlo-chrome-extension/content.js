var czidloBaseUrl;
var czidloApiBaseUrl;

chrome.storage.sync.get({
    instance: 'resolver.nkp.cz'
}, function (items) {
    //console.log("config loaded"); 
    //console.log(items);
    czidloBaseUrl = "https://" + items.instance;
    czidloApiBaseUrl = czidloBaseUrl + "/api/v5";
});


$(document).ready(function () {
    //remove existing elements with classes .urn-nbn-cz and replace with content of element .urn-nbn-cz-value
    $(".urn-nbn-cz").each(function (num, el) {
        var urn = $(this).children(".urn-nbn-cz-value").text();
        //console.log(urn);
        el.replaceWith(urn);
    });

    //find "urn:nbn:cz:" in text nodes and wrap with .urn-nbn-cz, .urn-nbn-cz-value and .urn-nbn-cz-info
    //see https://makandracards.com/makandra/38803-find-the-innermost-dom-element-that-contains-a-given-string
    $(":containsNC('urn:nbn:cz:'):not(:has(:containsNC('urn:nbn:cz:')))").each(function (num, val) {
        //console.log(val);
        var replaced = val.innerHTML.replace(new RegExp("urn:nbn:cz:[a-z0-9]{2,6}-[a-z0-9]{6}", "gi"), function (x) {
            return "<inline class='urn-nbn-cz'><inline class='urn-nbn-cz-value'>" + x + "</inline><div class='urn-nbn-cz-info'></div></inline>";
        });
        val.innerHTML = replaced;
    })

    //append listen for entering mouse for every urn-nbn-cz-value 
    $(".urn-nbn-cz-value").each(function () {
        $(this).on("mouseenter", () => {
            var urn = $(this).text();
            //console.log(urn);
            var info = $(this).parent().find(".urn-nbn-cz-info");
            info.empty();
            info.append("<div class='urn-nbn-cz-progress-spinner-container'><div class='dot-windmill'></div></div>");

            chrome.runtime.sendMessage(
                {
                    type: "getUrnData",
                    urn: urn,
                    czidloApiBaseUrl: czidloApiBaseUrl
                },
                function (response) {
                    //console.log(response);
                    info.empty();

                    //stav
                    info.append("<div class='" + toStatusClass(response.status) + "'>" + formatStatus(response.status) + "</div>");
                    //název
                    if (response.title) {
                        info.append("<div class='urn-nbn-cz-title'>" + response.title + "</div>");
                    }
                    //odkaz do CZIDLO
                    if (response.status != 'FREE') {
                        var czidloSearchUrl = czidloBaseUrl + "/web?q=" + response.urn
                        info.append("<div class='urn-nbn-cz-czidlo-url'><a target='_blank' href='" + czidloSearchUrl + "'>záznam v CZIDLO</a></div>");
                    }
                    //timestamps
                    if (response.reserved) {
                        //console.log("reserved: " + response.reserved);
                        info.append("<div class='urn-nbn-cz-timestamp'>rezervováno: " + formatDate(response.reserved) + "</div>");
                    }
                    if (response.registered) {
                        info.append("<div class='urn-nbn-cz-timestamp'>registrováno: " + formatDate(response.registered) + "</div>");
                    }
                    if (response.deactivated) {
                        info.append("<div class='urn-nbn-cz-timestamp'>deaktivováno: " + formatDate(response.deactivated) + "</div>");
                    }
                    //digital instances
                    if (response.digitalInstances) {
                        info.append("<div class='urn-nbn-cz-dis'/>");
                        var dis = info.find(".urn-nbn-cz-dis");
                        //urn-nbn-cz-dis
                        response.digitalInstances.forEach(di => {
                            //console.log(di);
                            if (di.active) {
                                dis.append("<div class='urn-nbn-cz-di-url'><a target='_blank' href='" + di.url + "'>" + di.url + "</a></div>");
                            }
                        });
                    }
                }
            );

            // var timer = setTimeout(() => {
            //     console.log(" data loaded");
            //     info.empty();
            //     info.append("<div>TODO: data loaded</div>");
            //     clearTimeout(timer);
            // }, 3000);
        });
    });
});

function toStatusClass(value) {
    switch (value) {
        case "FREE":
            return "urn-nbn-cz-status-free";
        case "RESERVED":
            return "urn-nbn-cz-status-reserved";
        case "ACTIVE":
            return "urn-nbn-cz-status-active";
        case "DEACTIVATED":
            return "urn-nbn-cz-status-deactivated";
        default:
            return "urn-nbn-cz-status";
    }
}

function formatStatus(value) {
    switch (value) {
        case "FREE":
            return "volné";
        case "RESERVED":
            return "rezervováno";
        case "ACTIVE":
            return "aktivní";
        case "DEACTIVATED":
            return "deaktivováno";
        default:
            return value;
    }
}

function formatDate(value) {
    var date = new Date(value);
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    return day + ". " + month + ". " + year;
}

/* 
function containsNS works just like constains, only is not case sensitive
see https://jsfiddle.net/ympBL/10/
*/
; (function () {
    jQuery.expr[':'].containsNC = function (elem, index, match) {
        return (elem.textContent || elem.innerText || jQuery(elem).text() || '').toLowerCase().indexOf((match[3] || '').toLowerCase()) >= 0;
    }
}(jQuery));