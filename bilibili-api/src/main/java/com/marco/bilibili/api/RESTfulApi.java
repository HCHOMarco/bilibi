package com.marco.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RESTfulApi {
    private final  Map<Integer, Map<String, Object>> dataMap;

    public RESTfulApi() {
        dataMap = new HashMap<>();
        for(int i = 1; i < 3; i++){
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("id", i);
            tmp.put("name", "name" + i);
            dataMap.put(i, tmp);
        }
    }

    @GetMapping("/objects/{id}")
    public Map<String, Object> getData(@PathVariable Integer id){
        return dataMap.get(id);
    }

    @DeleteMapping("/objects/{id}")
    public String deleteData(@PathVariable Integer id){
        dataMap.remove(id);
        return "delete success";
    }

    @PostMapping("/objects")
    public String postData(@RequestBody Map<String, Object> data){
        Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length - 1] + 1;
        dataMap.put(nextId, data);
        return "post success";
    }

    @PutMapping("/objects")
    public String putData(@RequestBody Map<String, Object> data){
        int id = Integer.parseInt(String.valueOf(data.get("id")));
        Map<String, Object> tmp = dataMap.get(id);
        if(tmp == null){
            Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            dataMap.put(nextId, data);
        }else{
            dataMap.put(id, data);
        }
        return "put success";
    }
}
