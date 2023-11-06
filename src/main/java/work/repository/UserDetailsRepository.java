package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import work.domain.UserDetails;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {

    @Query(value = "select * from user_details ud right join public.users u on u.id = ud.user_id where u.id=:userId",nativeQuery = true)
    Optional<UserDetails> findByUserId(Integer userId);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "update user_details set user_id=:userId where id=:userDetailsId", nativeQuery = true)
    void setUserDetailsId(@Param("userId") Integer userId, @Param("userDetailsId") Integer userDetailsId);
}
