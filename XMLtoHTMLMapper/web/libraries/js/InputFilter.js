/* Initalizes the beginning qualities of an input element.
 * Pass the element's id, the regex pattern for its filter, its testing mask, its auto mask, its case type, its capture, if it should auto submit, and an error message */
function initalizeInput(id, placeholder, regex, flags, mask, automask, caseType, capture, autoSubmit, refreshOnSubmit, errorMessage) {
   var inputElement = $("#" + id);
   inputElement.attr("size", mask.length);  // Set the elements size based on its mask length
   inputElement.next().hide(); // Hide the input elements error message alert
   inputElement.attr("placeholder", placeholder);
   inputElement.attr("data-refresh", refreshOnSubmit);
   regex = new RegExp(pattern, flags);    // Change regex pattern from a string to an object and add in the flags
   checkForInputAuthorErrors(id, regex, mask, automask, capture, errorMessage);
   var lastKeyWasBackspace = false;    // Keep track of if the last key pressed was a backspace
   testInput(id, lastKeyWasBackspace, regex, mask, automask, caseType, capture, autoSubmit, errorMessage);
   inputElement.keydown(function(key) {
       if (key.keyCode === 8) {    // 8 Is the keycode for backspace
           lastKeyWasBackspace = true;   // Keep track of the last key to be pressed
       }
       else {
           lastKeyWasBackspace = false;
       }
   });
   inputElement.on("input", function() {
       testInput(id, lastKeyWasBackspace, regex, mask, automask, caseType, capture, autoSubmit, errorMessage);   // User inputed into the element. Test his or her input
   });
}

/* Tests a user input and performs the appropriate functions depending on the input
* Pass the input element's id, if the last key pressed was a backspace, the regex for its filter, its testing mask, its auto mask, its case type, its capture, if it should auto submit, and an error message.
* This function is a bad function meaning it has bugs and needs to be re-written. However, it currently works by slowing iterating through each character of the user's input and checking
* to see if those characters are legal, or if a character should automatically be inserted in that spot, or if an automatic character should be removed from that spot. It keeps a string of the
* current legal input and slowly builds this string character by character. This function has has 5 Parts: 1. Initalize values. 2. Add auto characters that belong at the start of the input.
* 3. Remove any auto characters from the user's input that no longer below in the input 4. Test the new input. and 5. Update values and check if input is done */
function testInput(id, lastKeyWasBackspace, regex, mask, automask, caseType, capture, autoSubmit, errorMessage) {
   // Part 1: Initalize values
   var correctCaretPosition = getInputCaretPosition(document.getElementById(id));
   var inputElement = $("#" + id);
   var input = inputElement.val(); // The user's input
   var inputTypecased = caseSensitizeInput(input, caseType);
   // A string that slowly builds into the new legal input, by adding in legal parts of the input along with auto characters,
   // and by removing illegal parts of the input and auto characters that are no longer needed
   var currentLegalInput = "";
   // Part 2: Add auto characters at the start
   if (automask !== "") {  // If there is an automask, put add in any starting auto characters
       var oldCurrentLegalInput = currentLegalInput;
       currentLegalInput = addInputAutoAfterIndex(automask, "", -1);
       if (correctCaretPosition == inputStringLengthDifference(currentLegalInput, oldCurrentLegalInput) - 1) {
           correctCaretPosition += inputStringLengthDifference(currentLegalInput, oldCurrentLegalInput);
       }
   }
   // Part 3: Remove auto characters that no longer belong
   if (lastKeyWasBackspace && automask !== "") {    // User deleted something and there is an automask
       // Take the user's input, and remove any auto characters that no longer belong right before what they deleted
       currentLegalInput = removeInputAutoBeforeIndex(automask, inputTypecased, correctCaretPosition, true, false);
       var differenceSoFar = inputStringLengthDifference(currentLegalInput, inputTypecased);
       currentLegalInput = addInputAutoAfterIndex(automask, currentLegalInput, correctCaretPosition - 1 + differenceSoFar);
       if (correctCaretPosition == currentLegalInput.length - differenceSoFar) {
           correctCaretPosition += differenceSoFar;
       }
   }
   // Part 4: Test input
   else {
       for (var inputIndex = 0; inputIndex <= inputTypecased.length; inputIndex++) {
           var nextChar = inputTypecased.charAt(inputIndex);
           var testMaskForChar = mask;
           if (nextChar) {
               testMaskForChar = inputReplaceAtIndex(mask, nextChar, currentLegalInput.length);
           }
           regex.lastIndex = 0;
           var matchSoFar = true;
           if (mask !== "") {
               matchSoFar = regex.test(testMaskForChar);
           }
           if (matchSoFar) {
               currentLegalInput = currentLegalInput.substring(0, currentLegalInput.length);
               currentLegalInput += nextChar;
               if (automask !== "") {
                   var oldLegalInputSoFar = currentLegalInput;
                   currentLegalInput = addInputAutoAfterIndex(automask, currentLegalInput, currentLegalInput.length - 1);
                   if (correctCaretPosition == currentLegalInput.length - inputStringLengthDifference(currentLegalInput, oldLegalInputSoFar)){
                   correctCaretPosition += inputStringLengthDifference(currentLegalInput, oldLegalInputSoFar); }

               }
           }
       }
   }
   // Part 5: Update values and check if input is done
   regex.lastIndex = 0;    // Reset regex natural index coutner
   var validInput = regex.test(currentLegalInput);
   if (validInput) {
       var inputThroughCapture = currentLegalInput.replace(regex, capture);    // Filter the valid input through the capture
       inputElement.attr("value", inputThroughCapture); // Update the input value attribute with the final input which was filtered through the capture. Used for storing the user input
       if (autoSubmit) {
           $(":input:eq(" + ($(':input').index("#" + id) + 1) + "), :button:eq(" + ($(':button').index("#" + id) + 1) + ")").first().focus();  // Automatically focus onto the next input or button
           // submit(id)
       }
   }
   else {  // The input is not a valid submission
       inputElement.removeAttr("value");   // Remove the value that is used for storing the user input
       if (input !== "") {    // The user has inputted something
           displayInputErrorMessageInTime(inputElement, errorMessage);  // Get ready to display an error message
       }
   }
   inputElement.val(currentLegalInput);    // Update the input field with the updated input
   setInputCaretPosition(document.getElementById(id), correctCaretPosition);   // Set the caret to the correct position
}

