$(function () {
    $(document).ajaxStart(function () {
        Pace.restart();
    });
    initUserMenu();
    initUserInfo();
})

function initUserMenu() {
    //先将菜单清理掉
    $('.sidebar-menu > li:eq(0)').nextAll().remove();
    myAjax("get", false, "/sys/menu/list", null, showMenus);
}

function showMenus(result, status) {
    if (result.code === 200) {
        setMenu($('.sidebar-menu'), result.content);
        $('.smenuitem').bind('click',function(){
            if($(this).attr("href") == '#/view/file_manage'){
                $(".content").empty();//清除原有内容，避免页面内容冲突
                $(".content").load("/view/file/file_manage.html");
            }
        });
        //首页菜单单独处理点击事件
        $('.home-menu').bind('click', function () {
            $('.treeview').removeClass("active");
            $(this).parent().addClass("active");
        });
    }
}

function setMenu(menuid, menus) {
    var menuhtm = [];

    menuhtm.push("<li id='menu' class='treeview active'><a href='#/content' class='home-menu'> 首页</a></li>");

    $.each(menus, function (n, menudata) {
        menuhtm.push("<li id=\"menu" + menudata.menuId);
        menuhtm.push("\" class=\"treeview\">");
        //首页菜单 单独处理
        menuhtm.push("<a href=\#>");
        menuhtm.push(menudata.menuName);
        if (menudata.subMenus.length > 0) {
            menuhtm.push("<i class=\"fa fa-angle-left pull-right\"></i></a>")
            menuhtm.push(setSecondLevelMenu(menudata.subMenus));
        } else {
            menuhtm.push("</a>")
        }
        menuhtm.push("</li>");
    })
    menuid.append('' + menuhtm.join('') + '');
}

function setSecondLevelMenu(menus) {
    var menuhtm = [];
    menuhtm.push("<ul class='treeview-menu'>");
    $.each(menus, function (n, menudata) {
        menuhtm.push("<li><a class='smenuitem' href='" + menudata.menuUrl + "' onclick='changeMenuId(" + menudata.menuId + ")'>" + menudata.menuName);
        menuhtm.push("</a>")
        menuhtm.push("</li>");
    })
    menuhtm.push("</ul>");
    return '' + menuhtm.join('') + '';
}

/*function getMenuIcon(menutype) {
 var iconClass;
 switch (menutype) {
 case "0" :
 iconClass = "fa-home";
 break;//首页
 case "1" :
 iconClass = "fa-th";
 break;//应用管理
 }
 return iconClass;
 }*/

// 获取用户信息
function initUserInfo() {
    myAjax("get", false, "/loginUser", null, showUserName);
}
// 显示用户信息
function showUserName(result, status) {
    if (result.code === 200) {
        $('.username').html(result.content.userName);
    }
}

var contentMenuId;
function changeMenuId(menuId) {
    contentMenuId = menuId;
    $('#menuIdHidden').val(contentMenuId);
    localStorage.setItem('contentMenuId',menuId);
}
