package com.manage.common;

public class Constants {

    /*jwt的角色放入request的key*/
    public static final String JWT_ROLES_KEY = "jwt_roles_key";
    public static final String JWT_SUB_KEY = "jwt_sub_key";
    public static final String JWT_ACCOUNT_KEY = "jwt_account_key";

    /*管理员角色*/
    public static final Integer SYSTEM_TYPE = 1;
    public static final Integer ADMIN_TYPE = 2;
    public static final Integer NOMAL_TYPE = 3;
    public static final Integer DEFAULT_TYPE = -1;


    public static final Integer MENU_TYPE_SYSTEM = 1;
    public static final Integer MENU_TYPE_ADMIN = 2;
    public static final Integer MENU_TYPE_DEFAULT = 3;
    public static final Integer MENU_DOC_PARENTID = 11;

    // 状态 0有效、1无效
    public static final Integer STATUE_VALID = 1;
    public static final Integer STATUE_INVALID = 0;

    // 权限常量
    public static final String PERM_TYPE_SELECT = "SEARCH_LIST"; // 查询文件列表
    public static final String PERM_TYPE_DOWNLOAD = "DOWN_FILE"; //下载文件
    public static final String PERM_TYPE_UPLOAD = "UP_FILE";// 上传文件
    public static final String PERM_TYPE_DELETE = "DEL_FILE";// 删除文件
    public static final String PERM_TYPE_DEPART_ADMIN = "DEPART_ADMIN";// 普通人员管理权限
    public static final String PERM_TYPE_USER_MANAGE = "USER_MANAGE";// 人员管理的权限
    public static final String PERM_TYPE_MENU_MANAGE = "MENU_MANAGE";// 菜单管理的权限

    // --------------------分页常量-------------------
    /*当前页*/
    public static final Integer PAGEHELPER_PAGE_CURRENT = 0;
    /*每页显示条数*/
    public static final Integer PAGEHELPER_PAGE_SIZE = 10;

    // 文件访问地址
    public static final String DEPART_FILE_PATH = "#/view/file_manage";

    //
    public static final String DATE_FORMAT_YMDHMS = "yyyyMMddHHmmss";
    //
    public static final String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";


}
