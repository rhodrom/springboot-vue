package com.hsy.springboot.mapper;

import com.hsy.springboot.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hsy
 * @since 2023-04-09
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> findCommentDetail(@Param("articleId") Integer articleId);
}
