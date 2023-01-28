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
import platform.domain.User;
import platform.service.MessageService;
import platform.service.UserService;

import java.util.Date;
import java.util.List;

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

        Message message = new Message();
        model.addAttribute("posts", messages);
        model.addAttribute("message", message);
        model.addAttribute("currentPage", messages.getNumber() + 1);
        model.addAttribute("totalItems", messages.getTotalElements());
        model.addAttribute("totalPages", messages.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("service", messageService);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/post")
        public String viewPost(){
        return "redirect:/posts";
    }

    @GetMapping("/post/{id}")
        public String viewPost(Model model,  @PathVariable int id) {
        Message message = messageService.getById(id);
        if (message == null) {
            return "redirect:/posts";
        }
        List<Message> comments = messageService.getAllByParent(id);
        int mainComments = (int) messageService.countChildByParent(id);

        model.addAttribute("message", message);
        model.addAttribute("comments", comments);
        model.addAttribute("mainComments", mainComments);
        model.addAttribute("service", messageService);

        return "post";
    }

    @PostMapping("/post/add")
    public String addPost(int authorId, String title, String text) {
        User author = userService.findById(authorId);
        if (title != null && text != null && author != null) {
            Message newMessage = new Message(text, title, new Date(), author, null);
            messageService.add(newMessage);
        }

        return "redirect:/posts";
    }

    @GetMapping("/post/reply/{id}")
    public String post(Model model, @PathVariable Integer id) {
        Message message = new Message();
        model.addAttribute("message", message);
        model.addAttribute("title", "Reply");
        return "add";
    }

    @PostMapping("/post/reply")
    public String reply(int postId, String text, String title, int authorId, int parentId) {
        Message parent = messageService.getById(parentId);
        Message message = new Message(text, title, new Date(), userService.findById(authorId), parent);
        messageService.add(message);

        return "redirect:/post/" + postId + "#com" + parentId;
    }

    @DeleteMapping("/post/delete/{id}")
    public String deleteMessage(int id, int postId) {
        messageService.deleteAllByParent(id);
        Message parent = messageService.getById(id).getParent();
        messageService.remove(id);

        if (parent != null) {
            return "redirect:/post/" + postId + "#com" + parent.getId();
        } else {
            return "redirect:/posts";
        }
    }
}
