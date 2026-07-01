package de.htw_belin.Bookblock.repository;

import de.htw_belin.Bookblock.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /** Sucht einen Nutzer per E-Mail (fuer Login und Registrierungs-Check). */
    Optional<User> findByEmail(String email);
}
