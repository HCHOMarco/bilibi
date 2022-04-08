package com.marco.bilibili.api;

import com.marco.bilibili.domain.JsonResponse;
import com.marco.bilibili.domain.Video;
import com.marco.bilibili.service.DemoService;
import com.marco.bilibili.service.ElasticSearchService;
import com.marco.bilibili.service.util.FastDFSUtil;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/query")
    public Long query(Long id){
        return demoService.query(id);
    }

    @GetMapping("/slices")
    public void slice(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
    }

    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword){
        Video video = elasticSearchService.getVideos(keyword);
        return new JsonResponse<>(video);
    }
}
