function warnBeforePageUnload() {
	if (1 != 1) {
	    var msg = "If you leave the page you may lose unsaved data. Are you sure you want to leave?";
        if(/Firefox[\/\s](\d+)/.test(navigator.userAgent) && new Number(RegExp.$1) >= 4) {
            if(confirm(msg)) {
                history.go();
            } else {
                window.setTimeout(function() {window.stop(); }, 1);
            }
        } else {
            return msg;
        }
	}
};