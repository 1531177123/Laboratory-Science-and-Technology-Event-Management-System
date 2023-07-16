package com.yiwen.utils;

import com.yiwen.common.Constants;
import com.yiwen.domain.Message;
import com.yiwen.domain.Team;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-26
 * @see CommonUtils
 **/
public class CommonUtils
{

    public static Message assembleMessage(String type, String status, String relationId, String creatorId
                                        , String creator, String messageContent, String resPath, String toUser)
    {
        Message message = new Message();
        message.setStatus(status);
        message.setRelationId(relationId);
        message.setCreateTime(DateTimeUtil.getSysTime());
        message.setContent(messageContent);
        message.setCreator(creator);
        message.setCreatorId(creatorId);
        message.setType(type);
        message.setResPath(resPath);
        message.setToUser(toUser);
        return message;
    }

    public static String assembleMessageContent(String creator, String joinName, String applyContent)
    {
        return "[用户：" + creator + " ==申请加入==" + joinName + "]" + ": " + applyContent;
    }
}
