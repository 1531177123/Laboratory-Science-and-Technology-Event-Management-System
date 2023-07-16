package com.yiwen.controller;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Code;
import com.yiwen.common.Result;
import com.yiwen.config.AuthAccess;
import com.yiwen.controller.dto.LabShowDTO;
import com.yiwen.controller.dto.UserShowDTO;
import com.yiwen.domain.Lab;
import com.yiwen.domain.UserLogin;
import com.yiwen.service.ILabService;
import com.yiwen.service.IUserLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 实验室详细信息表 前端控制器
 * </p>
 *
 * @author yiwen
 * @since 2023-03-21
 */
@RestController
@RequestMapping("/labs")
public class LabController
{
    @Autowired
    private ILabService labService;

    @Autowired
    private IUserLabService userLabService;

    @PostMapping
    public Result save(@RequestBody Lab lab)
    {
        boolean flag = labService.saveLab(lab);
        if (flag)
        {
            return new Result(Code.SAVE_OK, null, "添加实验室成功");
        }
        return new Result(Code.SAVE_ERR, null, "添加实验室失败");
    }

    @PutMapping
    public Result update(@RequestBody Lab lab)
    {
        boolean flag = labService.updateLabById(lab);
        if (flag)
        {
            return new Result(Code.UPDATE_OK, null, "更新实验室成功");
        }
        return new Result(Code.UPDATE_ERR, null, "更新实验室失败");
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id)
    {
        boolean flag = labService.removeById(id);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "删除实验室信息成功");
        }
        return new Result(Code.DELETE_ERR, null, "删除实验室信息失败");
    }

    @PostMapping("/del/batch")
    public Result delete(@RequestBody List<String> ids)
    {
        boolean flag = labService.removeByIds(ids);
        if (flag)
        {
            return new Result(Code.DELETE_OK, null, "批量删除实验室成功");
        }
        return new Result(Code.DELETE_ERR, null, "批量删除实验室失败");
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam long currentPage, @RequestParam long pageSize
            , @RequestParam(required = false) String name, @RequestParam(required = false) String type
            , @RequestParam(required = false) String code, @RequestParam(required = false) String school
            , @RequestParam(required = false) String college)
    {
        IPage<Lab> labs = labService.selectPage(currentPage, pageSize, name, type, code, school, college);
        if (labs != null)
        {
            return new Result(Code.GET_OK, labs, "查询实验室信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新实验室信息失败");
        }
    }

    /**
     * 分页查询
     * @return 结果
     */
    @GetMapping("/selectPageByUserId")
    public Result selectPageByUserId(@RequestParam long currentPage, @RequestParam long pageSize, @RequestParam String userId)
    {
        IPage<Lab> labs = labService.selectPageByUserId(currentPage, pageSize, userId);
        if (labs != null)
        {
            return new Result(Code.GET_OK, labs, "查询实验室信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新实验室信息失败");
        }
    }


    /**
     * 按条件查询所有
     * @return 结果
     */
    @GetMapping("/findAll")
    public Result findAll(@RequestParam(required = false) String userId, @RequestParam(required = false) String name
            , @RequestParam(required = false) String priority)
    {
        List<Lab> labs = labService.getAll(userId, name, priority);
        if (labs != null)
        {
            return new Result(Code.GET_OK, labs, "查询实验室信息成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查新实验室信息失败");
        }
    }


    /**
     * 申请
     * @return 结果
     */
    @GetMapping("/applyLab")
    public Result selectPage( @RequestParam String userId, @RequestParam String labId, @RequestParam String content)
    {
        boolean saveFlag = userLabService.saveById(userId, labId, content);
        if (saveFlag)
        {
            return new Result(Code.GET_OK, null, "申请成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "申请失败");
        }
    }

    /**
     * 退出
     * @return 结果
     */
    @GetMapping("/quitLab")
    public Result quitLab( @RequestParam String userId, @RequestParam String labId)
    {
        boolean quitLabFlag = userLabService.quitLab(userId, labId);
        if (quitLabFlag)
        {
            return new Result(Code.GET_OK, null, "退出成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "退出失败");
        }
    }

    /**
     * 查询
     * @return 结果
     */
    @GetMapping("getById/{id}")
    public Result getById(@PathVariable String id)
    {
        Lab lab = labService.getLabById(id);
        if (lab != null)
        {
            return new Result(Code.GET_OK, lab, "查询成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "查询失败");
        }
    }

    /**
     * 退出
     * @return 结果
     */
    @GetMapping("/getMembers")
    public Result getMembers(@RequestParam String labId)
    {
        List<UserShowDTO> labMembers = userLabService.getMembers(labId);
        if (labMembers != null)
        {
            return new Result(Code.GET_OK, labMembers, "获取管理数据成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "无权限操作");
        }
    }

    /**
     * 任命管理
     * @return 结果
     */
    @GetMapping("/commissionAdmin")
    public Result commissionAdmin(@RequestParam String labId, @RequestParam String userId)
    {
        List<UserShowDTO> labMembers = userLabService.commissionAdmin(labId, userId);
        if (labMembers != null)
        {
            return new Result(Code.GET_OK, labMembers, "任命管理成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "任命管理失败");
        }
    }

    /**
     * 取消管理
     * @return 结果
     */
    @GetMapping("/cancelCommissionAdmin")
    public Result cancelCommissionAdmin(@RequestParam String labId, @RequestParam String userId)
    {
        List<UserShowDTO> labMembers = userLabService.cancelCommissionAdmin(labId, userId);
        if (labMembers != null)
        {
            return new Result(Code.GET_OK, labMembers, "取消管理成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "取消管理失败");
        }
    }

    /**
     * 取消管理
     * @return 结果
     */
    @GetMapping("/forcePropose")
    public Result forcePropose(@RequestParam String labId, @RequestParam String userId)
    {
        List<UserShowDTO> labMembers = userLabService.forcePropose(labId, userId);
        if (labMembers != null)
        {
            return new Result(Code.GET_OK, labMembers, "强制提出成功");
        }
        else
        {
            return new Result(Code.GET_ERR, null, "强制提出失败");
        }
    }

    /**
     * 导出接口
     * @param response
     * @return 结果
     */
    @AuthAccess
    @GetMapping("/export")
    public void export(HttpServletResponse response)throws Exception
    {
        List<Lab> labs = labService.getExportAll();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义别名
        writer.addHeaderAlias("name", "名称");
        writer.addHeaderAlias("type", "类型");
        writer.addHeaderAlias("code", "代码");
        writer.addHeaderAlias("school", "学校");
        writer.addHeaderAlias("college", "学院");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("editTime", "修改时间");
        writer.addHeaderAlias("creator", "负责人（导入需要填写负责人账号!!）");
        writer.addHeaderAlias("memberNames", "成员（导入需要填写成员账号(逗号分隔)）");
        writer.setOnlyAlias(true);
        writer.write(labs, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("实验室信息", "UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+ fileName +".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入用户账号信息
     * @return 结果
     */
    @AuthAccess
    @PostMapping("/import")
    public Result importLabs(MultipartFile file) throws Exception
    {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("名称","name");
        reader.addHeaderAlias("类型", "type");
        reader.addHeaderAlias("代码", "code");
        reader.addHeaderAlias("学校", "school");
        reader.addHeaderAlias("学院", "college");
        reader.addHeaderAlias("负责人（导入需要填写负责人账号!!）", "creator");
        reader.addHeaderAlias("成员（导入需要填写成员账号(逗号分隔)）", "memberNamesStr");
        reader.setIgnoreEmptyRow(true);
        List<Lab> labs = reader.readAll(Lab.class);
        return labService.importLabInfo(labs);
    }

}

