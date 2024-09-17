package dev.hemraj.jwtauthentication.ResponseDto;

import dev.hemraj.jwtauthentication.Model.Blog.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchAllBlogsResponse {
    private String authorId;
    private String title;
    private String content;
    private List<Comments> commentsList;
    private String createdAt;
}
