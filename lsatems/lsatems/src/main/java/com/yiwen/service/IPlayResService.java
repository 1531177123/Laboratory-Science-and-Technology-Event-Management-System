package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.PlayRes;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
public interface IPlayResService extends IService<PlayRes> {

    /**
     * 保存赛事资源
     * @param file 资源
     * @param playId 赛事id
     * @return 结果
     */
    boolean savePlayRes(MultipartFile file, String playId) throws Exception;

    /**
     * 分页查询赛事资源
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param playId 赛事id
     * @return 结果
     */
    IPage<PlayRes> selectPage(long currentPage, long pageSize, String playId);
}
