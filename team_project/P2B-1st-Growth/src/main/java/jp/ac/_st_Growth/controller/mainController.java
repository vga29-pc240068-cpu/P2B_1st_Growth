package jp.ac._st_Growth.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import jp.ac._st_Growth.repository.RecruitmentRepository;


@Controller
public class mainController {

    @Autowired
    RecruitmentRepository recruitmentsRepository;

    // 
    @GetMapping("common/top")
    public String showMain(Model model) {
        
        model.addAttribute("recruitments", recruitmentsRepository.findAll());
        return "common/top"; 
    }
}