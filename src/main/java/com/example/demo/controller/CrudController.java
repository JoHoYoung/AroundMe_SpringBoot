package com.example.demo.controller;

import com.example.demo.Repository.ImageRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.model.image_schema;
import com.example.demo.model.post_schema;
import org.apache.commons.io.FilenameUtils;
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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@EnableAutoConfiguration
public class CrudController {

    @Autowired
    PostRepository PostRepository;

    @Autowired
    ImageRepository ImageRepository;
    @RequestMapping(value = "/post/create", method = RequestMethod.POST)
    public ModelAndView CreatePost(Model model, HttpServletRequest req, HttpSession session, @RequestParam("userimage") List<MultipartFile> files) throws IOException {
        try {
            String paramtitle=req.getParameter("title");
            String paramcontent=req.getParameter("content");
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            String createdat = dayTime.format(new Date(time));
            String writer=session.getAttribute("user").toString();

            post_schema parents = PostRepository.save(new post_schema(paramtitle,paramcontent,writer,0,createdat,createdat,0,0,0,0));

            for(int i=0;i<files.size();i++){
                String sourceFileName = writer;
                File destinationFile;
                String destinationFileName;
                String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";


                do {
                    destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileName;
                    destinationFile = new File(fileUrl + destinationFileName);
                    image_schema image=ImageRepository.save(new image_schema(destinationFileName,parents.getId(),parents));
                    parents.addImage(image);
                } while (destinationFile.exists());

                destinationFile.getParentFile().mkdirs();
                files.get(i).transferTo(destinationFile);

            }

            return new ModelAndView(new RedirectView("/post/"+parents.getId(),true));
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView(new RedirectView("/main",true));
        }

    }

    @RequestMapping(value="/posts/:page",method = RequestMethod.GET)
    public String ReadPosts(Model model,@PathVariable(name="page") String page)
    {

        Page<post_schema> posts=PostRepository.findAll(new PageRequest(0,10,Sort.by(Sort.Direction.DESC,"create_at")));
        Map<String, Object> data=new HashMap();
        data.put("data",posts);
        model.addAttribute("data",data);
        return "post";
    }

    @RequestMapping(value="/post/:postroot",method = RequestMethod.GET)
    public String ReadPost(Model model,@PathVariable(name="postroot") String postroot)
    {

        post_schema post=PostRepository.findByid(postroot);
        Map<String, Object> data=new HashMap();
        data.put("data",post);
        model.addAttribute("data",data);
        return "post";
    }

    @RequestMapping(value="/post/delete/:postroot",method = RequestMethod.POST)
    public String DeletePost(Model model,@PathVariable(name="postroot") String postroot)
    {

        post_schema post=PostRepository.findByid(postroot);
        Map<String, Object> data=new HashMap();
        data.put("data",post);
        model.addAttribute("data",data);
        return "posts";
    }
}
