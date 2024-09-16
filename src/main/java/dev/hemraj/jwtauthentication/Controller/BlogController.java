package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.Blog.Comments;
import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import dev.hemraj.jwtauthentication.Service.BlogService.PostBlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;


@RequestMapping("/api/blog")
@RestController
@Slf4j
public class BlogController {
    private final PostBlogService postBlogService;
    public BlogController(PostBlogService postBlogService){
        this.postBlogService = postBlogService;
    }
    @PostMapping("/addBlog")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createBlogController(@RequestHeader(name = "author-id") String author_id,@RequestBody CreateBlogDto data){
        try{
            return postBlogService.createBlog(data,author_id);
        }catch (Exception err){
            log.error("Error in creating the blog");
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Check the correct format of data");
    }

    @PostMapping("/addComments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addCommentController(@RequestBody Comments comments,@RequestParam("blogID") String blogID){
        try{
            return postBlogService.addComments(comments, blogID);
        }catch (Exception err){
            log.error("Error in adding comments to the post", err);
        }
        return null;
    }

    @DeleteMapping("/deleteBlog")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteBlog(@RequestParam("blogID") String id){
        try{
            return postBlogService.deleteBlog(id);
        }catch (Exception err){
            log.error("Error in deleting the data");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting the data");
    }

    @GetMapping("/fetchAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> fetchAllBlogs(){
        try{
            return postBlogService.getBlogs();
        }catch (Exception err){
            log.error("Error in fetching the data",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in fetching all the blogs");
    }
}
