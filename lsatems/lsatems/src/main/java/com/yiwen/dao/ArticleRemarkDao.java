package com.yiwen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiwen.domain.ArticleRemark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 论坛文章评论表 Mapper 接口
 * </p>
 *
 * @author yiwen
 * @since 2023-03-06
 */
public interface ArticleRemarkDao extends BaseMapper<ArticleRemark> {

    @Select("select c.*,u.name,u.avatar_path from tbl_article_remark c " +
            "left join tbl_user_detail u on c.user_id = u.user_id where c.article_id = #{articleId} order by c.create_time desc")
    List<ArticleRemark> findRemarkDetails(@Param("articleId") String articleId);
}
