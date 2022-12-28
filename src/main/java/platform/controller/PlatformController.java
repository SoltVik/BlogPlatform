package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.service.MessageService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PlatformController {

    @Autowired
    MessageService messageService;

    @RequestMapping("/")
    public String welcome(Model model) {
        model.addAttribute("posts", messageService.getAllByParent(0));
        model.addAttribute("service", messageService);
        return "index";
    }
}
