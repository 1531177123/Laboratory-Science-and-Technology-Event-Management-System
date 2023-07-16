package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.common.Result;
import com.yiwen.domain.Files;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * <p>
 * 论坛文章信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
public interface IFileService extends IService<Files>
{
    /**
     * 上传
     * @param file 文件
     * @return 结果
     */
    String upload(MultipartFile file, String userId) throws Exception;

    Files uploadRes(MultipartFile file, String userId) throws Exception;

    IPage<Files> selectPage(long currentPage, long pageSize, String name);

    Boolean delete(String id);

    Boolean deleteBatchByIds(List<String> ids);

    Result update(Files files);
}
