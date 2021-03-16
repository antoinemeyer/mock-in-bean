package com.teketik.mockinbean.example;

import org.springframework.stereotype.Service;

@Service
public class ThirdPartyApiService {

    public void doSomethingOnThirdPartyApi(Object somethingExpensive) {
        throw new UnsupportedOperationException();
    }

}
