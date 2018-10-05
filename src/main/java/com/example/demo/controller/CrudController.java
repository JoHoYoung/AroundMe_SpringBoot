package com.example.demo.controller;

import com.example.demo.Repository.ImageRepository;
import com.example.demo.Repository.PostRepository;
import com.example.demo.model.image_schema;
import com.example.demo.model.post_schema;
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
public class CrudController {

    @Autowired
    PostRepository PostRepository;

    @Autowired
    ImageRepository ImageRepository;
    @RequestMapping(value = "/post/create", method = RequestMethod.POST)
    public String CreatePost(Model model, HttpServletRequest req, HttpSession session, @RequestParam("userimage") List<MultipartFile> files) throws IOException {
        try {
            String paramtitle=req.getParameter("title");
            String paramcontent=req.getParameter("content");
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            String createdat = dayTime.format(new Date(time));
            String writer="hoyoung";

            post_schema parents = PostRepository.save(new post_schema(paramtitle,paramcontent,writer,0,"","",0,0,0,0));

            System.out.println(parents.getId());
            for(int i=0;i<files.size();i++){
                String sourceFileName = writer;
                File destinationFile;
                String destinationFileName;
                String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";


                do {
                    destinationFileName = RandomStringUtils.randomAlphanumeric(32) + sourceFileName;
                    destinationFile = new File(fileUrl + destinationFileName);


                    image_schema image=ImageRepository.save(new image_schema(destinationFileName,parents.getId()));
                    parents.addImage(image);
                } while (destinationFile.exists());

                destinationFile.getParentFile().mkdirs();
                files.get(i).transferTo(destinationFile);

            }
            PostRepository.save(parents);
            List<image_schema> list = parents.getImage();

            for( image_schema m : list ){
                System.out.println(m.getImagename());
            }
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

    @RequestMapping(value="/posts/:page",method = RequestMethod.GET)
    public String ReadPosts(Model model,@PathVariable(name="page") String page)
    {

        Page<post_schema> posts=PostRepository.findAll(new PageRequest(0,10,Sort.by(Sort.Direction.DESC,"create_at")));
        Map<String, Object> data=new HashMap();
        data.put("data",posts);
        model.addAttribute("data",data);
        return "post";
    }

    @RequestMapping(value="/post/{postroot}",method = RequestMethod.GET)
    public String ReadPost(Model model,@PathVariable(name="postroot") int postroot)
    {

        System.out.println(postroot);
        post_schema post=PostRepository.findByid(postroot);
        Map<String, Object> data=new HashMap();
        System.out.println(post.getImage().get(0).getImagename());
        data.put("data",post.getId());
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
                image_schema image=ImageRepository.save(new image_schema(destinationFileName,post.getId()));
                post.addImage(image);
            } while (destinationFile.exists());

            destinationFile.getParentFile().mkdirs();
            files.get(i).transferTo(destinationFile);
        }

        post.setTitle(paramtitle);
        post.setContent(paramcontent);
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
        }
        PostRepository.delete(post);

        return "posts";
    }

    class Dscending implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    }


}
