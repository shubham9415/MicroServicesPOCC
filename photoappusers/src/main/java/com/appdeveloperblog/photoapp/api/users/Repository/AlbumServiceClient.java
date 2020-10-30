package com.appdeveloperblog.photoapp.api.users.Repository;

import com.appdeveloperblog.photoapp.api.users.model.AlbumsResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws", fallback = AlbumsFallBack.class)
public interface AlbumServiceClient {

    @GetMapping("/users/{id}/albums")
    public List<AlbumsResponseModel> getAlbums(@PathVariable String id);
}

/** This is the fault tolerance capability in which a fallback class is defined and
 * that class is referred in case of fallbacks.
 */

@Component
class AlbumsFallBack implements AlbumServiceClient {

    @Override
    public List<AlbumsResponseModel> getAlbums(String id) {
        return new ArrayList<AlbumsResponseModel>();
    }
}
