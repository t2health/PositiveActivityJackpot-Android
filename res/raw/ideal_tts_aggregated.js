/**
 * Code taken from: http://www.apps4android.org/web-access-plug-in-news-release.htm
 */

/* from ideal-global.js */
/*
 * Copyright (C) 2010 The IDEAL Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// 0 for browse
// 1 for forms
// 2 for combobox (Also, wtf?!??! Why doesn't Android browser's trackball focus respect the actual page focus????)
var IDEAL_MODE = 0;

// Used to get out of forms mode
var IDEAL_SHIFTCOUNT = 0;

// Used to track where we are on the page
var IDEAL_CurrentElement = null;
var IDEAL_PreviousElement = null;


/* from ideal-tts.js */
function IDEAL_TTS_Speak(messageString, queueMode, params){
  var speechNode = document.getElementById("IDEAL_speechnode");
  if (speechNode){
    speechNode = speechNode.parentNode.removeChild(speechNode);
  }
  speechNode = document.createElement("script");
  var queueModeStr = "/0/";
  if (queueMode == 1){
    queueModeStr = "/1/";
  }
  speechNode.src = "content://com.ideal.webaccess.tts" + queueModeStr + new Date().getTime() + "/" + messageString;
  document.body.appendChild(speechNode);
}

function IDEAL_TTS_Stop(){
  //IDEAL_TTS_Speak(" ", 0, null);
}


/* from ideal-keyhandler.js */
function IDEAL_KEYHANDLER_Watcher(event){
  IDEAL_TTS_Stop();

  // All hotkeys will be capitalized since we are watching keydown events.
  var hotkey = String.fromCharCode(event.keyCode);
  console.log("Key:"+hotkey);
  // Combo box mode
  // Wish we didn't have to do this, but Android browser's
  // trackball focus is immovable and refuses to respect the
  // page's actual focus.
  if (IDEAL_MODE == 2){
    if (hotkey == 'Q'){
      window.setTimeout("IDEAL_INTERFACE_PreviousSelectOption();", 0);
      return false;
    }

    if (hotkey == 'P'){
      window.setTimeout("IDEAL_INTERFACE_NextSelectOption();", 0);
      return false;
    }

    if (hotkey == ' '){
      IDEAL_INTERFACE_SwitchToBrowseMode();
	  return false;
    }
  }

  if (IDEAL_MODE != 0){
    if (event.keyCode == 16){
      IDEAL_SHIFTCOUNT = IDEAL_SHIFTCOUNT + 1;
    } else {
      IDEAL_SHIFTCOUNT = 0;
    }
    if (IDEAL_SHIFTCOUNT > 1){
      IDEAL_INTERFACE_SwitchToBrowseMode();
	  return false;
    }
	return true;
  }

  event.cancelBubble = true;
  if (event.stopPropagation) event.stopPropagation();

  if (hotkey == 'Q'){
    window.setTimeout("IDEAL_INTERFACE_ReadPrevious();", 0);
    return false;
  }

  if (hotkey == 'P'){
    window.setTimeout("IDEAL_INTERFACE_ReadNext();", 0);
    return false;
  }

  if (hotkey == ' '){
    window.setTimeout("IDEAL_INTERFACE_ActOnCurrentElem();", 0);
    return false;
  }
  return true;
}

function IDEAL_KEYHANDLER_FocusOnCommandArea(){
  /*var commandArea = document.getElementById("IDEAL_CommandArea");
  if (!commandArea){
    commandArea = document.createElement("input");
    commandArea.id = "IDEAL_CommandArea";
    commandArea.size = 1;
    commandArea.style.position = "absolute";
    commandArea.style.left = "-500px";
    commandArea.addEventListener("blur",
	  function(){
	    if (IDEAL_MODE == 0){
		  window.setTimeout("IDEAL_KEYHANDLER_FocusOnCommandArea()", 100);
		}
	  },
	  true);
	document.body.appendChild(commandArea);
  }
  commandArea.focus();*/
}

