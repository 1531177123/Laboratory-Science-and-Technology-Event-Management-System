package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiwen.dao.DictDao;
import com.yiwen.domain.Dict;
import com.yiwen.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yiwen
 * @since 2023-03-15
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictDao, Dict> implements IDictService
{

    @Autowired
    private DictDao dictDao;

    @Override
    public IPage<Dict> selectPage(long currentPage, long pageSize, String name, String type)
    {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(name), Dict::getName, name);
        queryWrapper.eq(StrUtil.isNotBlank(type), Dict::getType, type);
        return dictDao.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
    }

    @Override
    public List<Dict> getDictByType(String type)
    {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(type), Dict::getType, type);
        return list(queryWrapper);
    }
}
