package com.yiwen.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Files;
import com.yiwen.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-12
 * @see FileController
 **/
@RestController
@RequestMapping("/files")
public class FileController
{

    @Value("${files.upload.path}")
    private String fileUploadBasePath;

    @Autowired
    private IFileService fileService;

    @PostMapping("/upload/{userId}")
    public String upload(@RequestParam MultipartFile file, @PathVariable String userId) throws Exception
    {
        return fileService.upload(file, userId);
    }

    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response)throws Exception
    {
        File file = new File(fileUploadBasePath + fileUUID);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        os.write(FileUtil.readBytes(file));
        os.flush();
        os.close();
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name)
    {
        IPage<Files> filesIPages = fileService.selectPage(currentPage, pageSize, name);
        if (filesIPages != null)
        {
            return new Result(Code.GET_OK, filesIPages, "查询文件信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询文件信息失败");
        }
    }

    /**
     * 删除文件
     * @param id id
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id)
    {
        Boolean flag = fileService.delete(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除文件成功");
        }
        else
        {
            return new Result(Code.DELETE_ERR, null, "删除文件失败");
        }
    }

    /**
     * 批量删除文件
     * @param ids ids
     * @return 结果
     */
    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        Boolean flag = fileService.deleteBatchByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除文件成功");
        }
        else
        {
            return new Result(Code.DELETE_ERR, null, "批量删除文件失败");
        }
    }

    /**
     * 更新
     * @return 结果
     */
    @PutMapping
    public Result update(@RequestBody Files files)
    {
        return fileService.update(files);
    }
}
