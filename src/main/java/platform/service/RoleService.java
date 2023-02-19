package platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.domain.Role;
import platform.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private RoleRepository roleRepository;

    public Role findById(int id) {
        return roleRepository.findById(id);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public void add (Role role) {
        logger.info("Added role {}", role);
        roleRepository.save(role);
    }
}
