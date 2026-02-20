package com.github.nderwin.api.adapter.control;

import com.github.nderwin.api.adapter.entity.CanonicalPayment;

public interface RequestAdapter<T> {
    
    String version();
    
    Class<T> requestType();
    
    CanonicalPayment toCanonical(T request);
    
}
