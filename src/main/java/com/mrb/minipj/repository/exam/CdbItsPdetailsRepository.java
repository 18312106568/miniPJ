package com.mrb.minipj.repository.exam;

import com.mrb.minipj.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdbItsPdetailsRepository extends JpaRepository<Application.CdbItsPdetails, Long> {
    List<Application.CdbItsPdetails> findAllByPid(Integer pid);
}
