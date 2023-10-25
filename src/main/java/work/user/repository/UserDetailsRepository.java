package work.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import work.user.domain.UserInfo;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserInfo, Integer> {
    @Query(value = "select * from user_details where user_id=:id", nativeQuery = true)
    Optional<UserInfo> getUserDetailsByUser(Integer id);
}
