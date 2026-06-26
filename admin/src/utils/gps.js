export const PI = 3.14159265358979324;
export const a = 6378245.0;
export const ee = 0.00669342162296594323;
export const x_pi = 3.14159265358979324 * 3000.0 / 180.0;

// wgs84坐标转百度坐标
export function wgs2bd(lat, lon) {
       let _wgs2gcj = wgs2gcj(lat, lon);
       let _gcj2bd = gcj2bd(_wgs2gcj[0], _wgs2gcj[1]);
       return _gcj2bd;
}

export function gcj2bd(lat, lon) {
       let x = lon, y = lat;
       let z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
       let theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
       let bd_lon = z * Math.cos(theta) + 0.0065;
       let bd_lat = z * Math.sin(theta) + 0.006;
       return [ bd_lat, bd_lon ];
}

export function bd2gcj(lat, lon) {
       let x = lon - 0.0065, y = lat - 0.006;
       let z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
       let theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
       let gg_lon = z * Math.cos(theta);
       let gg_lat = z * Math.sin(theta);
       return [ gg_lat, gg_lon ];
}

export function wgs2gcj(lat, lon) {
       let dLat = transformLat(lon - 105.0, lat - 35.0);
       let dLon = transformLon(lon - 105.0, lat - 35.0);
       let radLat = lat / 180.0 * PI;
       let magic = Math.sin(radLat);
       magic = 1 - ee * magic * magic;
       let sqrtMagic = Math.sqrt(magic);
       dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
       dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
       let mgLat = lat + dLat;
       let mgLon = lon + dLon;
       return [ mgLat, mgLon ];
}

export function transformLat(lon,lat) {
	   let ret = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
       ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0;
       ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
       ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI  / 30.0)) * 2.0 / 3.0;
       return ret;
}

export default function transformLon(lon,lat) {
       let ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
       ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0;
       ret += (20.0 * Math.sin(lon * PI) + 40.0 * Math.sin(lon / 3.0 * PI)) * 2.0 / 3.0;
       ret += (150.0 * Math.sin(lon / 12.0 * PI) + 300.0 * Math.sin(lon / 30.0 * PI)) * 2.0 / 3.0;
       return ret;
}