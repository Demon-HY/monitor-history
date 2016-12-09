# coding:utf8
import config
import hlp
import retstat
from api import auth_api


def test_auth_basic():
    authapi = auth_api.AuthApi(config.DEMON_ADDR,
                               config.DEMON_TIMEOUT,
                               False,
                               config.DEVICE_NAME)
    authapi = hlp.api.auth

    # 管理员登录
    stat, ret = authapi.login(config.ADMIN_EMAIL, config.ADMIN_PASSWORD, 'name')
    assert stat == retstat.OK
    token = ret["token"]


if __name__ == "__main__":
    test_auth_basic()

    print "auth api test is over"
