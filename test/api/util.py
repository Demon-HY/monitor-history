# coding:utf-8

import json
import urllib
import urllib2
import sys
import mimetypes
import mimetools
import os

PRINT_CURL_CMD = False
if len(sys.argv) >= 2 and sys.argv[1].lower() == "curl":
    PRINT_CURL_CMD = True


def _print_curl_cmd(url, post_body, headers):
    if not PRINT_CURL_CMD:
        return

    _headers = []
    for name, value in headers.iteritems():
        _headers.append("-H '%s: %s'" % (name, value))

    cmd = "curl -i -d '%s' %s '%s'" % (post_body, " ".join(_headers), url)
    print cmd


def json_invoke(server_addr, module_name, url_prefix, method_name, timeout,
                device_name, post_body, is_parse_response, is_cookie_mode, **kwargs):
    return json_invoke_with_header(server_addr, module_name, url_prefix,
                                  method_name,
                                  timeout,
                                  device_name,
                                  post_body,
                                  is_parse_response,
                                  is_cookie_mode,
                                  {},
                                  **kwargs)


def json_invoke_with_header(server_addr, module_name, url_prefix, method_name, timeout,
                device_name, post_body, is_parse_response, is_cookie_mode, headers, **kwargs):
    url = "%s/%s/%s/%s" % (server_addr, module_name, url_prefix, method_name)

    headers["X-Device"] = device_name
    if is_cookie_mode and "token" in kwargs:
        headers["Cookie"] = "token=%s" % kwargs.pop("token")

    if post_body:
        url = "%s?%s" % (url, urllib.urlencode(kwargs))
    else:
        post_body = json.dumps(kwargs)

    print('url========', url)
    print('post_body==', post_body)
    print('headers====', headers)

    req = urllib2.Request(url, post_body, headers)
    _print_curl_cmd(url, post_body, headers)
    try:
        resp = urllib2.urlopen(req, timeout=timeout)
    except urllib2.HTTPError, e:
        if is_parse_response:
            raise
        else:
            return (e.code, None)

    data = resp.read()
    if is_parse_response:
        ret = json.loads(data)
        stat = ret["stat"]
        if is_cookie_mode:
            cookie_str = resp.headers.getheader("set-cookie", "")
            for kv_str in cookie_str.split(";"):
                if not kv_str.startswith("token"):
                    continue
                ret["token"] = kv_str[6:].strip()
        return stat, ret

    else:
        return resp.code, data

def encode_multipart_formdata(fields, files=[]):
    """
    fields is a sequence of (name, value) elements for regular form fields.
    files is a sequence of (name, filepath, filedata) elements for data to be uploaded as files
    Return (content_type, body) ready for httplib.HTTP instance
    """
    BOUNDARY = mimetools.choose_boundary()
    CRLF = '\r\n'
    L = []
    for (key, value) in fields:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"' % key)
        L.append('')
        L.append(value)
    for (key, filepath, filedata) in files:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"; filename="%s"' % (key, os.path.basename(filepath)))
        L.append('Content-Type: %s' % get_content_type(filepath))
        L.append('')
        L.append(filedata)
    L.append('--' + BOUNDARY + '--')
    L.append('')
    body = CRLF.join(L)
    content_type = 'multipart/form-data; boundary=%s' % BOUNDARY
    return content_type, body

def get_content_type(filepath):
    return mimetypes.guess_type(filepath)[0] or 'application/octet-stream'

class ApiBase:

    MODULE_NAME = ""
    URL_PREFIX = ""

    def __init__(self, xserver_addr, xserver_timeout, cookie_mode, device_name):
        self._xserver_addr = xserver_addr
        self._xserver_timeout = xserver_timeout
        self.cookie_mode = cookie_mode
        self._device_name = device_name

    def _invoke(self, method_name, post_body, is_parse_response, **kwargs):
        assert self.MODULE_NAME
        assert self.URL_PREFIX

        return json_invoke(self._xserver_addr,
                           self.MODULE_NAME,
                           self.URL_PREFIX,
                           method_name,
                           self._xserver_timeout,
                           self._device_name,
                           post_body,
                           is_parse_response,
                           self.cookie_mode,
                           **kwargs)

