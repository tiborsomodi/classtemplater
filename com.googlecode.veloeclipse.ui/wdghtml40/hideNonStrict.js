var newRules = new Array();
var hideNonStrictAction = null;

function writeButton(what) {
  if (document.styleSheets && (document.all || document.getElementById)
    && document.styleSheets[0] != null)
  {
    document.write("<p><input type=button value=\"Hide non-strict " + what
      + "\" id=toggler accesskey=s onclick=\"toggleNonStrict('"
      + what + "')\"></p>");
  }
}

function toggleNonStrict(what) {
  var sheet = document.styleSheets[0];
  var toggler = null;

  if (document.all) {
    toggler = document.all.toggler;

    if (toggler != null) {
      if (toggler.value == "Hide non-strict " + what) {
        sheet.addRule(".transitional", "display:none");
        newRules.push(sheet.rules.length - 1);

        sheet.addRule(".transitional", "speak:none");
        newRules.push(sheet.rules.length - 1);

        sheet.addRule(".frameset", "display:none");
        newRules.push(sheet.rules.length - 1);
      
        sheet.addRule(".frameset", "speak:none");
        newRules.push(sheet.rules.length - 1);
      
        if (hideNonStrictAction != null) {
          hideNonStrictAction(true);
        }
        toggler.value = "Show non-strict " + what;

      } else {
        while (newRules.length > 0) {
          sheet.removeRule(newRules.pop());
        }
        if (hideNonStrictAction != null) {
          hideNonStrictAction(false);
        }
        toggler.value = "Hide non-strict " + what;
      }
    }

  } else {
    toggler = document.getElementById('toggler');

    if (toggler != null) {
      if (toggler.value == "Hide non-strict " + what) {
        newRules.push(sheet.insertRule(".transitional {display:none}",sheet.cssRules.length));
        newRules.push(sheet.insertRule(".transitional {speak:none}",sheet.cssRules.length));
        newRules.push(sheet.insertRule(".frameset {display:none}",sheet.cssRules.length));
        newRules.push(sheet.insertRule(".frameset {speak:none}",sheet.cssRules.length));

        if (hideNonStrictAction != null) {
          hideNonStrictAction(true);
        }
        toggler.value = "Show non-strict " + what;
      } else {
        while (newRules.length > 0) {
          sheet.deleteRule(newRules.pop());
        }
        if (hideNonStrictAction != null) {
          hideNonStrictAction(false);
        }
        toggler.value = "Hide non-strict " + what;
      }
    }
  }
}
