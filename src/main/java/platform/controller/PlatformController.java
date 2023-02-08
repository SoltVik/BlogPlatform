package platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.domain.Message;
import platform.domain.Role;
import platform.domain.User;
import platform.service.MessageService;
import platform.service.RoleService;
import platform.service.UserService;

import java.util.*;

@Controller
public class PlatformController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/"})
    public String index(Model model) {
        List<Message> messages = messageService.getLastPosts(6);
        List<Message> comments = messageService.getLastComments(6);
        List<User> topAuthors = userService.getTopAuthors(3);
        List<Message> randomMessageToTopAuthors = messageService.getRandomPostToUsers(topAuthors);

        model.addAttribute("posts", messages);
        model.addAttribute("comments", comments);
        model.addAttribute("service", messageService);
        model.addAttribute("topAuthors", topAuthors);
        model.addAttribute("randomMessageToTopAuthors", randomMessageToTopAuthors);
        return "index";
    }

    @GetMapping({"/posts"})
    public String getPosts(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size) {
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

        return "posts";
    }

    @GetMapping("/login")
    public String login(@CurrentSecurityContext SecurityContext context) {
        if (context.getAuthentication().getName().equalsIgnoreCase("anonymousUser")) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/post")
    public String viewPost() {
        return "redirect:/posts";
    }

    @GetMapping("/post/{id}")
    public String viewPost(Model model, @PathVariable int id) {
        Message message = messageService.getById(id);
        if (message == null || message.getParentId() != null) {
            return "redirect:/posts";
        }
        List<Message> comments = messageService.getAllByParent(id);
        int mainComments = (int) messageService.countChildByParent(id);

        model.addAttribute("message", message);
        model.addAttribute("comments", comments);
        model.addAttribute("mainComments", mainComments);
        model.addAttribute("messageService", messageService);
        model.addAttribute("userService", userService);

        return "post";
    }

    @PostMapping("/post/add")
    public String addPost(String username, String title, String text) {
        User author = userService.findByUsername(username);
        System.out.println(author);
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
    public String reply(int postId, String text, String title, String authorName, int parentId) {
        Message parent = messageService.getById(parentId);
        User author = userService.findByUsername(authorName);

        if (author != null) {
            Message message = new Message(text, title, new Date(), author, parent);
            messageService.add(message);
        }

        return "redirect:/post/" + postId + "#com" + parentId;
    }

    @PostMapping("/post/edit")
    public String edit(int postId, int msgId, String editText, String editTitle, String editorName) {
        Message message = messageService.getById(msgId);
        User editor = userService.findByUsername(editorName);

        if (editor != null) {
            message.setTitle(editTitle);
            message.setText(editText);
            message.setDateEdit(new Date());
            message.setEditor(editor);
            messageService.save(message);
        }

        return "redirect:/post/" + postId + "#com" + msgId;

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

    @GetMapping("/reg")
    public String regPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "reg";
    }

    @PostMapping("/reg")
    public String createUser(Model model, @ModelAttribute("user") User user, BindingResult result, String rePassword) {
        if (!user.getPassword().equals(rePassword)) {
            result.rejectValue("pswd", "1", "Password mismatch");
        }
        if (user.getPassword().isEmpty()) {
            result.rejectValue("pswd_empty", "2", "Password is empty");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "3", "There is already an account registered with the same username");
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            result.rejectValue("username", "4", "There is already an account registered with the same email");
        }
        if (!result.hasErrors()) {
            String username = user.getUsername();
            String password = passwordEncoder.encode(user.getPassword());
            String email = user.getEmail();
            Role role = roleService.findById(Role.ROLE_USER);

            User newUser = new User(username, password, email, role, 1);
            userService.add(newUser);
            return "redirect:/login";
        }
        return "reg";
    }

    @GetMapping("/users")
    public String userPage(Model model) {
        List<List<User>> userList = userService.getUserLists(hasRole("ROLE_ADMIN"));
        List<Role> roles = roleService.getAll();
        model.addAttribute("userList", userList);
        model.addAttribute("userService", userService);
        model.addAttribute("roles", roles);
        return "users";
    }


    @PostMapping("/users")
    public String editUser(Optional<String> editName, String editEmail, String editPassword, Integer editRole, boolean editEnabled, int userId, boolean isAdmin) {
        if (!editEmail.isEmpty()) {
            User oldUser = userService.findById(userId);
            if (oldUser == null) {
                return "redirect:/users";
            }

            User user;
            if (isAdmin && editName.isPresent()) {
                user = userService.findByUsername(editName.get());
                if (user != null) {;
                    if (user.getId() != oldUser.getId()) {
                        return "redirect:/users";
                    }
                }
                oldUser.setUsername(editName.get());
            }
            if (userService.isValidEmail(editEmail)) {
                user = userService.findByEmail(editEmail);
                if (user != null) {;
                    if (user.getId() != oldUser.getId()) {
                        return "redirect:/users";
                    }
                }
                oldUser.setEmail(editEmail);
            }

            if (!editPassword.isEmpty()) {
                oldUser.setPassword(passwordEncoder.encode(editPassword));
            }
            if (isAdmin) {
                oldUser.setRole(roleService.findById(editRole));
                oldUser.setEnabled(editEnabled ? 1 : 0);
            }

            userService.save(oldUser);
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{letter}")
    public String userPageLetter(Model model, @PathVariable String letter) {
        List<List<User>> userList = userService.getUserListsByLetter(letter, hasRole("ROLE_ADMIN"));
        List<Role> roles = roleService.getAll();
        model.addAttribute("userList", userList);
        model.addAttribute("userService", userService);
        model.addAttribute("roles", roles);
       return "users";
    }

    private boolean hasRole(String role) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(role);
            if (hasRole) {
                break;
            }
        }
        return hasRole;
    }
}