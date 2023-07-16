package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.domain.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-23
 */
public interface IMessageService extends IService<Message> {

    /**
     * 保存消息
     * @param msg 保存消息
     * @return 结果
     */
    boolean saveMessage(Message msg);

    /**
     * 更新消息
     * @param msg 更新消息
     * @return 结果
     */
    boolean updateMessageById(Message msg);

    /**
     * 查询消息
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param type 类型
     * @param creator 创建人
     * @return 结果
     */
    IPage<Message> selectPage(long currentPage, long pageSize, String type, String creator, String status);

    /**
     * 一键提醒功能
     * @return 结果
     */
    boolean pressTeams(String playId);

    /**
     * 保存反馈信息
     * @param msg  信息
     * @return 结果
     */
    boolean saveFeedBack(Message msg);
}
