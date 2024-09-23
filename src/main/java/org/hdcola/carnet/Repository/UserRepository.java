package org.hdcola.carnet.Repository;

import org.hdcola.carnet.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    public User findByEmail(String email);

    boolean existsByEmail(String email);

    int countByHasAppliedTrueAndVerifiedFalse();

    Page<User> findByHasAppliedAndVerified(boolean hasApplied, boolean verified, Pageable pageable);
}
