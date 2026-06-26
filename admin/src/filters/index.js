/**
 * 隐藏手机号码
 * @param val {Number, String} 转换的字符串对象
 * @param retain {Number} 保留位数
 * @return {String}
 */
export const privatePhone = function(val,retain = 4){
    if(!NUMBER(val) || String(val).length !== 11 || retain==0 ) return val;
    let phone = String(val)
    let digit = 11 - 3 - retain
    let reg = new RegExp(`^(\\d{3})\\d{${digit}}(\\d{${retain}})$`)
    return mobile.replace(reg,`$1${'*'.repeat(digit)}$2`)
}

/**
 * 网路图片域名拼接
 * @param url {String}
 * @return {String}
 */
export const formatImg = function(url) {
		if (url){
			return url.startsWith('http') ? url :  process.env.VUE_APP_IMG + url;
		} else{
			return url;
		}
}


export const getYMD = function(t, format = '-'){
	t = t.toString().length === 13 ? t : t * 1000
	let date = new Date(t),
			Y = date.getFullYear(),
			M = date.getMonth() + 1,
			D = date.getDate() + ' ';
	M = M < 10 ? '0' + M : M
	D = D < 10 ? '0' + D : D
	return [Y, M, D].join(format)
}


export const getHMS = function(t,format = ':',unit=false) {
	t = t.toString().length === 13 ? t : t * 1000
	let date = new Date(t),
			h = date.getHours(),
			m = date.getMinutes(),
			s = date.getSeconds();
	h = h < 10 ? '0' + h : h
	m = m < 10 ? '0' + m : m
	s = s < 10 ? '0' + s : s
	return unit===true?[h, m, s].join(format):[h, m].join(format)
}


export const getDeta = function(time, format = '-,:') {
	if (!time) {
		return '-'
	}
	time = time.toString().length === 13 ? time : time * 1000
	let sep = format.split(',')
	return `${getYMD(time, sep[0])} ${getHMS(time, sep[1])}`
}


//时间转换
export const formatTime=function(date,format) {

  function  formatNumber(n) {
    n = n.toString()
    return n[1] ? n : '0' + n
  }


  var number=new Date(date).getTime();
  var formateArr  = ['Y','M','D','h','m','s'];
  var returnArr   = [];

  number+='';
  if(number.length==10){
    var date = new Date(number * 1000);
  }else{
    var date = new Date(number*1);
  }


  returnArr.push(date.getFullYear());
  returnArr.push(formatNumber(date.getMonth() + 1));
  returnArr.push(formatNumber(date.getDate()));

  returnArr.push(formatNumber(date.getHours()));
  returnArr.push(formatNumber(date.getMinutes()));
  returnArr.push(formatNumber(date.getSeconds()));

  for (var i in returnArr)
  {
    format = format.replace(formateArr[i], returnArr[i]);
  }
  return format;
}
