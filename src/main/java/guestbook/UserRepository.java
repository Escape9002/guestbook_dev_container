package guestbook;

import org.springframework.data.repository.CrudRepository;

/**
 * https://spring.io/guides/gs/accessing-data-jpa
 * 
 * JPA sees that we extend the CrudRepository and will provide default implementations
 * for nearly everything.
 * 
 * We can provide our own querys/functions by writing them below.
 */
public interface UserRepository extends CrudRepository<User, Long>{

    /**
     * This will invoke a query to look for a username.
     * 
     * If we wrote findByUserName, the query would look for userName entities.
     * This would not work, since the parsed variable from the User-class is
     * all small case (username) and thus not the same (to userName)
     * @param username
     * @return
     */
    User findByUsername(String username);
}
