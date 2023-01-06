package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import platform.domain.Message;
import platform.service.MessageService;


@Controller
public class PlatformController {

    @Autowired
    MessageService messageService;


    @RequestMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Message> messages = messageService.findAll(pageable);

        model.addAttribute("posts", messages);
        model.addAttribute("currentPage", messages.getNumber() + 1);
        model.addAttribute("totalItems", messages.getTotalElements());
        model.addAttribute("totalPages", messages.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("service", messageService);

        return "index";
    }

    @RequestMapping("/posts")
    public String posts(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Message> messages = messageService.findAll(pageable);

        model.addAttribute("posts", messages);
        model.addAttribute("currentPage", messages.getNumber() + 1);
        model.addAttribute("totalItems", messages.getTotalElements());
        model.addAttribute("totalPages", messages.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("service", messageService);

        return "index";
    }

}
