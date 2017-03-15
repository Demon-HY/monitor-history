var getContentType = function(ext) {
    var contentType = '';
    switch (ext) {
        case ".html":
        case ".tpl":
            contentType = "text/html";
            break;
        case ".js":
            contentType = "text/javascript";
            break;
        case ".json":
            contentType = "application/json";
            break;
        case ".css":
            contentType = "text/css";
            break;
        case ".gif":
            contentType = "image/gif";
            break;
        case ".jpg":
            contentType = "image/jpeg";
            break;
        case ".png":
            contentType = "image/png";
            break;
        case ".ico":
            contentType = "image/icon";
            break;
        case ".swf":
            contentType = "application/x-shockwave-flash";
            break;
        case ".svg":
            contentType = "image/svg+xml";
            break;
        default:
            contentType = "application/octet-stream";
    }
    return contentType;
};
module.exports = getContentType;