package com.hsy.springboot.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsy.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hsy.springboot.common.Result;

import com.hsy.springboot.service.ICommentService;
import com.hsy.springboot.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hsy
 * @since 2023-04-09
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Comment comment) {
        if(comment.getId()==null){  //新增
            comment.setUserId(TokenUtils.getCurrentUser().getId());
            comment.setTime(DateUtil.now());

            if(comment.getPid()!=null){// 判断如果是回复，进行处理
                Integer pid=comment.getPid();
                Comment pComment=commentService.getById(pid);
                if(pComment.getOriginId()!=null){// 如果当前回复的父级有祖宗，那么就设置相同的祖宗
                    comment.setOriginId(pComment.getOriginId());
                }else{// 否则就设置父级为当前回复的祖宗
                    comment.setOriginId(comment.getPid());
                }
            }
        }
        commentService.saveOrUpdate(comment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        commentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/tree/{articleId}")
    public Result findCommentDetail(@PathVariable Integer articleId){
        //查询所有的评论和回复数据
        List<Comment> articleComments = commentService.findCommentDetail(articleId);
        //查询评论数据（不包括回复）
        List<Comment> originList = articleComments.stream().filter(comment -> comment.getOriginId() == null).collect(Collectors.toList());

        //设置评论数据的子节点，也就是回复内容
        for (Comment origin : originList) {
            List<Comment> comments = articleComments.stream().filter(comment -> origin.getId().equals(comment.getOriginId())).collect(Collectors.toList());
            comments.forEach(comment -> {
                articleComments.stream().filter(c->c.getId().equals(comment.getPid())).findFirst().ifPresent((v->{  //找到父级评论用户id和用户昵称，并设置给当前的回复对象
                    comment.setPUserId(v.getUserId());
                    comment.setPNickname(v.getNickname());
                }));
            });
            origin.setChildren(comments);
        }
        return Result.success(originList);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}

