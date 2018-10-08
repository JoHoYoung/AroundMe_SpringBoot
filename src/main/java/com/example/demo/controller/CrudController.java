package com.example.demo.controller;

import com.example.demo.Repository.CommentRepository;
import com.example.demo.Repository.ImageRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Repository.RecommenderRepository;
import com.example.demo.model.comment_schema;
import com.example.demo.model.image_schema;
import com.example.demo.model.post_schema;
import com.example.demo.model.recommender_schema;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


@Controller
@EnableAutoConfiguration
@SuppressWarnings("Duplicates")
public class CrudController {

    @Autowired
    PostRepository PostRepository;

    @Autowired
    ImageRepository ImageRepository;

    @Autowired
    CommentRepository CommentRepository;

    @Autowired
    RecommenderRepository RecommenderRepository;

    public String Gettime()
    {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return dayTime.format(new Date(time));

    }
    ///////////////////---------CRUD-------------////////////////////////////////
    @RequestMapping(value = "/post/create", method = RequestMethod.POST)
    public String CreatePost(Model model, HttpServletRequest req, HttpSession session, @RequestParam("userimage") List<MultipartFile> files) throws IOException {
        try {
            String paramtitle=req.getParameter("title");
            String paramcontent=req.getParameter("content");

            String createdat = Gettime();
            String writer="hoyoung";

            post_schema parents = PostRepository.save(new post_schema(paramtitle,paramcontent,writer,0,createdat,createdat,0,0,0,0));

            System.out.println(parents.getId());
            for(int i=0;i<files.size();i++){
                String sourceFileName = writer;
                File destinationFile;
                String destinationFilename;
                String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";

                 do {
                    destinationFilename = RandomStringUtils.randomAlphanumeric(32) + sourceFileName;
                    destinationFile = new File(fileUrl + destinationFilename);
                    image_schema image=ImageRepository.save(new image_schema(destinationFilename));
                    parents.addImage(image);
                } while (destinationFile.exists());

                destinationFile.getParentFile().mkdirs();
                files.get(i).transferTo(destinationFile);
            }

            PostRepository.save(parents);
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
            return "login";
        }

    }

    @RequestMapping(value="/create", method=RequestMethod.GET)
    public String create()
    {
        return "create";
    }

    @RequestMapping(value="/posts/{page}",method = RequestMethod.GET)
    public String ReadPosts(Model model,@PathVariable(name="page") int page)
    {
        PageRequest PageRequest=new PageRequest(page-1,10,Sort.by(Sort.Direction.DESC,"id"));
        Page<post_schema> posts=PostRepository.findAll(PageRequest);
        Map<String, Object> data=new HashMap();
        data.put("data",posts);
        model.addAttribute("data",data);
        return "posts";
    }

    @RequestMapping(value="/post/{postroot}",method = RequestMethod.GET)
    public String ReadPost(Model model,HttpSession session,@PathVariable(name="postroot") int postroot)
    {

        post_schema post=PostRepository.findByid(postroot);
        post.setViews(post.getViews()+1);
        PostRepository.save(post);
        Map<String, Object> data=new HashMap();

        if(session.getAttribute("user").toString()==null)
        {
            model.addAttribute("islogin",0);
        }
        else {
            model.addAttribute("islogin",1);
            recommender_schema recommender = RecommenderRepository.findByBelongtoAndUser(postroot,session.getAttribute("user").toString());

            if(recommender==null)
            {
                model.addAttribute("canrecommend",1);
            }
            else {
                model.addAttribute("canrecommend", -1);
            }
        }

        data.put("data",post);
        model.addAttribute("data",data);
        return "post";

    }

    @RequestMapping(value="/post/update/:postroot", method=RequestMethod.POST)
    public ModelAndView UpdatePost(Model model,HttpSession session ,@PathVariable(name="postroot")int postroot, @RequestParam("userimage") List<MultipartFile> files, HttpServletRequest req) throws IOException
    {

        String paramtitle=req.getParameter("title");
        String paramcontent=req.getParameter("content");
        String[] data = req.getParameterValues("todelete");


        post_schema post = PostRepository.findByid(postroot);
        List<image_schema> images=post.getImage(); //글에 속한 사진을 가져옴.

        List<Integer> todelete=new ArrayList(Arrays.asList(data));
        Dscending ascending = new Dscending();
        Collections.sort(todelete,ascending);
        for(int i=0;i<todelete.size();i++)
        {
            //서버에 저장된 이미지파일 삭제하는 부분

            String path = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/"; // 삭제할 파일의 경로
            String filepath=path+images.get(todelete.get(i)).getImagename();

            File file = new File(filepath);
            if(file.exists()){
                    file.delete();
            }
             // DB에 저장된 이미지 삭제하는 부분.
            ImageRepository.deleteByImagename(images.get(todelete.get(i)).getImagename());
            images.remove(todelete.get(i));
        }

        //Update하며 새로 업로드한 사진 저장하는 부분.
        for(int i=0;i<files.size();i++){
            String sourceFileName = session.getAttribute("user").toString();
            File destinationFile;
            String destinationFileName;
            String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";


            do {
                destinationFileName = RandomStringUtils.randomAlphanumeric(32) + sourceFileName;
                destinationFile = new File(fileUrl + destinationFileName);
                image_schema image=ImageRepository.save(new image_schema(destinationFileName));
                post.addImage(image);
            } while (destinationFile.exists());

            destinationFile.getParentFile().mkdirs();
            files.get(i).transferTo(destinationFile);
        }


        post.setTitle(paramtitle);
        post.setContent(paramcontent);
        post.setUpdateat(Gettime());
        PostRepository.save(post);

        return new ModelAndView(new RedirectView("/post/"+post.getId(),true));
    }

