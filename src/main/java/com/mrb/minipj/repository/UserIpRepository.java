package com.mrb.minipj.repository;

import com.mrb.minipj.Application;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserIpRepository extends ElasticsearchRepository<Application.UserIp, String> {

    List<Application.UserIp> findByUserName(String userName);

    List<Application.UserIp> findByIpAddr(String ipAddr);

}
