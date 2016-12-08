#coding:utf-8
# Created: 10/04/2014

import base64
import os
import random

import retstat
import config
from api import auth_api
from api import user_api

TEST_USER_NAME_PREFIX = "demon_apitest_user_"
TEST_USER_NICKNAME_PREFIX = "demon_"


def get_admin_token():
    """
    使用配置中的管理员帐号密码登录, 获取一个 token
    """
    authapi = auth_api.AuthApi(config.DEMON_ADDR,
                               config.DEMON_TIMEOUT,
                               False,
                               config.DEVICE_NAME)

    stat, ret = authapi.login(name=config.ADMIN_NAME, password=config.ADMIN_PASSWORD, type='name')
    assert stat == retstat.OK
    return ret["token"]


def get_token_uid():
    """
    创建一个新用户, 并登录获取一个 token
    """
    userapi = user_api.UserApi(config.DEMON_ADDR,
                               config.DEMON_TIMEOUT,
                               False,
                               config.DEVICE_NAME)
    
    authapi = auth_api.AuthApi(config.DEMON_ADDR,
                               config.DEMON_TIMEOUT,
                               False,
                               config.DEVICE_NAME)
    
    account = TEST_USER_NAME_PREFIX + base64.b16encode(os.urandom(8))
    nickname = TEST_USER_NICKNAME_PREFIX + base64.b16encode(os.urandom(8))
    password = "test_" + base64.b16encode(os.urandom(8))
    stat, ret = userapi.userRegister(name=account, nick=nickname, password=password)
    assert stat == retstat.OK
    
    uid = ret["uid"]

    stat, ret = authapi.login(account, password)
    assert stat == retstat.OK
    return ret["token"], uid


def get_token():
    token, uid = get_token_uid()
    return token


class XApi:
    def __init__(self, addr, timeout, devname):
        common_params = (addr, timeout, False, devname)
        self.auth = auth_api.AuthApi(*common_params)
        self.user = user_api.UserApi(*common_params)

api = XApi(config.DEMON_ADDR,
           config.DEMON_TIMEOUT,
           config.DEVICE_NAME)


def random_phone():
    """
     随机产生一个电话号码
    """
    phone = ["13"]
    for item in range(0, 9):
        phone.append(str(random.randrange(9)))
    return "".join(phone)


def clean_user_by_prefix(prefix):
    """
    清理测试用户
    :param prefix:
    :return:
    """
    admin_token = get_admin_token()
    userapi = api.user
    
    stat, ret = userapi.searchUser(admin_token, "")
    assert stat == retstat.OK
    for userinfo in ret["rows"]:
        name = userinfo["name"]
        if name.startswith(prefix):
            userapi.adminDeleteUser(admin_token, userinfo["uid"])


def clean_up():
    clean_user_by_prefix(TEST_USER_NAME_PREFIX)


def assert_stat(func, in_outs):
    for stat_expected, params in in_outs:
        stat, ret = func(*params)
        assert stat == stat_expected, (func, in_outs)

if __name__ == '__main__':
    import sys
    import inspect
    if len(sys.argv) < 2:
        print "Usage:"
        for k, v in globals().items():
            if inspect.isfunction(v) and k[0] != "_":
                print sys.argv[0], k, str(v.func_code.co_varnames[:v.func_code.co_argcount])[1:-1].replace(",", "")
        sys.exit(-1)
    else:
        func = eval(sys.argv[1])
        args = sys.argv[2:]
        func(*args)


