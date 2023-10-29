package work.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import work.user.domain.User;
import work.user.domain.UserDetails;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

    @Query(value = "select * from user_details ud right join public.users u on u.id = ud.user_id where u.id=:userId",nativeQuery = true)
    Optional<UserDetails> findByUserId(Integer userId);
}
