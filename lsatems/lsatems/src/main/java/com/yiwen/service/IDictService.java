package com.yiwen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yiwen.domain.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
public interface IDictService extends IService<Dict> {

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param pageSize 每页行数
     * @param name 名字
     * @param type  类型
     * @return 结果
     */
    IPage<Dict> selectPage(long currentPage, long pageSize, String name, String type);

    /**
     * 根据类型拿字典值集合
     * @param type 类型
     * @return 结果
     */
    List<Dict> getDictByType(String type);
}