/* Checks common errors that are caused by the html template author in relation to the input element parameters.
* Pass the element's id, the regex for its filter, its testing mask, its auto mask, its capture, and an error message */
function checkForInputAuthorErrors(id, regex, mask, automask, capture, errorMessage) {
   if (mask.length !== automask.length) {  // Mask and automask must be the same length
       alert("Error on input element with id '" + id + "': The mask '" + mask + "' and the automask '" + automask + "' must be the same length");
   }
   if (capture === "") {   // There must be a capture to return an answer value to the content server
       alert("Error on input element with id '" + id + "': The capture is empty");
   }
   if (regex === null && mask !== "")  { // If regex is empty, mask should be too
       alert("Warning on input element with id '" + id + "': The regex '" + regex + "' is empty but the mask '" + mask + "' is not");
   }
   if (mask === "" && regex !== null) {   // If mask is empty, regex should be too
       alert("Warning on input element with id '" + id + "': The mask '" + mask + "' is empty but the regex '" + regex + "' is not");
   }
   if (errorMessage === "") {
       alert("Warning on input element with id '" + id + "': The error message '" + errorMessage + "' is empty");
   }
}

/* Turns a string into all lower or all uppercase characters. Returns the sensitized string.
* Pass string to sensitize, and the case type ("upper" or "lower"). Any other case type does not sensitize the string. */
function caseSensitizeInput(string, caseType) {       
   if (caseType === "lower") {
       return string.toLowerCase();
   }
   else if (caseType === "upper") {
       return string.toUpperCase();
   }
   else {
       return string;
   }
}

/* Adds any needed autos that belong after the passed index of a string, according to an automask
* Pass the automask to use, the string to add auto characters to, and the index in the string to start adding autos at */
function addInputAutoAfterIndex(automask, originalString, index) {
   var automaskCharAfterIndex = automask.charAt(index + 1);
   if (automaskCharAfterIndex && automaskCharAfterIndex !== "*") { // There is an automask character after the index and that character is an auto character
       var autoCharToAdd = automaskCharAfterIndex; // Therefore, the next automask chacter is one that should be added to the string
       // Add the next auto character into the original string at the correct index without removing any of the original string
       var stringWithImmediateAutos = originalString.slice(0, index + 1) + autoCharToAdd + originalString.slice(index + 1);
       // Recursivly call itself using the next index and the new sting with its new auto character, in order to check for multiple auto characters in a row, and add them if there are any
       stringWithImmediateAutos = addInputAutoAfterIndex(automask, stringWithImmediateAutos, index + 1);
       return stringWithImmediateAutos;
   }
   else {  // There is no auto character immediately after the index. This is the last of the recursive calls
       return originalString;
   }
}

