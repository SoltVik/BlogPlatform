package platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.controller.vo.UserVO;
import platform.domain.Message;
import platform.domain.Role;
import platform.domain.User;
import platform.repository.MessageRepository;
import platform.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;


@Service
public class UserService {
    static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public List<User> findAllEnabled() {
        return userRepository.findAllEnabled();
    }

    public void add(User user) {
        logger.info("Added user {}", user);
        userRepository.save(user);
    }

    public void save(User user) {
        logger.info("Updated user {}", user);
        userRepository.save(user);
    }

    public User findById(int idx) {
        User result = userRepository.findById(idx).orElse(null);
        return result;
    }

    public User findEnabledById(int idx) {
        User result = userRepository.findByIdAndIsEnabled(idx);
        return result;
    }

    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        return result;
    }

    public User findByEmail(String email) {
        User result = userRepository.findByEmail(email);
        return result;
    }

    public List<User> getTopAuthors(int num) {
        List<User> users = userRepository.getTopAuthors(num);
        return users;
    }

    public boolean isPermit(int postId, int msgId, String user) {
        User author = findByUsername(user);
        int roleId = author.getRole().getId();

        if ((roleId == Role.ROLE_ADMIN) || (roleId == Role.ROLE_STAFF)) {
            return true;
        }

        Message post = messageRepository.findByIdAndDateDeleteIsNull(postId).orElse(null);
        Message msg =  messageRepository.findByIdAndDateDeleteIsNull(msgId).orElse(null);

        return ((post.getAuthor() == author) || (msg.getAuthor() == author));
    }

    public List<List<User>> getUserLists(boolean isAdmin) {
        List<User> users = (isAdmin) ? userRepository.findAll() : userRepository.findAllEnabled();
        List<List<User>> userList = new ArrayList<>();
        Set<String> letters = new TreeSet<>();
        for (User user : users) {
            String let = "" + user.getUsername().toLowerCase().charAt(0);
            if (let.matches("[0-9]")) {
                let = "#";
            }
            letters.add(let);
        }
        for (String letter : letters) {
            List<User> usersByLetter;
            if (letter.equals("#")) {
                usersByLetter = (isAdmin) ? userRepository.findAllBySpecSymbol() : userRepository.findAllEnabledBySpecSymbol();
            } else {
                usersByLetter = (isAdmin) ? userRepository.findAllByLetter(letter) : userRepository.findAllEnabledByLetter(letter);
            }
            userList.add(usersByLetter);
        }
        return userList;
    }

    public List<List<User>> getUserListsByLetter(String letter, boolean isAdmin) {
        letter = letter.toLowerCase();
        List<List<User>> userList = new ArrayList<>();
        List<User> usersByLetter;
        if (letter.equalsIgnoreCase("num")) {
            usersByLetter = (isAdmin) ? userRepository.findAllBySpecSymbol() : userRepository.findAllEnabledBySpecSymbol();
        } else {
            usersByLetter = (isAdmin) ? userRepository.findAllByLetter(letter) : userRepository.findAllEnabledByLetter(letter);
        }

        if (!usersByLetter.isEmpty()) {
            userList.add(usersByLetter);
        }

        return userList;
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public User update(int idx, UserVO userVO) {
        User old = userRepository.findById(idx).orElse(null);
        if (old != null) {
            old.setUsername(userVO.getUsername());
            old.setPassword(userVO.getPassword());
            old.setEmail(userVO.getEmail());
            return userRepository.save(old);
        }
        return null;
    }

    public User update(int idx, UserVO userVO, boolean changePassword) {
        User old = userRepository.findById(idx).orElse(null);
        if (old != null) {
            old.setUsername(userVO.getUsername());
            if (changePassword) {
                old.setPassword(userVO.getPassword());
            }
            old.setEmail(userVO.getEmail());
            return userRepository.save(old);
        }
        return null;
    }

    public boolean remove(int idx) {
        User user = findEnabledById(idx);
        if (user != null) {
            user.setEnabled(0);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}