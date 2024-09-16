package dev.hemraj.jwtauthentication.RequestDto;


import dev.hemraj.jwtauthentication.Model.Blog.Comments;
import lombok.Data;

import java.util.List;

@Data
public class CreateBlogDto {
    private String title;
    private String content;
    private List<Comments> comments;
    private String createdAt;
}