function IDEAL_KEYHANDLER_Init(){
  document.addEventListener("keydown", IDEAL_KEYHANDLER_Watcher, true);
  IDEAL_KEYHANDLER_FocusOnCommandArea();
}



/* from ideal-interface.js */
function IDEAL_INTERFACE_ReadNext(){
  IDEAL_PreviousElement = IDEAL_CurrentElement;
  IDEAL_CurrentElement = IDEAL_DOM_GetNextLeafNode(IDEAL_CurrentElement);
  while (IDEAL_CurrentElement && !IDEAL_DOM_HasContent(IDEAL_CurrentElement)){
    IDEAL_CurrentElement = IDEAL_DOM_GetNextLeafNode(IDEAL_CurrentElement);
  }
  if (IDEAL_CurrentElement){
    IDEAL_TTS_Speak(IDEAL_DOM_GetMetaInfo(IDEAL_CurrentElement) + " " + IDEAL_DOM_GetContent(IDEAL_CurrentElement), 0, null);
    IDEAL_DOM_ScrollToElem(IDEAL_CurrentElement);
  } else {
	window.T2AWV_INTERFACE.endOfDocuemntReached();
    //IDEAL_TTS_Speak("End of document", 0, null);
  }
}

function IDEAL_INTERFACE_ReadPrevious(){
  IDEAL_PreviousElement = IDEAL_CurrentElement;
  IDEAL_CurrentElement = IDEAL_DOM_GetPreviousLeafNode(IDEAL_CurrentElement);
  while (IDEAL_CurrentElement && !IDEAL_DOM_HasContent(IDEAL_CurrentElement)){
    IDEAL_CurrentElement = IDEAL_DOM_GetPreviousLeafNode(IDEAL_CurrentElement);
  }
  if (IDEAL_CurrentElement){
    IDEAL_TTS_Speak(IDEAL_DOM_GetMetaInfo(IDEAL_CurrentElement) + " " + IDEAL_DOM_GetContent(IDEAL_CurrentElement), 0, null);
    IDEAL_DOM_ScrollToElem(IDEAL_CurrentElement);
  } else {
	  window.T2AWV_INTERFACE.startOfDocumentReached();
    //IDEAL_TTS_Speak("Start of document", 0, null);
  }
}

