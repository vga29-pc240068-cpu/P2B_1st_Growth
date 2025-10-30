package jp.ac._st_Growth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ac._st_Growth.repository.ApplicationsRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    ApplicationsRepository applicationRepository;

    @Autowired
     RecruitmentsRepository recruitmentRepository;

    @Autowired
     UsersRepository userRepository;
    
    @RequestMapping(path ="/{recruitmentId}",method = RequestMethod.POST)
    public String applyForRecruitment()  {
        
                return "redirect:/users/login";
            }
}
