/**
 * 请求地址
 * 
 * @returns {String}
 */
function ip() {
	return "http://192.168.0.88/grape_mall/";
}

/**
 * 页面标题名
 * 
 * @returns {String}
 */
function titleName() {
	return "舍神商城";
}

/**
 * 地址栏获取参数 不存在undefined
 * 
 * @param param
 * @returns
 */
function getParameter(param) {
	var query = window.location.search;
	var iLen = param.length;
	var iStart = query.indexOf(param);
	if (iStart == -1)
		return "undefined";
	iStart += iLen + 1;
	var iEnd = query.indexOf("&", iStart);
	if (iEnd == -1)
		return query.substring(iStart);

	return query.substring(iStart, iEnd);
}

/**
 * 地址栏时间
 * 
 * @returns {String}
 */
function timestamp() {
	return 't=' + new Date().getTime();
}

/**
 * 监听返回键不刷新
 */
function backrefresh() {
	// 特殊监听返回,适配手机增加收货地址后返回不刷新
	pushHistory();
	window.addEventListener("popstate", function(e) {
		self.location = document.referrer;
	}, false);
	function pushHistory() {
		var state = {
			title : "title",
			url : "#"
		};
		window.history.pushState(state, "title", "#");
	}
}

/**
 * 修改地址栏参数
 */
function changeURLPar(par, par_value) {
	var destiny = window.location + "";
	var pattern = par + '=([^&]*)';
	var replaceText = par + '=' + par_value;
	if (destiny.match(pattern)) {
		var tmp = '/\\' + par + '=[^&]*/';
		tmp = destiny.replace(eval(tmp), replaceText);
		return (tmp);
	} else {
		if (destiny.match('[\?]')) {
			return destiny + '&' + replaceText;
		} else {
			return destiny + '?' + replaceText;
		}
	}
	return destiny + '\n' + par + '\n' + par_value;
}

/**
 * 监听返回
 */
function listenback() {
	var u = navigator.userAgent;
	isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); // ios终端
	// 特殊监听返回
	pushHistory();
	window.addEventListener("popstate", function(e) {
		if (isiOS) {
			var url = document.referrer;
			// 替换当前链接的历史记录，此时不会跳转页面
			history.replaceState({}, '', url);
			// 刷新当前页面，并且不留历史
			location.reload();
		} else {
			// alert(111);
			history.go(-1);
		}
	}, false);
	function pushHistory() {
		var state = {
			title : "title",
			url : "#"
		};
		window.history.pushState(state, "title", "#");
	}
}