// The majority of this function was taken from the AxsJAX project by Google.
// http://google-axsjax.googlecode.com
function IDEAL_INTERFACE_ActOnCurrentElem(){
  var targetNode = IDEAL_CurrentElement;
  var shiftKey = false;
  //Send a mousedown
  var evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('mousedown', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Send a mouse up
  evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('mouseup', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Send a click
  evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('click', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Clicking on a link does not cause traversal because of script
  //privilege limitations. The traversal has to be done by setting
  //document.location.
  var href = targetNode.getAttribute('href');
  if ((targetNode.tagName == 'A') &&
       href &&
      (href != '#')){
    if (shiftKey){
      window.open(targetNode.href);
    } else {
      document.location = targetNode.href;
    }
  }

  if (targetNode.tagName == 'INPUT'){
	var inputType = "";
	if (targetNode.type){
	  inputType = targetNode.type.toLowerCase();
	}
    if ((inputType == 'radio') || (inputType == 'checkbox')){
      window.setTimeout(function(){
	    var checkedStatus = "Not checked."
	    if (targetNode.checked){
		  checkedStatus = "Checked."
		}
        IDEAL_TTS_Speak(checkedStatus, 0, null);
	  }, 0);
	  return;
	}
	// Only switch modes if typing is involved
    IDEAL_MODE = 1;
	IDEAL_TTS_Speak("Forms mode.", 0, null);
    window.setTimeout(function(){targetNode.focus();}, 0);
  }

  if (targetNode.tagName == 'SELECT'){
    IDEAL_MODE = 2;
	IDEAL_TTS_Speak("Combo box mode.", 0, null);
  }
}

function IDEAL_INTERFACE_SwitchToBrowseMode(){
  IDEAL_MODE = 0;
  IDEAL_KEYHANDLER_FocusOnCommandArea();
  IDEAL_TTS_Speak("Browse mode", 0, null);
  IDEAL_SHIFTCOUNT = 0;
}

function IDEAL_INTERFACE_NextSelectOption(){
  if (IDEAL_CurrentElement &&
      IDEAL_CurrentElement.tagName &&
	  (IDEAL_CurrentElement.tagName == "SELECT")){
	  var selectedIndex = IDEAL_CurrentElement.selectedIndex + 1;
	  if (selectedIndex >= IDEAL_CurrentElement.length){
	    selectedIndex = 0;
	  }
	  IDEAL_CurrentElement.selectedIndex = selectedIndex;
  	  IDEAL_TTS_Speak(IDEAL_CurrentElement.value, 0, null);
  } else {
    IDEAL_INTERFACE_SwitchToBrowseMode();
  }
}

function IDEAL_INTERFACE_PreviousSelectOption(){
  if (IDEAL_CurrentElement &&
      IDEAL_CurrentElement.tagName &&
	  (IDEAL_CurrentElement.tagName == "SELECT")){
	var selectedIndex = IDEAL_CurrentElement.selectedIndex - 1;
	if (selectedIndex < 0){
	  selectedIndex = IDEAL_CurrentElement.length - 1;
	}
	IDEAL_CurrentElement.selectedIndex = selectedIndex;
  	IDEAL_TTS_Speak(IDEAL_CurrentElement.value, 0, null);
  } else {
    IDEAL_INTERFACE_SwitchToBrowseMode();
  }
}


/* from ideal-dom.js */
function IDEAL_DOM_GetNextLeafNode(targetNode){
  var currentElem = targetNode;
  var prevElem = currentElem;
  if (currentElem == null){
    currentElem = document.body;
  } else {
    while (!currentElem.nextSibling){
      currentElem = currentElem.parentNode;
      if (currentElem == document.body){
        currentElem = null;
        return currentElem;
      }
    }
    currentElem = currentElem.nextSibling;
  }
  while (!IDEAL_DOM_IsLeaf(currentElem)){
    currentElem = currentElem.firstChild;
  }
  return currentElem;
}

function IDEAL_DOM_GetPreviousLeafNode(targetNode){
  var currentElem = targetNode;
  var prevElem = currentElem;
  if (currentElem == null){
    currentElem = document.body;
  } else {
    while (!currentElem.previousSibling){
      currentElem = currentElem.parentNode;
      if (currentElem == document.body){
        currentElem = null;
        return currentElem;
      }
    }
    currentElem = currentElem.previousSibling;
  }
  while (!IDEAL_DOM_IsLeaf(currentElem)){
    currentElem = currentElem.lastChild;
  }
  return currentElem;
}


function IDEAL_DOM_IsLeaf(targetNode){
  if (!targetNode.firstChild){
    return true;
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "a")){
    return true;
  }
  if (CLC_TagInLineage(lineage, "input")){
    return true;
  }
  if (CLC_TagInLineage(lineage, "select")){
    return true;
  }
  return false;
}

function IDEAL_DOM_HasContent(targetNode){
  if (targetNode.nodeType == 8){
    return false;
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "head") || CLC_TagInLineage(lineage, "script")){
    return false;
  }
  if (IDEAL_DOM_IsHidden(targetNode)){
    return false;
  }
  if (IDEAL_DOM_GetContent(targetNode).length < 1){
    return false;
  }

  return true;
}

function IDEAL_DOM_IsHidden(targetNode){
  while (targetNode && (targetNode.nodeType != 1)){
    targetNode = targetNode.parentNode;
  }
  // Our own scratch areas are always considered hidden
  if (targetNode.id && (targetNode.id == "IDEAL_CommandArea")){
    return true;
  }
  if (targetNode){
    var computedStyle = getComputedStyle(targetNode, "");
    var display = computedStyle.getPropertyValue("display");
    if (display == "none"){
      return true;
    }
    var visibility = computedStyle.getPropertyValue("visibility");
    if (visibility == "hidden"){
      return true;
    }
  }
  return false;
}

function IDEAL_DOM_GetContent(targetNode){
  if (!targetNode){
    return "";
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "input")){
    var inputNode = targetNode;
	while (!inputNode.tagName || (inputNode.tagName != "INPUT")){
	  inputNode = inputNode.parentNode;
	}
	var inputType = "";
	if (inputNode.type){
	  inputType = inputNode.type.toLowerCase();
	}
	if (inputType == "radio"){
	  if (inputNode.checked){
	    return "Checked.";
	  } else {
	    return "Not checked.";
	  }
	} else if (inputType == "checkbox"){
	  if (inputNode.checked){
	    return "Checked.";
	  } else {
	    return "Not checked.";
	  }
	} else if (inputType == "password"){
	  var charCount = 0;
	  if (targetNode.value && (targetNode.value.length > 0)){
	    charCount = targetNode.value.length;
	  }
	  return charCount + " characters typed.";
	} else {
      if (targetNode.value && (targetNode.value.length > 0)){
        return targetNode.value;
      } else {
	    return "Blank.";
	  }
	}
  }
  if (CLC_TagInLineage(lineage, "select")){
    var selectNode = targetNode;
	while (!selectNode.tagName || (selectNode.tagName != "SELECT")){
	  selectNode = selectNode.parentNode;
	}
	return selectNode.value;
  }
  if (CLC_TagInLineage(lineage, "img")){
    if (targetNode.alt){
      return targetNode.alt;
    }
    if (targetNode.title){
      return targetNode.title;
    }
	return ""
  }
  if (targetNode.textContent && (targetNode.textContent.length > 0)){
	var actualContent = targetNode.textContent;
	while ((actualContent.charAt(0) == '\n') ||
           (actualContent.charAt(0) == '\r') ||
           (actualContent.charAt(0) == '\t') ||
           (actualContent.charAt(0) == ' ')    ){
      actualContent = actualContent.substring(1, actualContent.length);
    }
	return actualContent;
  }
  return "";
}

