package org.hdcola.carnet.Repository;

import org.hdcola.carnet.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);

    boolean existsByEmail(String email);
}
