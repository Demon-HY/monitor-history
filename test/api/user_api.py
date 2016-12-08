# coding:utf8
from api import util


class UserApi(object):

    MODULE_NAME = "user"
    URL_PREFIX = "api"

    def __init__(self, xserver_addr, xserver_timeout, cookie_mode, device_name):
        self._xserver_addr = xserver_addr
        self._xserver_timeout = xserver_timeout
        self._cookie_mode = cookie_mode
        self._device_name = device_name

    def _invoke(self, method_name, post_body, is_parse_response, **kwargs):
        return util.json_invoke(self._xserver_addr,
                                self.MODULE_NAME,
                                self.URL_PREFIX,
                                method_name,
                                self._xserver_timeout,
                                self._device_name,
                                post_body,
                                is_parse_response,
                                self._cookie_mode,
                                **kwargs)

    def userRegister(self, name=None, email=None, phone=None, password=None, nick=None, qq=None, exattr=None):
        return self._invoke("userRegister", "", True)