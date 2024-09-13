package dev.hemraj.jwtauthentication.Service.BlogService;

import dev.hemraj.jwtauthentication.Model.Blog.Post;
import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Service
public class PostBlogService {

    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<?> createBlog(CreateBlogDto blogPostData,String authorId){
        try {
            Post newBlog = new Post();
//            String decodedAuthorId = Arrays.toString(Base64.getDecoder().decode(blogPostData.getAuthor_id()));

//            newBlog.setAuthor_id(decodedAuthorId);
            newBlog.setTitle(blogPostData.getTitle());
            newBlog.setContent(blogPostData.getContent());
            if (blogPostData.getCommments().length == 0) {
                blogPostData.setCommments(new String[]{});
            }
            mongoTemplate.insert(blogPostData);
            return ResponseEntity.status(HttpStatus.OK).body("Data is been successfully saved!");
        }catch (Exception err){
            log.error("error in saving data to db");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error in saving data!");
    }
}
