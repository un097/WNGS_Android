package com.xuhai.wngs;

/**
 * Created by changliang on 14-10-8.
 */
public interface Constants {

    public static final int LOAD_SUCCESS = 0, LOAD_FAIL = -1, LOAD_REFRESH = 1, LOAD_MORE = 2,WEIDU_BBS = 12,JIFEN = 15;


    public static final int STATE_LOGIN = 1, STATE_LOGOUT = 2,STATE_REFRESH = 3;
    public static final int LOGIN_SUCCESS = 1, LOGIN_FAIL = -1, LOGOUT_SUCCESS = 2, LOGOUT_FAIL = -2;

    public static final int INITIAL_DELAY_MILLIS = 300;

    public static final String SPN_WNGS = "spn_wngs"; //数据存储
    public static final String SPN_SQIMG = "sqimg"; //小区ID
    public static final String SPN_SQID = "sqid"; //小区图标
    public static final String SPN_SQNAME = "sqname"; //小区名字
    public static final String SPN_PAGE_COUNT = "page_count"; //分页条数
    public static final String SPN_BBS = "bbs"; //未读信息
    public static final String SPN_INFO = "info"; //未读信息
    public static final String SPN_EXPRESS = "express"; //未读信息
    public static final String SPN_BALANCE = "balance"; //余额


    public static final String SPN_START_TIME = "starttime"; //开始时间
    public static final String SPN_END_TIME = "endtime"; //结束时间
    public static final String SPN_IS_LOGIN = "is_login";//用户是否登录
    public static final String SPN_UID = "uid"; //用户唯一ID
    public static final String SPN_USER_PHONE = "user_phone"; //用户手机
    public static final String SPN_USER_PWD = "user_pwd"; //用户密码

    public static final String SPN_USER_PASSWD = "user_passwd"; //用户密码
    public static final String SPN_USER_HEAD = "user_head"; //用户头像
    public static final String SPN_USER_NOTE = "user_note"; //个人说明
    public static final String SPN_BANK_UID = "bankuid"; //交易密码

    public static final String SPN_NICK_NAME = "nick_name"; //用户昵称
    public static final String SPN_USER_CHECKIN = "user_checkin"; //今日是否已签到
    public static final String SPN_AUTH = "auth"; //用户是否认证
    public static final String SPN_AUTH_NAME = "auth_name"; //用户认证姓名
    public static final String SPN_AUTH_PHONE = "auth_phone"; //用户认证电话
    public static final String SPN_AUTH_BUILDING = "auth_building"; //用户认证栋
    public static final String SPN_AUTH_UNIT = "auth_unit"; //认证单元
    public static final String SPN_AUTH_ROOM = "auth_room"; //认证房间号
    public static final String SPN_STOREID = "storeid"; //用户唯一ID
    public static final String SPN_POINTS_TOTLA = "points_total"; //积分数

    public static final String SPN_IS_FIRST_OPEN = "is_first_open"; //是否第一次打开应用
    public static final String SPN_IS_SELECT_SHEQU = "is_select_shequ"; //是否选择小区
    public static final String SPN_IS_BANKCHECK = "is_bankcheck";
    public static final String SPN_IS_BANKPWD = "is_bankpwd";

    public static final String SPN_USERID = "userid";
    public static final String SPN_CHANNELID = "channelid";

    public static final String SPN_INDICATOR_HEIGHT = "indicator_height";
    public static final String SPN_MAIN_TOP_HEIGHT = "main_top_height";

//    public static final String HTTP_SERVER1 = "http://wngs.xuhaisoft.com/api/"; //主地址
    public static final String HTTP_SERVER = "https://wngs.xuhaisoft.com/upgradeapi/"; //主地址

    public static final String HTTP_SIGNUP = HTTP_SERVER + "signup.php";//注册
    public static final String HTTP_LOGIN = HTTP_SERVER + "login.php";//登录
    public static final String HTTP_USER_INFO = HTTP_SERVER + "user_info.php";//个人信息

