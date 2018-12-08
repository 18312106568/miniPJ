package com.mrb.minipj;
import com.google.gson.Gson;
import com.mrb.minipj.repository.UserGoodRepository;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mrb.minipj.repository.UserIpRepository;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UrlPathHelper;


/**
 *
 * @author MRB
 */
@SpringBootApplication
public class Application {
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }
    
    
    @RestController
    static class DefaultController{
        @Autowired
        UserGoodRepository userGoodRepository;

        @Autowired
        UserIpRepository userIpRepository;

        Gson gson = new Gson();
        
        @RequestMapping(value = "saveGood")
        public String saveUserGood(Long id){
            System.out.println("保存商品");
            UserGoods userGoods = new UserGoods();
            userGoods.setId(new Long(id));
            userGoods.setGoodDesc("奶茶真好喝");
            userGoods.setGoodName("奶茶");
            if(userGoodRepository.save(userGoods)!=null){
                return "success";
            }
            return  "fail";
        }
        
        @RequestMapping(value = "getGood")
        public String getUserGood(Long id){
            Optional<UserGoods> optional = userGoodRepository.findById(id);
            return optional.get().toString();
        }

        /**
         *
         * @param url （内网分配情况地址）
         * @return
         */
        @RequestMapping(value = "load/userIp")
        public String loadUserIps(String url){
            if(!isNetUrl(url)){
                return "fail";
            }
            String REGEX_ABS = "/(Users|home)/(.*)/logs/metaq/mqtrace/pubTraceLog.log";
            String REGEX_NOR = "C:\\\\Users\\\\(.*)/logs/metaq/mqtrace/pubTraceLog.log";
            String REGEX_FIXED = "/root/logs/metaq/mqtrace/pubTraceLog.log";
            List<UserIp> userIpList = new ArrayList();
            try {

                String result = httpGet(url);
                String items[] = result.split("\r\n");
                for(String item : items){

                    String[] ipAndUser = item.split("\\=");
                    if(ipAndUser.length>=2) {
                        if(ipAndUser[1].matches(REGEX_ABS)){
                            UserIp userIp = new UserIp();
                            userIp.setUserName(ipAndUser[1].replaceAll(REGEX_ABS,"$2"));
                            userIp.setIpAddr(ipAndUser[0]);
                            userIpList.add(userIp);
                        }else if(ipAndUser[1].matches(REGEX_NOR)){
                            UserIp userIp = new UserIp();
                            userIp.setUserName(ipAndUser[1].replaceAll(REGEX_NOR,"$1"));
                            userIp.setIpAddr(ipAndUser[0]);
                            userIpList.add(userIp);

                        }else{

                            continue;
                        }
                    }else{
                        System.out.println(item);
                    }
                }
                userIpRepository.saveAll(userIpList);
                return "success";
            } catch (IOException  e) {
                e.printStackTrace();
                return "fail";
            }
        }

        @RequestMapping(value = "remove/userIp")
        public String removeUserIps(){
            try {
                userIpRepository.deleteAll();
                return "success";
            }catch(Exception e){
                e.printStackTrace();
                return "fail";
            }
        }


        @RequestMapping(value = "get/userIp")
        public String getUserIpsByName(String userName,String ipAddr){
            String result = "none";
            if(!StringUtils.isEmpty(userName)){
                result = gson.toJson(userIpRepository.findByUserName(userName));
                return result;
            }
            if(!StringUtils.isEmpty(ipAddr)){
                result = gson.toJson(userIpRepository.findByIpAddr(ipAddr));
            }
            return result;
        }


        private String httpGet(String url) throws IOException {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "8cff57db-b42a-0142-cbda-cc83c998cdc8")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        private static boolean isNetUrl(String url) {
            boolean reault = false;
            if (url != null) {
                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("rtsp") || url.toLowerCase().startsWith("mms")) {
                    reault = true;
                }
            }
            return reault;
        }
        
    }


    @Data
    @Document(indexName = "es-good",type = "user")
    public static class UserGoods implements Serializable{
        private Long id;
        private String goodName;
        private String goodDesc;
    }

    /**
     * 公司内网人员ip实体
     */
    @Data
    @Document(indexName = "jym-userip", type = "userip")
    public static class UserIp implements Serializable {
        @Id
        private String id;

        private String userName;

        private String ipAddr;
    }
}
