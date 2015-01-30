// "mock" away the foundation jquery plugin, or we get some curious errors from foundation like
// Syntax error, unrecognized expression: [data-'Times New Roman'-topbar]
$.fn.foundation = function () {
};
$.fn.fdatepicker = function () {
};
var bowser = {
};
