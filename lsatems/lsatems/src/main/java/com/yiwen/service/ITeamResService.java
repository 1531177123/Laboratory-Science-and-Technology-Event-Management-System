package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.PlayRes;
import com.yiwen.domain.TeamRes;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-26
 */
public interface ITeamResService extends IService<TeamRes> {

    /**
     * 保存组队资料
     * @param file 文件
     * @param teamId 组队id
     * @return 结果
     */
    boolean savePlayRes(MultipartFile file, String teamId) throws Exception;

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param teamId  组队id
     * @return 结果
     */
    IPage<TeamRes> selectPage(long currentPage, long pageSize, String teamId);
}
