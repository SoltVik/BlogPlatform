package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import platform.domain.Message;
import platform.service.MessageService;
import platform.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class PlatformController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @GetMapping({"/", "/posts"})
    public String index(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int size) {

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

    @GetMapping({"/post/{action}", "/post/{action}/{id}"})
    public String post(Model model, @PathVariable String action, @PathVariable Optional<Integer> id) {
        Message message = new Message();
        if (action.matches("\\d+")) {
            int idx = Integer.parseInt(action);
            if (idx > 0) {
                message = messageService.getById(idx);
                List<Message> comments = messageService.getAllByParent(idx);
                int mainComments = (int) messageService.countChildByParent(idx);

                model.addAttribute("message", message);
                model.addAttribute("comments", comments);
                model.addAttribute("mainComments", mainComments);
                model.addAttribute("service", messageService);

                return "post";
            }
        } else if (action.equalsIgnoreCase("add") && !id.isPresent()) {
            model.addAttribute("title", "add");
            model.addAttribute("message", message);
            model.addAttribute("title", "Add");
            return "add";
        } else if (action.equalsIgnoreCase("reply") && id.isPresent()) {
            model.addAttribute("message", message);
            model.addAttribute("title", "Reply");
            return "add";
        }
        return "redirect:/posts";
    }

    @PostMapping("/post/reply")
    public String reply(int postId, String text, String title, int authorId, int parentId) {

        Message parent = messageService.getById(parentId);
        Message message = new Message(text, title, new Date(), userService.findById(authorId), parent);
        messageService.add(message);

        return "redirect:/post/" + postId + "#com" + parentId;
    }
}