    public static final String HTTP_ABOUT = HTTP_SERVER + "about.php";//关于
    public static final String HTTP_FEEDBACK = HTTP_SERVER + "feedback.php";//意见反馈
    public static final String HTTP_CHANGEPLOT = HTTP_SERVER + "change_plot.php";//切换小区

    public static final String HTTP_SHEQU_LIST = HTTP_SERVER + "shequ_list.php";//小区列表
    public static final String HTTP_XGXX = HTTP_SERVER + "userinfo.php";//个人信息
    public static final String HTTP_LIVE_LIST = HTTP_SERVER + "list.php";//生活与商家列表
    public static final String HTTP_ADD_SHDZ = HTTP_SERVER + "address_add.php";//添加收货地址
    public static final String HTTP_REPAIR = HTTP_SERVER + "repair.php";//报修
    public static final String HTTP_SET_MOREN = HTTP_SERVER + "address_default.php";//设置默认地址
    public static final String HTTP__XIUGAI_DIZHI = HTTP_SERVER + "address_modify.php";//修改收货地址

    public static final String HTTP_REPAIR_LIST = HTTP_SERVER + "repair_list.php";//报修列表报修列表
    public static final String HTTP_CONVENIENCE_LIST = HTTP_SERVER + "convenience_list.php";//便民服务
    public static final String HTTP_INFO_LIST = HTTP_SERVER + "info_list.php";//公告列表
    public static final String HTTP_PHONE_LIST = HTTP_SERVER + "coninfo_list.php";//便民电话
    public static final String HTTP_BBS_LIST = HTTP_SERVER + "bbs_list.php";//论坛列表
    public static final String HTTP_ZFCX_LIST = HTTP_SERVER + "property_costs.php";//资费查询
    public static final String HTTP_GJXX = HTTP_SERVER + "butler_info.php";//管家信息
    public static final String HTTP_HD_LIST = HTTP_SERVER + "activity_list.php";//活动列表
    public static final String HTTP_BBS_POST = HTTP_SERVER + "bbs_post.php";//论坛发表内容
    public static final String HTTP_BBS_CONTENT = HTTP_SERVER + "bbs_content.php";//论坛内容
    public static final String HTTP_BBS_CONTENT_HUIFU = HTTP_SERVER + "bbs_reply.php";//论坛回复
    public static final String HTTP_SHDZ_LIST = HTTP_SERVER + "address_list.php";//收货地址
    public static final String HTTP_JFDD_LIST = HTTP_SERVER + "redeem_order_list.php";//积分订单列表
    public static final String HTTP_WDKD_LIST = HTTP_SERVER + "box_list.php";//快递列表
    public static final String HTTP_WDKD_XQ = HTTP_SERVER + "box_info.php";//快递详情
    public static final String HTTP_JFDD_ITEM_LIST = HTTP_SERVER + "redeem_order_info.php";//积分订单列表
    public static final String HTTP_GET_YANZHENG = HTTP_SERVER + "sms_get.php";//获取验证码
    public static final String HTTP_YANZHENG = HTTP_SERVER + "sms_auth.php";//验证验证码
    public static final String HTTP_BLD = HTTP_SERVER + "store.php";//便利店
    public static final String HTTP_BLD_CPLB = HTTP_SERVER + "goods_list.php";//便利店产品列表
    public static final String HTTP_BLD_SPXQ = HTTP_SERVER + "goods_info.php";//便利店商品详情
    public static final String HTTP_NEWPWD = HTTP_SERVER + "forget_passwd.php";//设置新密码
    public static final String HTTP_BANK_AGREEMENT = HTTP_SERVER + "bankcard_bound_agreement.php";//用户协议
    public static final String HTTP_REGISTER_AGREEMENT = HTTP_SERVER + "user_register_agreement.php";//用户协议
    public static final String HTTP_BUTLER = HTTP_SERVER + "butler_good.php";//管家点赞
    public static final String HTTP_YZRZ = HTTP_SERVER + "certification.php";//业主认证
    public static final String HTTP_CHECK = HTTP_SERVER + "checkin.php";//签到
    public static final String HTTP_JFDH_POST = HTTP_SERVER + "redeem_post.php";//积分兑换POST
    public static final String HTTP_WDJF_LIST = HTTP_SERVER + "points_list.php";//我的积分
    public static final String HTTP_WDDD_LIST = HTTP_SERVER + "order_list.php";//我的订单
    public static final String HTTP_WDDD_XQ = HTTP_SERVER + "order_detail.php";//订单详情
    public static final String HTTP_WDDD_PJ = HTTP_SERVER + "order_star.php";//订单评价
    public static final String HTTP_WDDD_QRSH = HTTP_SERVER + "order_confirm.php";//确认收货
    public static final String HTTP_JFDH_LIST = HTTP_SERVER + "redeem_list.php";//积分兑换
    public static final String HTTP_JFDHITEM_LIST = HTTP_SERVER + "redeem_info.php";//积分兑换详情
    public static final String HTTP_BLD_BUY = HTTP_SERVER + "store_buy.php";//便利店下单
    public static final String HTTP_PAY_DONE = HTTP_SERVER + "pay_done.php";//支付确认
    public static final String HTTP_PAY_TK = HTTP_SERVER + "pay_back.php";//退款
    public static final String HTTP_QXDD = HTTP_SERVER + "order_cancel.php";//取消订单
    public static final String HTTP_OPENDOOR = HTTP_SERVER + "open_door.php";//开门请求

