package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.common.Result;
import com.yiwen.controller.dto.LabShowDTO;
import com.yiwen.domain.Lab;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 实验室详细信息表 服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-21
 */
public interface ILabService extends IService<Lab> {

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param pageSize 每页行数
     * @param name 名字
     * @param type  类型
     * @return 结果
     */
    IPage<Lab> selectPage(long currentPage, long pageSize, String name, String type, String code, String school, String college);

    /**
     * 保存实验室信息
     * @param lab 实验室信息
     * @return 结果
     */
    boolean saveLab(Lab lab);

    /**
     * 更新实验室信息
     * @param lab 实验室信息
     * @return 结果
     */
    boolean updateLabById(Lab lab);

    /**
     * 分页查询个人实验室信息
     * @param currentPage 当前页
     * @param pageSize 每页记录数
     * @param userId 用户id
     * @return 结果
     */
    IPage<Lab> selectPageByUserId(long currentPage, long pageSize, String userId);

    /**
     * 查询所有实验室记录
     * @param userId 用户id
     * @return 结果
     */
    List<Lab> getAll(String userId, String name, String priority);

    /**
     * 通过id查询实验室
     * @param id id
     * @return 结果
     */
    Lab getLabById(String id);

    /**
     * 导入实验室信息
     * @param labs 实验室信息
     * @return 结果
     */
    Result importLabInfo(List<Lab> labs);

    /**
     * 获取导出的对象
     * @return 结果
     */
    List<Lab> getExportAll();
}
