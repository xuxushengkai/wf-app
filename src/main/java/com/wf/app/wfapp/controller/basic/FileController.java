package com.wf.app.wfapp.controller.basic;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.wf.app.wfapp.annotation.CacheLock;
import com.wf.app.wfapp.dto.vo.user.LoginVO;
import com.wf.app.wfapp.service.FileService;
import com.wf.common.exception.WFException;
import com.wf.common.utils.StringUtils;
import com.wf.common.vo.ResultMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@Api(value = "文件接口", description = "文件接口")
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "上传图片", notes = "上传图片")
    @PostMapping("/upload/image")
    public ResultMessage uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        if (StringUtils.isBlank(url)) {
            // url为空，证明上传失败
            return ResultMessage.fail("上传失败");
        }
        return ResultMessage.success(url);
    }
}
