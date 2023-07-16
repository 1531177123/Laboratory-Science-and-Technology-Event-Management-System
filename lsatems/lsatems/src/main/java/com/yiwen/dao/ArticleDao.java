package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.domain.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 论坛文章信息表 Mapper 接口
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
public interface ArticleDao extends BaseMapper<Article> {

}
