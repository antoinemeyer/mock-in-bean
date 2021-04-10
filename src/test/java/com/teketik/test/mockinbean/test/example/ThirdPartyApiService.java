package com.teketik.test.mockinbean.test.example;

import org.springframework.stereotype.Service;

@Service
public class ThirdPartyApiService {

    public void doSomethingOnThirdPartyApi(Object somethingExpensive) {
        throw new UnsupportedOperationException();
    }

}
