package com.appdeveloperblog.photoapp.api.users.shared;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        switch (response.status()){
            case 400:
                break;
            case 404:
                return new ResponseStatusException(HttpStatus.
                        valueOf(response.status()), "Wrong Processing");
            default:
                return new ResponseStatusException(HttpStatus.
                        valueOf(response.status()));
        }
        return null;
    }
}
