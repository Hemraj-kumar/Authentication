package dev.hemraj.jwtauthentication.RequestDto;


import lombok.Data;

@Data
public class CreateBlogDto {
    private String author_id;
    private String title;
    private String content;
    private String[] commments;

}
