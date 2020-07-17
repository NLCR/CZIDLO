//https://developer.chrome.com/extensions/messaging
chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        //console.log(request)
        if (request.type == "getUrnData") {
            var czidloApiBaseUrl = request.czidloApiBaseUrl;
            $.get(czidloApiBaseUrl + "/urnnbn/" + request.urn + "?format=json", function (data) {
                //console.log(data);
                if (data.urnNbn) {
                    var u = data.urnNbn;
                    var messageResponse = {
                        status: u.status,
                        urn: u.value,
                        registrarCode: u.registrarCode,
                        digitalDocumentId: u.digitalDocumentId,
                        reserved: u.reserved,
                        registered: u.registered,
                        deactivated: u.deactivated,
                        deactivationNote: u.deactivationNote
                    }
                    //TODO: predecessors
                    //https://resolver-dev.nkp.cz/api/v5/urnnbn/urn:nbn:cz:aba001-0005hm?format=json

                    //TODO: natahat data o knihovnach, digitalnich instancich
                    if (u.status == 'ACTIVE') {
                        $.get(czidloApiBaseUrl + "/digitalDocuments/id/" + u.digitalDocumentId + "?format=json", function (data) {
                            //console.log(data)
                            messageResponse.title = extractTitle(data.digitalDocument);
                            messageResponse.digitalInstances = data.digitalDocument.digitalInstances;
                            sendResponse(messageResponse);
                        });
                    } else {
                        sendResponse(messageResponse);
                    }
                } else {
                    //TODO: handle error
                }
                //sendResponse(data);
            });
            //jinak "The message port closed before a response was received", see https://stackoverflow.com/questions/54126343/how-to-fix-unchecked-runtime-lasterror-the-message-port-closed-before-a-respon
            return true;
        }
    });

// https://resolver-dev.nkp.cz/api/v5/urnnbn/urn:nbn:cz:mzk-005wo2?format=json
// https://resolver-dev.nkp.cz/api/v5/resolver/urn:nbn:cz:nk-000m65/digitalInstances?format=json
// https://resolver-dev.nkp.cz/api/v5/registrars?digitalLibraries=true&catalogs=false&format=json
// https://resolver-dev.nkp.cz/api/v5/digitalDocuments/id/11401?format=json
// https://resolver-dev.nkp.cz/api/v5/resolver/urn:nbn:cz:ik-007920?format=json
// https://resolver-dev.nkp.cz/api/v5/urnnbn/urn:nbn:cz:mzk-005wo2?format=json

function extractTitle(data) {
    //periodical
    if (data.periodical) {
        var titleInfo = data.periodical.titleInfo;
        var result = titleInfo.title;
        if (titleInfo.subTitle) {
            result += ' (' + titleInfo.subTitle + ')';
        }
        return result;
    }
    if (data.periodicalVolume) {
        var titleInfo = data.periodicalVolume.titleInfo;
        var result = titleInfo.periodicalTitle;
        if (titleInfo.volumeTitle) {
            result += ' ' + titleInfo.volumeTitle;
        }
        return result;
    }
    if (data.periodicalIssue) {
        var titleInfo = data.periodicalIssue.titleInfo;
        var result = titleInfo.periodicalTitle;
        if (titleInfo.volumeTitle) {
            result += ' ' + titleInfo.volumeTitle;
        }
        if (titleInfo.issueTitle) {
            result += ' ' + titleInfo.issueTitle;
        }
        return result;
    }
    //monograph
    if (data.monograph) {
        var titleInfo = data.monograph.titleInfo;
        var result = titleInfo.title;
        if (titleInfo.subTitle) {
            result += ' (' + titleInfo.subTitle + ')';
        }
        return result;
    }
    if (data.monographVolume) {
        var titleInfo = data.monographVolume.titleInfo;
        var result = titleInfo.monographTitle;
        if (titleInfo.volumeTitle) {
            result += ' ' + titleInfo.volumeTitle;
        }
        return result;
    }
    //analytical, thesis, other
    //TODO: otestovat
    if (data.analytical) {
        var titleInfo = data.analytical.titleInfo;
    } else if (data.thesis) {
        var titleInfo = data.thesis.titleInfo;
    } else if (data.other) {
        var titleInfo = data.other.titleInfo;
    }
    var result = titleInfo.title;
    if (titleInfo.subTitle) {
        result += ' (' + titleInfo.subTitle + ')';
    }
    return result;
}

