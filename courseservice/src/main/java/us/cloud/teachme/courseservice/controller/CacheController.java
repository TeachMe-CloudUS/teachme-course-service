package us.cloud.teachme.courseservice.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.cloud.teachme.courseservice.service.CacheInspector;

@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    private final CacheInspector cacheInspector;

    public CacheController(CacheInspector cacheInspector) {
        this.cacheInspector = cacheInspector;
    }

    @GetMapping("/inspect/{cacheName}")
    public Map<Object, Object> inspectCache(@PathVariable String cacheName) {
        return cacheInspector.getCacheContents(cacheName);
    }
    
}
