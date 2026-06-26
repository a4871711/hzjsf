package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.AboutUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LINNINGXIA
 * @version 1.0
 * @date 2018-09-15 11:06
 */
@RestController
@RequestMapping("/api/settings")
public class ApiSettingsController extends BaseController {

    @Autowired
    private AboutUsService aboutUsService;

    /**
     * 关于我们
     * @return
     */
    @RequestMapping("/info")
    public R info(HttpServletRequest request){
        return R.reOk(aboutUsService.queryList());
    }

    /**
     * 版本下载  1.IOS，2.Android
     * @return
     */
    @RequestMapping("/downVersion")
    public R downVersion(Integer type, HttpServletRequest request) {
        /*String dataDirectory = Constant.APP_FILEPATH;
        String fileName = Constant.APP_FILENAME;
        Path file = Paths.get(dataDirectory, fileName);
        if (Files.exists(file)) {
            response.setContentType("application/x-gzip");
            try {
                response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
                Files.copy(file, response.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
        return R.reOk(aboutUsService.downVersion(type));
    }
}
