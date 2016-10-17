var exec = require('cordova/exec');


var K3A = function() { };

K3A.GetSerialNumber = function(arg0, success, error) {
    exec(success, error, "K3A", "GetSerialNumber", [arg0]);
};

K3A.Hello = function(arg0, success, error) {
    exec(success, error, "K3A", "Hello", [arg0]);
};

K3A.Read = function(arg0, success, error) {
    exec(success, error, "K3A", "Read", [arg0]);
};

K3A.Init = function(arg0, success, error) {
    exec(success, error, "K3A", "Init", [arg0]);
};

K3A.Stop = function(arg0, success, error) {
    exec(success, error, "K3A", "Stop", [arg0]);
};


module.exports = K3A;