    @RequestMapping(value="/post/delete/:postroot",method = RequestMethod.POST)
    public String DeletePost(Model model,@PathVariable(name="postroot")int postroot)
    {

        //db에서 사진테이블의 외래킬을 delete on cascade로 설정해 놓아서... 글만 지워도 사진 db는 지워질 것 같다.
        post_schema post = PostRepository.findByid(postroot);
        List<image_schema> images=post.getImage();

        //서버에 저장된 이미지 파일들 삭제. 서버에 저장된건 이미지 파일밖에없다. 추천인, 댓글은 cascade설정때문에 post를 지우면 관련속성은 모두 지워진다.
        for(int i=0;i<images.size();i++)
        {
            String path = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";
            String filepath=path+images.get(i).getImagename();

            File file = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            ImageRepository.delete(images.get(i));
        }
        PostRepository.delete(post);

        return "posts";
    }

    //////////////////////////////////////////////////////////////////////////

    /////////////////////----댓글 처리 -----------/////////////////////////////////////////////

    @RequestMapping(value="/post/{postroot}/addcomment", method=RequestMethod.POST)
    public ModelAndView AddComment(Model model,HttpSession session,@PathVariable(name="postroot")int postroot,HttpServletRequest req)
    {
        String paramcontent=req.getParameter("content");
        post_schema post=PostRepository.findByid(postroot);
        comment_schema comment = CommentRepository.save(new comment_schema(paramcontent,session.getAttribute("user").toString(),Gettime()));
        post.addComment(comment);
        PostRepository.save(post);

        return new ModelAndView(new RedirectView("/post/"+Integer.toString(postroot),true));
    }

    @RequestMapping(value = "/post/{postroot}/deletecomment/{commentroot}",method = RequestMethod.POST)
    public ModelAndView DeleteComment(Model model,HttpSession session,@PathVariable(name="postroot")int postroot,@PathVariable(name="commentroot")int commentroot)
    {

        post_schema post=PostRepository.findByid(postroot);
        List<comment_schema> comments=post.getComment();

        for(int i=0;i<comments.size();i++)
        {
            if(comments.get(i).getId()==commentroot)
            {
                CommentRepository.delete(comments.get(i));
                comments.remove(i);
            }
        }
        PostRepository.save(post);

        return new ModelAndView(new RedirectView("/post/"+Integer.toString(postroot)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////------------추천처리--------------------/////////////////////////////////////
    @RequestMapping(value="/post/recommend/{postroot}", method=RequestMethod.GET)
    public ModelAndView Recommend(Model model,HttpSession session,@PathVariable(name="postroot")int postroot)
    {
        recommender_schema recommender=RecommenderRepository.findByBelongtoAndUser(postroot,session.getAttribute("user").toString());
        post_schema post=PostRepository.findByid(postroot);
        if(recommender==null) //사용자가 이미 추천한 글이 아닐경우
        {
            recommender_schema newrecommender = RecommenderRepository.save(new recommender_schema(session.getAttribute("user").toString()));
            post.addRecommender(newrecommender);
            post.setStar(post.getStar()+1);
            PostRepository.save(post);
        }
        else  //사용자가 이미 추천한 글일경우
        {
            for(int i=0;i<post.getRecommender().size();i++)
            {
                if(post.getRecommender().get(i).getUser()==session.getAttribute("user").toString())
                {
                    RecommenderRepository.delete(recommender); //추천인 튜플 삭제
                    post.setStar(post.getStar()-1); // 추천수 1감소
                    post.getRecommender().remove(i); // 해당 Collection 삭제.
                    break;

                }
            }
        }
        return new ModelAndView(new RedirectView("/post/"+Integer.toString(postroot)));
    }

    class Dscending implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    }


}
