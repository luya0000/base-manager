/**
 * Created by luya on 2018/6/10.
 */
//构造函数
function Router() {
    this.routes = {};
    this.currentUrl = '';
}
Router.prototype.route = function (path, callback) {
    this.routes[path] = callback || function () {};//给不同的hash设置不同的回调函数
};
Router.prototype.refresh = function () {
    this.currentUrl = location.hash.slice(1) == '' ? '/' : location.hash.slice(1);//如果存在hash值则获取到，否则设置hash值为/
    this.routes[this.currentUrl]();//根据当前的hash值来调用相对应的回调函数
};
Router.prototype.init = function () {
    window.addEventListener('load', this.refresh.bind(this), true);
    window.addEventListener('hashchange', this.refresh.bind(this), true);
}
//给window对象挂载属性
window.Router = new Router();
window.Router.init();

function routerPage(pageName) {
    $('.content').load(pageName);
}
Router.route('/', function () {
    routerPage('/view/content.html');
});
Router.route('/content', function () {
    routerPage('/view/content.html');
});
Router.route('/view/file_manage', function () {
    routerPage('/view/file/file_manage.html');
});
Router.route('/view/role', function () {
    routerPage('/view/role/role_content.html');
});
Router.route('/view/depart', function () {
    routerPage('/view/depart/depart_content.html');
});
Router.route('/view/menu', function () {
    routerPage('/view/menu/menu_content.html');
});
Router.route('/view/user', function () {
    routerPage('/view/user/user_content.html');
});
/*Router.route('/view/user/nomal', function () {
    routerPage('/view/user/user_nomal.html');
});*/
