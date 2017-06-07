
// Client code for Sheet Translation
// Jim Skon, 2015
// Expert Learning Systems

var newCode = '';
var req;
var isIE;

// Callback for for XML to XML template application
function callback() {
    if (req.readyState === 4) {
        if (req.status === 200) {
            // Javascript function JSON.parse to parse JSON data
            var message = JSON.parse(req.response);
            //alert(message['translateMessage']);
            if (message['success']) {
                XMLCode = message['output'];
                $('#TranslatedSheetCode').val(XMLCode);
                var code = convertToHTML(XMLCode);
                //alert(code);
                $('#HTMLcode').val(code);
                $('#HTMLOutput').html(code);
            } else {
                $('#SheetMessage').text(message['translateMessage']);
            }

        }
    }
}

// Handle Text Area Button highlights
$('.display').on('click', function () {
    $(this).toggleClass('btn-success');
});

// Clear text area messages on event
$('.form-control').on('change', function (event) {
    $(this).parent().find('.message').html("");

});

function TranslateSheet() {
    $('#TranslateMessage').html("");
    $('#SheetMessage').html("");
    var sheet = encodeURIComponent($('#SheetCode').val());
    var url = "XMLTranslatorServlet?command=sheet" + "&sheet=" + sheet;
    req = initRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback;
    req.send(null);
}

function initRequest() {
    if (window.XMLHttpRequest) {
        if (navigator.userAgent.indexOf('MSIE') != -1) {
            isIE = true;
        }
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}

function convertToHTML(xml) {
    var oSerializer = new XMLSerializer();
    
    xmlDoc = loadXMLString(xml);
    var html = "";
    var XMLCode = xmlDoc.getElementsByTagName("html");

    $.each (XMLCode, function (i, htmlNode) {
        //alert(oSerializer.serializeToString(htmlNode));
        var htmlChildren = htmlNode.childNodes;
        //alert(oSerializer.serializeToString(htmlChildren[0]));
        $.each(htmlChildren , function  (j, code) {
        //    alert(oSerializer.serializeToString(code));
        html += oSerializer.serializeToString(code);
    });
    });
    return html;
}

function loadXMLString(txt)
{
    if (window.DOMParser)
    {
        parser = new DOMParser();
        xmlDoc = parser.parseFromString(txt, "text/xml");
    }
    else // code for IE
    {
        xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = false;
        xmlDoc.loadXML(txt);
    }
    return xmlDoc;
}

