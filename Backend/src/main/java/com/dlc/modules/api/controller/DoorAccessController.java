package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.DoorAccessService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** 开门码页面专用接口；不改变旧 getOpenDoorQR 的会员状态语义。 */
@RestController
@RequestMapping("/api/doorAccess")
public class DoorAccessController extends BaseController {

    @Autowired
    private DoorAccessService doorAccessService;

    @RequestMapping(value = "/qrcode", method = RequestMethod.POST)
    public R qrcode(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        UserInfoVo user = getUserVo(request);
        Map<String, Object> decision = doorAccessService.qrcode(user, params);
        boolean granted = Boolean.TRUE.equals(decision.remove("granted"));
        Object data = decision.remove("data");
        if (!granted) {
            String message = String.valueOf(decision.remove("message"));
            R error = R.reError(message);
            for (Map.Entry<String, Object> entry : decision.entrySet()) {
                error.put(entry.getKey(), entry.getValue());
            }
            return error;
        }
        R ok = R.reOk(data);
        for (Map.Entry<String, Object> entry : decision.entrySet()) {
            ok.put(entry.getKey(), entry.getValue());
        }
        return ok;
    }
}
