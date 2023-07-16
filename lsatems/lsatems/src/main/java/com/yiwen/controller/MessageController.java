package com.yiwen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.domain.Message;
import com.yiwen.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-23
 */
@RestController
@RequestMapping("/message")
public class MessageController
{
    @Autowired
    private IMessageService messageService;

    @PostMapping
    public Result save(@RequestBody Message msg)
    {
        boolean flag = messageService.saveMessage(msg);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加消息成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加消息失败");
    }

    @PostMapping("/FeedBack")
    public Result saveFeedBack(@RequestBody Message msg)
    {
        boolean flag = messageService.saveFeedBack(msg);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "反馈消息成功");
        }
        return new Result(Code.SAVE_ERR, null, "反馈消息失败");
    }

    @PutMapping
    public Result update(@RequestBody Message msg)
    {
        boolean flag = messageService.updateMessageById(msg);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新消息成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新消息失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = messageService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除消息成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除消息失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = messageService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除消息成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除消息失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
                , @RequestParam(required = false) String type, @RequestParam(required = false) String creator
            , @RequestParam(required = false) String status)
    {
        IPage<Message> messages = messageService.selectPage(currentPage, pageSize, type, creator, status);
        if (messages != null)
        {
            return new Result(Code.GET_OK, messages, "查询消息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新消息失败");
        }
    }

    @GetMapping("/pressTeams/{playId}")
    public Result pressTeams(@PathVariable String playId)
    {
        boolean msgFlag = messageService.pressTeams(playId);
        if (msgFlag)
        {
            return new Result(Code.COMMON_OK, null, "一键提醒成功");
        }
        else
        {
            return new Result(Code.COMMON_ERR, null, "一键提醒失败");
        }
    }

}

