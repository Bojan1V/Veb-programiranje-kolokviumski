package mk.ukim.finki.wp.kol2022.g3.repository;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ForumUserRepository extends JpaRepository<ForumUser,Long> {
    List<ForumUser> findAllForumUserByInterestsContaining(Interest interest);
    List<ForumUser> findAllForumUserByBirthdayBefore(LocalDate localDate);
    List<ForumUser> findAllForumUserByInterestsContainingAndBirthdayBefore(Interest interest,LocalDate localDate);
    ForumUser findForumUserByEmail(String username);
}
