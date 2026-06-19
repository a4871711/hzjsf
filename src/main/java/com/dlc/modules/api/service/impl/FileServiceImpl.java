package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.UserRecordDao;
import com.dlc.modules.api.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private UserRecordDao userRecordDao;
    /**
     *文件上传处理方法
     * @param file
     * @param path
     * @return
     */
    @Override
    public File upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        File tempFile = null;
        try {
            tempFile = new File(path, new Date().getTime() + String.valueOf(fileName));
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdir();
            }
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            file.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 版本更新
     * @param userId
     * @return
     */
    @Override
    public File updateVersioin(Long userId) {
        //先查一下当前用户的版本号和newVersion，如果已经是最新则不需要更新
        return null;
    }

}
