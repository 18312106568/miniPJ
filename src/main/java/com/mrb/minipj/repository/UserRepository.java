
package com.mrb.minipj.repository;

import com.mrb.minipj.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author MRB
 */

public interface UserRepository extends JpaRepository<Application.User, Long>{
    List<Application.User> findAllByName(String name);
}
