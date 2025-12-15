define(["server", "commons", "constant", "appContext", "appModal"], function (
    server, commons, constant, appContext, appModal) {
    var JDS = server.JDS;
    var StringUtils = commons.StringUtils;

    var demoFileSecDevJsFun = function (options) {
        /**
         * options.params.file
         * {
            fileID: "2754fe8721494cdcbc5f3b4304754fb9"
            fileName: "1.doc"
            fileSize: 22528,
            ...
            }
         */
        console.log(options.params.file);


    };
    return demoFileSecDevJsFun;
});