function IDEAL_DOM_GetMetaInfo(targetNode){
  var metaInfo = ""
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "a")){
    metaInfo = "Link.";
  } else if (CLC_TagInLineage(lineage, "select")){
    metaInfo = "Combo box.";
  } else if (CLC_TagInLineage(lineage, "h1")){
    metaInfo = "Heading One.";
  } else if (CLC_TagInLineage(lineage, "h2")){
    metaInfo = "Heading Two.";
  } else if (CLC_TagInLineage(lineage, "h3")){
    metaInfo = "Heading Three.";
  } else if (CLC_TagInLineage(lineage, "h4")){
    metaInfo = "Heading Four.";
  } else if (CLC_TagInLineage(lineage, "h5")){
    metaInfo = "Heading Five.";
  } else if (CLC_TagInLineage(lineage, "h6")){
    metaInfo = "Heading Six.";
  } else if (CLC_TagInLineage(lineage, "input")){
    var inputNode = targetNode;
	while (!inputNode.tagName || (inputNode.tagName != "INPUT")){
	  inputNode = inputNode.parentNode;
	}
	if (inputNode.type && (inputNode.type.toLowerCase() == "radio")){
	  metaInfo = "Radio button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "checkbox")){
	  metaInfo = "Checkbox.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "button")){
	  metaInfo = "Button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "submit")){
	  metaInfo = "Submit button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "password")){
	  metaInfo = "Password.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "image")){
	  metaInfo = "Image button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "reset")){
	  metaInfo = "Reset button.";
	} else {
      metaInfo = "Input.";
	}
  }
  return metaInfo;
}

function IDEAL_DOM_ScrollToElem(targetNode){
  while (!targetNode.offsetParent){
    targetNode = targetNode.parentNode;
  }
  var left = 0;
  var top = 0;
  while (targetNode.offsetParent) {
    left += targetNode.offsetLeft;
    top += targetNode.offsetTop;
    targetNode = targetNode.offsetParent;
  }
  left += targetNode.offsetLeft;
  top += targetNode.offsetTop;
  window.scrollTo(left, top);
}


