package ltweb.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import ltweb.entity.User;

public interface UserService {

	Page<User> findByUsernameContaining(String username, Pageable pageable);

	void deleteById(int id);

	User save(User user);

	Optional<User> findById(int id);

	Page<User> findAll(Pageable pageable);

	List<User> findAll();

}
