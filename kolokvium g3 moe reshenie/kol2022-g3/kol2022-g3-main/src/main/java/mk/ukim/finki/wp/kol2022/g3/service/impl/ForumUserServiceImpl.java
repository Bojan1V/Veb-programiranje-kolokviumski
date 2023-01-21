package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ForumUserServiceImpl implements ForumUserService, UserDetailsService {

    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ForumUser> listAll() {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        String encryptedPassword = this.passwordEncoder.encode(password);
        List<Interest> interests = this.interestRepository.findAllById(interestId);
        ForumUser forumUser = new ForumUser(name,email,encryptedPassword,type,interests,birthday);
        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        String encryptedPassword = this.passwordEncoder.encode(password);//mozno e i ova da ne trebit
        ForumUser forumUser = this.findById(id);
        forumUser.setName(name);
        forumUser.setEmail(email);
        forumUser.setPassword(encryptedPassword);
        forumUser.setType(type);
        forumUser.setBirthday(birthday);

        List<Interest> interests = this.interestRepository.findAllById(interestId);
        forumUser.setInterests(interests);

        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser forumUser = this.findById(id);
        this.forumUserRepository.delete(forumUser);
        return forumUser;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        List<ForumUser> results;

        if(interestId==null && age==null){
            results=forumUserRepository.findAll();
        }
        else if(interestId!=null && age != null)
        {
            results=forumUserRepository.findAllForumUserByInterestsContainingAndBirthdayBefore(interestRepository.getById(interestId),LocalDate.now().minusYears(age));
        }
        else if(age!=null&&interestId==null){
            results=forumUserRepository.findAllForumUserByBirthdayBefore(LocalDate.now().minusYears(age));
        }
        else{
            results=forumUserRepository.findAllForumUserByInterestsContaining(interestRepository.getById(interestId));
        }
        return results;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser e = forumUserRepository.findForumUserByEmail(username);
        return new User(
                e.getEmail(),
                e.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%S", e.getType()))).collect(Collectors.toList())
        );
    }
}