    public static final String HTTP_BANK_LIST = HTTP_SERVER + "bank_list.php";//银行卡别表
    public static final String HTTP_BANK_CHECK = HTTP_SERVER + "bankcard_check.php";//银行卡号效验
    public static final String HTTP_BANKINFO_CHECK = HTTP_SERVER + "bankcard_info_cheak.php";//银行卡信息效验
    public static final String HTTP_BANK_SMSGET = HTTP_SERVER + "bankcard_sms_get.php";//获取验证码
    public static final String HTTP_BANK_SMSAUTH = HTTP_SERVER + "yz_sms_auth.php";//验证码验证
    public static final String HTTP_BANK_REMOVE = HTTP_SERVER + "bankcard_remove.php";//银行卡解绑
    public static final String HTTP_PWD_REALNAME = HTTP_SERVER + "realname_auth.php";//设置交易密码实名验证
    public static final String HTTP_PWD_FGTAUTH = HTTP_SERVER + "fgtpsw_realname_auth.php";//忘记交易密码实名验证

    public static final String HTTP_PWD_SET = HTTP_SERVER + "setpasswd.php";//设置交易密码
    public static final String HTTP_PWD_UPDATE = HTTP_SERVER + "updatetradpasswd.php";//修改交易密码

    public static final String HTTP_VERSION = HTTP_SERVER + "version.php";//检测更新

    public static final String HTTP_PWD_AUTH = HTTP_SERVER + "sz_sms_auth.php";//设置交易密码验证验证码
    public static final String HTTP_PWD_GET = HTTP_SERVER + "sz_sms_get.php";//设置交易密码 获取验证码
    public static final String HTTP_YUE_CHONG = HTTP_SERVER + "balance_recharge.php";//余额充值
    public static final String HTTP_YUE_TIXIAN = HTTP_SERVER + "balance_get.php";//余额提现
    public static final String HTTP_VACCOUNT = HTTP_SERVER + "vaccount_list.php";//交易明细
    public static final String HTTP_YUE_CHECK = HTTP_SERVER + "check_passwd.php";//检验交易密码
    public static final String HTTP_FORGER_PWD = HTTP_SERVER + "fgot_transpasswd.php";//忘记交易密码
    public static final String HTTP_GET_BALANCE = HTTP_SERVER + "get_balance.php";//获取余额

}