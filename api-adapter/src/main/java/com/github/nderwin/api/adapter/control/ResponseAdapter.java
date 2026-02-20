package com.github.nderwin.api.adapter.control;

import com.github.nderwin.api.adapter.entity.CanonicalPayment;

public interface ResponseAdapter<R> {
    
    String version();
    
    Class<R> responseType();
    
    R fromCanonical(CanonicalPayment model);
    
}