/* from clc-domUtils.js */
//------------------------------------------
//Returns an array of DOM objects that is the lineage
//of the target object.
//The array is ordered such that the target is the
//last element in the array.
//
function CLC_GetLineage(target){
   var lineage = new Array();
   var object = target;
   while (object){
      lineage.push(object);
      if (object.parentPointer){
         object = object.parentPointer;
         }
      else{
         object = object.parentNode;
         }
      }
   lineage.reverse();
   while(lineage.length && !lineage[0].tagName && !lineage[0].nodeValue){
      lineage.shift();
      }
   return lineage;
   }

//------------------------------------------
//Compares Lineage A with Lineage B and returns
//the index value in B at which B diverges from A.
//If there is no divergence, the result will be -1.
//Note that if B is the same as A except B has more nodes
//even after A has ended, that is considered a divergence.
//The first node that B has which A does not have will
//be treated as the divergence point.
//
function CLC_CompareLineages(lina, linb){
   var i = 0;
   while( lina[i] && linb[i] && (lina[i] == linb[i]) ){
       i++;
       }
   if ( !lina[i] && !linb[i] ){
      i = -1;
      }
   return i;
   }
//------------------------------------------
//Determines if DOM_obja contains DOM_objb
//
function CLC_AcontainsB(DOM_obja, DOM_objb){
   if(!DOM_obja){
      return false;
      }
   var lineage = CLC_GetLineage(DOM_objb);
   for (var i=0; i < lineage.length; i++){
      if (lineage[i] == DOM_obja){
         return true;
         }
      }
   return false;
   }


//------------------------------------------
//Determines if a lineage has any DOM object with
//the specified HTML tag string
//
function CLC_TagInLineage(lineage, tag){
   tag = tag.toLowerCase();
   for (var i=0; i < lineage.length; i++){
      if ( lineage[i].localName && (lineage[i].localName.toLowerCase() == tag) ){
         return true;
         }
      }
   return false;
   }
//------------------------------------------
//Builds a logical lineage as opposed to a strict physical lineage.
//These are identical for the most part, except a logical lineage
//will include the label elements as parents of whatever
//they are a label for.
//If there are multiple labels, the earlier they appear in the HTML,
//the "older" they are. "Older"/"younger" means that if both exist, the
//older one will be the parent of the younger one.
//
function CLC_GetLogicalLineage(target){
   if (!target.tagName){
      return CLC_GetLineage(target);
      }
   if (   (target.tagName.toLowerCase() != "input") &&
          (target.tagName.toLowerCase() != "button") &&
          (target.tagName.toLowerCase() != "select") &&
          (target.tagName.toLowerCase() != "textarea")    ) {
      return CLC_GetLineage(target);
      }
   if (!target.hasAttribute("id")){
      return CLC_GetLineage(target);
      }

    var tempLineage = CLC_GetLineage(target);
    var labelArray = tempLineage[0].getElementsByTagName("label");
    //Build up the Logical Lineage of an input element
    var logicalLineage = new Array();
    //Last element should be the target itself
    logicalLineage.push(tempLineage[tempLineage.length-1]);
    //Add labels that are attached to the target
    for (var i = labelArray.length - 1; i >= 0; i--){
       if (labelArray[i].htmlFor == target.id){
          logicalLineage.push(labelArray[i]);
          }
       }
    //Add the rest of the physical lineage
    for (var i = tempLineage.length - 2; i >= 0; i--){
       logicalLineage.push(tempLineage[i]);
       }
    logicalLineage.reverse();
    return logicalLineage;
    }

/* from ideal-loader.js */
window.setTimeout("IDEAL_KEYHANDLER_Init()", 500);
window.setTimeout("IDEAL_TTS_Speak(document.title, 0, null);", 500);
