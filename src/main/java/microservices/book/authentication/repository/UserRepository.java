package microservices.book.authentication.repository;


import microservices.book.authentication.user.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository extends CrudRepository<User, Long> {

    User findByAliasAndPassword(final String alias, final String password);
    Optional<User> findByAlias(final String alias);

}
