// This is the equivalent of the old waitsFor/runs syntax
// which was removed from Jasmine 2
waitsForAndRuns = function(escapeFunction, runFunction, escapeTime, failOnTimeout) {
    // check the escapeFunction every millisecond so as soon as it is met we can escape the function
    var interval = setInterval(function() {
        if (escapeFunction()) {
            clearMe();
            runFunction();
        }
    }, 1);

    // in case we never reach the escapeFunction, we will time out
    // at the escapeTime
    var timeOut = setTimeout(function() {
        clearMe();
        if (failOnTimeout) {
            expect(false).toBe(true);
        } else {
            runFunction();
        }
    }, escapeTime);

    // clear the interval and the timeout
    function clearMe(){
        clearInterval(interval);
        clearTimeout(timeOut);
    }
};