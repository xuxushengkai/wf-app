package com.wf.app.wfapp.controller.basic;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.wf.app.wfapp.annotation.CacheLock;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.common.exception.WFException;
import com.wf.common.vo.ResultMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @ClassName FileController
 * @Description 文件方法
 * Date 2019/8/28 16:37
 **/
@RestController
@Api(value = "文件接口", description = "人员接口")
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @ApiOperation(value = "账户登录", notes = "账户登录")
    @PostMapping("/upload/normal")
    public ResultMessage uploadImage() {

        File file = new File("D:\\test\\baby.png");
        // 上传并且生成缩略图
        StorePath storePath = null;
        try {
            storePath = this.storageClient.uploadFile(
                    new FileInputStream(file), file.length(), "png", null);
        } catch (FileNotFoundException e) {
             log.info(e.getMessage());
             throw new WFException("上传文件失败");
        }
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
        return ResultMessage.success(storePath.getFullPath());
    }

    @ApiOperation(value = "账户登录", notes = "账户登录")
    @PostMapping("/upload/other")
    public ResultMessage uploadAndCreateThumb() {

        File file = new File("D:\\test\\baby.png");
        // 上传并且生成缩略图
        StorePath storePath = null;
        try {
            storePath = this.storageClient.uploadImageAndCrtThumbImage(
                    new FileInputStream(file), file.length(), "png", null);
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
            throw new WFException("上传文件失败");
        }
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
        // 获取缩略图路径
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(path);
        return ResultMessage.success(storePath.getFullPath());
    }



}
