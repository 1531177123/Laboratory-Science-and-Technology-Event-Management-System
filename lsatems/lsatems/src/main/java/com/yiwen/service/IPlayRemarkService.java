package com.yiwen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiwen.domain.PlayRemark;

import java.util.List;

/**
 * <p>
 * 科技赛事评论表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-25
 */
public interface IPlayRemarkService extends IService<PlayRemark> {

    /**
     * 保存赛事评论
     * @param playRemark 评论
     * @return 结果
     */
    boolean savePlayRemark(PlayRemark playRemark);

    /**
     * 更新赛事评论
     * @param playRemark 评论
     * @return 结果
     */
    boolean updatePlayRemarkById(PlayRemark playRemark);

    /**
     * 查询赛事评论
     * @param playId 赛事id
     * @return 结果
     */
    List<PlayRemark> getTreeByPlayId(String playId);
}
