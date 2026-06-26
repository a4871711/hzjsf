/* eslint-disable */
require('script-loader!file-saver');
import JSZip from 'jszip'
import {Notification} from 'element-ui'

export function export_txt_to_zip(th, jsonData, txtName, zipName) {
  const zip = new JSZip()
  const txt_name = txtName || 'file'
  const zip_name = zipName || 'file'
  const data = jsonData
  let txtData = `${th}\r\n`
  data.forEach((row) => {
    let tempStr = ''
    tempStr = row.toString()
    txtData += `${tempStr}\r\n`
  })
  zip.file(`${txt_name}.txt`, txtData)
  zip.generateAsync({
    type: "blob"
  }).then((blob) => {
    saveAs(blob, `${zip_name}.zip`)
  }, (err) => {
    alert('导出失败')
  })
}



/* 
 * 批量下载
 * 结合vue-qr jszip
 * 传入数据格式如下：
 * [{name:"你的图片名称，不能重复",baseImg:"你的base64图片"}]
 * 要注意一下这个传入的base64要先切割一下“data:image/png;base64,”.slice(22)
 */

export function StoreDowQrcode(arr, blogTitle = "二维码") {
  var zip = new JSZip();
  var imgs = zip.folder(blogTitle);
  var baseList = [];
  var _this = this;
  for (var i = 0; i < arr.length; i++) {
    let name = arr[i].name; 
    let image = new Image();
    // 解决跨域 Canvas 污染问题
    image.setAttribute("crossOrigin", "anonymous");
    image.onload = function() {
      let canvas = document.createElement("canvas");
      canvas.width = image.width;
      canvas.height = image.height;
      let context = canvas.getContext("2d");
      context.drawImage(image, 0, 0, image.width, image.height);
      let url = canvas.toDataURL(); 
      canvas.toDataURL("image/png");
      baseList.push({ name: name, img: url.substring(22) });
      if (baseList.length === arr.length) {
        if (baseList.length > 0) {
          Notification({
            title: "成功",
            message: "即将下载",
            type: "success"
          });
          for (let k = 0; k < baseList.length; k++) {
            imgs.file(baseList[k].name + ".png", baseList[k].img, {
              base64: true
            });
          }
          zip.generateAsync({ type: "blob" }).then(function(content) {
            saveAs(content, blogTitle + ".zip");
          });
        } else {
          Notification.error({
            title: "错误",
            message: "暂无图片可下载"
          });
        }
      }
    };
    image.src = arr[i].baseImg = `data:image/png;base64,${arr[i].baseImg}`;
  }
}
