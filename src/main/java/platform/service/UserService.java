package platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.controller.vo.UserVO;
import platform.domain.User;
import platform.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void add(User user) {
        userRepository.save(user);
    }

    public User findById(int idx) {
        Optional<User> result = userRepository.findById(idx);
        return result.orElse(null);
    }

    public User update(int idx, UserVO userVO) {
        User old = userRepository.findById(idx).get();
        old.setName(userVO.getName());
        old.setPassword(userVO.getPassword());
        old.setEmail(userVO.getEmail());
        old.setAdmin(userVO.isAdmin());
        return userRepository.save(old);
    }

    public User update(int idx, UserVO userVO, boolean changePassword) {
        User old = userRepository.findById(idx).get();
        old.setName(userVO.getName());
        if (changePassword) {
            old.setPassword(userVO.getPassword());
        }
        old.setEmail(userVO.getEmail());
        old.setAdmin(userVO.isAdmin());
        return userRepository.save(old);
    }

    public boolean remove(int idx) {
        User user = findById(idx);
        if (user != null) {
            userRepository.delete(user);
            return true;
        } else {
            return false;
        }
    }
}
