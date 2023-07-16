package com.yiwen.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.dao.ArticleDao;
import com.yiwen.dao.FileDao;
import com.yiwen.domain.Article;
import com.yiwen.domain.Files;
import com.yiwen.domain.UserLogin;
import com.yiwen.exception.BusinessException;
import com.yiwen.service.IFileService;
import com.yiwen.service.IUserService;
import com.yiwen.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 论坛文章信息表 服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileDao, Files> implements IFileService
{

    @Value("${files.upload.path}")
    private String fileUploadBasePath;

    @Autowired
    private FileDao fileDao;

    @Autowired
    private IUserService userService;


    @Override
    public String upload(MultipartFile file, String userId) throws Exception
    {

        Map<String, String> parameterMap = handleUploadParameter(file, userId);
        Files saveFiles = tidyFilesToSave(parameterMap, userId);
        int insert = fileDao.insert(saveFiles);
        if (insert == 1)
        {
            return saveFiles.getUrl();
        }
        else
        {

            throw new BusinessException(Code.FILE_UPLOAD_ERR, "文件上传失败!");
        }
    }

    /**
     * 处理上传参数
     * @param file 文件
     * @return 结果
    */
    private Map<String,String> handleUploadParameter(MultipartFile file, String userId) throws Exception
    {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        String uuid = IdUtil.fastSimpleUUID();
        String fileUuid = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadBasePath + fileUuid);
        if (!uploadFile.getParentFile().exists())
        {
            uploadFile.getParentFile().mkdirs();
        }
        file.transferTo(uploadFile);
        String url;
        String md5 = SecureUtil.md5(uploadFile);
        Files dbFile = getFilesByMd5(md5);
        if (dbFile != null)
        {
            url = dbFile.getUrl();
            uploadFile.delete();
        }
        else
        {
            url = "http://localhost:8085/files/" + fileUuid;
        }
        Map<String, String> resMap = new HashMap<>();
        resMap.put("originalFilename", originalFilename);
        resMap.put("size", String.valueOf(size/1024));
        resMap.put("type", type);
        resMap.put("url", url);
        resMap.put("md5", md5);
        return resMap;
    }

    @Override
    public Files uploadRes(MultipartFile file, String userId) throws Exception
    {

        Map<String, String> parameterMap = handleUploadParameter(file, userId);
        Files saveFiles = tidyFilesToSave(parameterMap, userId);
        if (save(saveFiles))
        {
            return saveFiles;
        }
        else
        {

            throw new BusinessException(Code.FILE_UPLOAD_ERR, "文件上传失败!");
        }
    }

    /**
     * 整理存储对象
     * @param parameterMap 参数集合
     * @param userId 用户id
     * @return 结果
     */
    private Files tidyFilesToSave(Map<String, String> parameterMap, String userId)
    {
        String url = parameterMap.get("url");
        //存储到数据库
        Files saveFiles = new Files();
        saveFiles.setName(parameterMap.get("originalFilename"));
        saveFiles.setSize(parameterMap.get("size"));
        saveFiles.setType(parameterMap.get("type"));
        saveFiles.setUrl(url);
        saveFiles.setMd5(parameterMap.get("md5"));
        saveFiles.setCreateTime(DateTimeUtil.getSysTime());
        saveFiles.setCreatorId(userId);
        return saveFiles;
    }

    @Override
    public IPage<Files> selectPage(long currentPage, long pageSize, String name)
    {
        LambdaQueryWrapper<Files> lambdaQueryWrapper = null;
        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name), Files::getName, name);
        lambdaQueryWrapper.orderByDesc(Files::getCreateTime);
        IPage<Files> iPage = fileDao.selectPage(new Page<Files>(currentPage, pageSize), lambdaQueryWrapper);
        List<Files> records = iPage.getRecords();
        for (Files record : records) {
            UserLogin loginById = userService.getLoginById(record.getCreatorId());
            record.setCreator(userService.getCurrentUserDetails(loginById).getName());
        }
        iPage.setRecords(records);
        return iPage;
    }

    @Override
    public Boolean delete(String id) {
        return fileDao.deleteById(id) == 1;
    }

    @Override
    public Boolean deleteBatchByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
        {
            return false;
        }
        return fileDao.deleteBatchIds(ids) == ids.size();
    }

    @Override
    public Result update(Files files)
    {
        files.setEditTime(DateTimeUtil.getSysTime());
        if (fileDao.updateById(files) == 1)
        {
            return new Result(Code.UPDATE_OK, null, "更新文件信息成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新文件信息失败");
    }

    /**
     * 通过文件md5查询文件
     * @param md5
     * @return 结果
     */
    private Files getFilesByMd5(String md5)
    {
        LambdaQueryWrapper<Files> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Files::getMd5, md5);
        List<Files> files = fileDao.selectList(queryWrapper);
        if (files != null && !files.isEmpty())
        {
            return files.get(0);
        }
        return  null;
    }

}
