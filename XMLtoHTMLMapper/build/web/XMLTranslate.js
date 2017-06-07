


/* global vkbeautify */

// Client code for XML to HTML Translator tool
// Jim Skon, 2015
// Expert Learning Systems

var newCode = '';
var req;
var isIE;
var toggleAllAreas = false;

// Callback for for XML to XML template application
function callback1() {
    if (req.readyState === 4) {
        if (req.status === 200) {
            // Javascript function JSON.parse to parse JSON data
            var message = JSON.parse(req.response);
            //alert(message['HTML']);
            $('#XMLCode').val(message['output']);   
            $('#BaseXMLMessage').html(message['BaseXMLMessage']);
            $('#XMLTemplateMessage').append(message['templateMessage']);
            $('#XMLTranslateMessage').append(message['translateMessage']);
            if (message['output'].length > 0) {
                XMLTemplateSelect(message['output'],"HTML");
            }

        }
    }
}

 // Callback for for XML to HTML template application
function callback2() {

    if (req.readyState === 4) {
        if (req.status === 200) {
            // Javascript function JSON.parse to parse JSON data
            var message = JSON.parse(req.response);
            //alert(JSON.stringify(message));
            $('#HTMLOutput').val(message['output']);
            $('.dropdown-toggle').dropdown();
            $('#HTMLOutput').val(message['output']);    
            $('#XMLMessage').html(message['XMLMessage']);
            $('#TemplateMessage').append(message['templateMessage']);
            $('#TranslateMessage').html(message['translateMessage']);
            if (message['output'].length > 0) {
                doHTMLDisplay();
            }

        }
    }
}

// Callback for Template lookup
function callback3() {
    if (req.readyState === 4) {
        if (req.status === 200) {
            var message = JSON.parse(req.response);
            //alert(JSON.stringify(message));
            if (message['type'] === "XML") {
                $('#XMLTemplateMessage').html(message['message']);
                if (message['success']) {
                    $('#XMLTemplateCode').val(message['template']);
                    if (message['template'].length > 0) {
                        //alert(message['message']);
                        doXMLSubmit(message['message']);
                    }
                }

                
            } else if (message['type'] === "HTML") {
                if (message['success']) {
                    $('#TemplateCode').val(message['template']);
                    if (message['template'].length > 0) {
                        doHTMLSubmit();
                    }
                } else {
                    $('#TemplateMessage').html(message['message']);
                    $('#TemplateCode').val(message[""]);
                }
            }
        }
    }
}

function selfSubmitElement(element) {
        var sheet = element.attr('loadsheet');
        console.log("selfsubmit:" + sheet + ":" + element.attr('data-ischecked'));
        //submitItems(element);
        //getSheet(sheet);
}
// Handle Text Area Button highlights
$('.display').on('click', function () {
  $(this).toggleClass('btn-success');
});

// Make sure there is text in boxes before translation can be attempted
//$(".XMLText").on('oninput', function () {
$(document).click(function(event){
    checkTextAreas();
});
$(document).ready(function()  {
    checkTextAreas();
});
        
// Clear text area messages on event
$('.form-control').on('change', function (event) {
    $(this).parent().find('.message').html("");
    
});

$('textarea').on('mouseout mouseover', function () {
   checkTextAreas();
});


//Make buttons inactive if the associated text areas are empty
function checkTextAreas() {
   var baseXMLLength = $('#BaseXMLCode').val().length;
   var templateXMLLength = $('#XMLTemplateCode').val().length;
   if (baseXMLLength < 1){ 
       $('.lookupXML').prop('disabled', true);
   } else {
       $('.lookupXML').prop('disabled', false);
   }
   
   if (baseXMLLength < 1 || templateXMLLength < 1) { 
       $('.translateXML').prop('disabled', true);
   } else {
       $('.translateXML').prop('disabled', false);
   }
   
   var XMLLength = $('#XMLCode').val().length;
   var baseHTMLLength = $('#TemplateCode').val().length;
   
   if (XMLLength < 1) { 
       $('.lookupHTML').prop('disabled', true);
   } else {
       $('.lookupHTML').prop('disabled', false);
   }  
   
   if (baseHTMLLength < 1 || XMLLength < 1) { 
       $('.translateHTML').prop('disabled', true);
   } else {
       $('.translateHTML').prop('disabled', false);
   }   

    var HTMLLength = $('#HTMLOutput').val().length;
   if (HTMLLength < 1) { 
       $('.displayHTML').prop('disabled', true);
   } else {
       $('.displayHTML').prop('disabled', false);
   }   
};

function toggleAll() {

    if (toggleAllAreas) {
        $('.collapse').collapse('hide');
        $('.display').removeClass('btn-success');
        $('#all').text("All");
        toggleAllAreas = false;
        
    } else {
        $('.collapse').collapse('show');
        $('.display').addClass('btn-success');
        $('#all').text("None");
        toggleAllAreas = true;
    }
    return true;
}

function init() {
    //completeField = document.getElementById("complete-field");
}


function doHTMLSubmit() {
    
    $('#TranslateMessage').html("");
    $('#TemplateMessage').html("");
    var template = encodeURIComponent($('#TemplateCode').val());
    var xml = encodeURIComponent($('#XMLCode').val());
    var url = "XMLTranslatorServlet?command=xml2html" + "&xml=" + xml + "&template=" + template;
    console.log("Request size:"+url.length)
    req = initRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback2;
    req.send(null);
}

function doXMLSubmit(preMess) {
    
    $('#XMLTranslateMessage').html("");
    $('#XMLTemplateMessage').html(preMess);
    var template = encodeURIComponent($('#XMLTemplateCode').val());
    var xml = encodeURIComponent($('#BaseXMLCode').val());
    var url = "XMLTranslatorServlet?command=xml2xml" + "&xml=" + xml + "&template=" + template;
    req = initRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback1;
    req.send(null);
}

function doHTMLDisplay() {
    $('#htmldisplay').html($('#HTMLOutput').val());
}

function XMLTemplateSelect(sourceXML,type) {
    
    var url = "XMLTranslatorServlet?command=findtemplate" + "&xml=" + sourceXML + "&type=" + type;
    req = initRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback3;
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

