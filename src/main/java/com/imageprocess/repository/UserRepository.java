package com.imageprocess.repository;

import com.imageprocess.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsernameAndPassword(String username, String password);
    public Optional<User> findByUsername(String username);
}