/* Remove autos added by the "addAutoAfterIndex" function
* Pass the automask that was used, the string to remove auto characters from,
* the index in the string to start removing autos at (all autos immediately before this index are removed),
* and the last two parameters deal with recursion and backspaces as I will try to explain:
* When a user backspaces, they have either backspaced an auto character or a user inputted character.
* If an auto character was backspaced, then this function should delete all autos immediately before the
* backspaced character and then delete one more character. This is because the user should not have to care
* about the auto inputs. The user's inputs and backspaces should not deal with auto characters. Now if a
* user inputted character was backspaced, then this function should delete all auto characters immediately
* before the backspaced character and thats it. Remember that the string this function is passed does not
* contain the backspaced character. It was already removed. Even though I do not have that backspaced
* character, I can look at the correct index of the automask and find if it was an auto character or not.
* This looking should only be done on the first of the recursive calls of this function. This is because
* the function moves onto the previous index with each call, and I only need to look at the character
* at the index given at the first call, as that will be the index of the backspaced character.
* Therefore, the 4th parameter "checkForAutoCharDeleted" is a boolean that should be true on the first call
* which is when the function should check to see if the deleted character was an auto character.
* If that character was an auto character, then the function needs to delete one extra character at the
* last call of the recursive calls. Therefore, the 5th parameter "deleteCharAtEnd" is a boolean that should
* be true if the function needs to delete a charater at the end of its recursive calls due to the user
* backspacing an auto character. */
function removeInputAutoBeforeIndex(automask, originalString, index, checkForAutoCharDeleted, deleteCharAtEnd) {                                                                               
   var stringWithoutImmediateAutos = "";
   if (checkForAutoCharDeleted && automask.charAt(index) !== "*") {    // If it should check for if an auto character was deleted and if the character deleted was an auto character
       deleteCharAtEnd = true; // Delete an extra character on the last recursive call
   }
   var automaskCharBeforeIndex = automask.charAt(index - 1);   
   if (automaskCharBeforeIndex && automaskCharBeforeIndex !== "*") {   // There is an automask character before the index and that character is an auto character
       stringWithoutImmediateAutos = originalString.slice(0, index - 1) + originalString.slice(index);   // Remove the character before the index from the original string
       // Recursivly call itself using the previous index and the new sting without its auto character, in order to check for multiple auto characters in a row, and remove them if there are any
       stringWithoutImmediateAutos = removeInputAutoBeforeIndex(automask, stringWithoutImmediateAutos, index - 1, false, deleteCharAtEnd);
       return stringWithoutImmediateAutos;
   }
   else {  // There is no auto character immediately after the index. This is the last of the recursive calls
       if (deleteCharAtEnd) {  // A character needed to be deleted on the last call
           var stringWithOneCharDeleted = originalString.slice(0, index - 1) + originalString.slice(index); // Remove one more character
           return stringWithOneCharDeleted;
       }
       else {  // A character did not need to be deleted on the last call
           return originalString;
       }
   }
}
/* Replace a character at a specific index in a string with a new character.
* Pass the string to do the replacing in, the new character to use as the replacement, and the index to replace that new character into */
function inputReplaceAtIndex(origString, newChar, index) {
   var beforeIndex = origString.substr(0, index);
   var afterIndex = origString.substr(index + 1);
   return beforeIndex + newChar + afterIndex;
}

/* Gets the position of the caret in a text field.
* Pass the field (not in as a jquery object, but as a normal object) */
function getInputCaretPosition(field) {
   var caretPosition = 0;
   if (document.selection) {   // IE support
       var selection = document.selection.createRange ();  // To get cursor position, get empty selection range
       selection.moveStart("character", field.value.length);  // Move selection start to position 0
       caretPosition = selection.text.length;  // The caret position is selection length
   }
   else if (field.selectionStart || field.selectionStart == '0')   // Other support
       caretPosition = field.selectionStart;
   return caretPosition;
}

/* Sets the position of the caret in a text field.
* Pass the field (not in as a jquery object, but as a normal object) and the position to set the caret at in that field */
function setInputCaretPosition(field, caretPosition) {
   if (field.createRange) {    // IE support
       var range = field.createRange();
       range.collapse(true);
       range.moveEnd("character", caretPosition);
       range.moveStart("character", caretPosition);
       range.select();
   }
   else if (field.setSelectionRange) { // Other support
       field.focus();
       field.setSelectionRange(caretPosition,caretPosition);
   }
}

/* Get the positive or negative difference of two strings
* Pass two strings. Returns how far the length of the second string is from the first in a positive or negative format */
function inputStringLengthDifference(string1, string2) {
   var difference = string1.length - string2.length;
   return difference;
}

/* Displays an error message if the user has not typed for one second and their input is not complete.
* Pass the input element to display the message for and the message to display */
function displayInputErrorMessageInTime(inputElement, message) {
   var timeoutObject = setTimeout(function() { // Set a timeout that will display the error message when the timeout ends
       inputElement.css("border-color", "rgb(255, 0 , 0)");    // Bootstrap css that was edited in order to make the input red instead of blue
       inputElement.css("box-shadow", "rgba(255, 0, 0, 0.0745098) 0px 1px 1px 0px inset, rgba(255, 0, 0, 0.6) 0px 0px 8px 0px");
       inputElement.css("outline", "rgb(85, 85, 85) none 0px");
       var alertElement = inputElement.next(); // Assumes that the alert for the input is always the next sibling of the input element
       alertElement.text(message);
       alertElement.stop().hide().slideDown("fast");
       inputElement.on("input", function() {   // User typed something
           inputElement.css("border-color", "rgb(102, 175, 233)");  // So put back the original bootstrap css
           inputElement.css("box-shadow", "rgba(0, 0, 0, 0.0745098) 0px 1px 1px 0px inset, rgba(102, 175, 233, 0.6) 0px 0px 8px 0px");
           inputElement.css("outline", "rgb(85, 85, 85) none 0px");
           alertElement.stop().hide(); // And hide the alert message
       });
   }, 1000);   // A one second timeout before the error message is displayed
   inputElement.on("input", function() {   // If the user types something before the timeout ends
       clearTimeout(timeoutObject);    // Stop the timeout
   });
}