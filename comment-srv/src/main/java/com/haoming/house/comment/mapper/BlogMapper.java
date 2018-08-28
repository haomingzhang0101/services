package com.haoming.house.comment.mapper;

import java.util.List;

import com.haoming.house.comment.model.Blog;
import com.haoming.house.comment.model.LimitOffset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface BlogMapper {
  
  public List<Blog> selectBlog(@Param("blog") Blog blog, @Param("pageParams") LimitOffset limitOffset);
  
  public Long selectBlogCount(Blog query);

}
