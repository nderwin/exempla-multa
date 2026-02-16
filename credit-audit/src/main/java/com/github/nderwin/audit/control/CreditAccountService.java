package com.github.nderwin.audit.control;

import com.github.nderwin.audit.entity.CreditAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreditAccountService {

    @Transactional
    public CreditAccount create(final String owner, final long limit) {
        final CreditAccount account = new CreditAccount(owner, limit);
        account.persist();
        
        return account;
    }
    
    @Transactional
    public CreditAccount updateLimit(final long id, final long newLimit) {
        final CreditAccount account = CreditAccount.findById(id);
        account.setCreditLimit(newLimit);
        
        return account;
    }
    
    @Transactional
    public CreditAccount suspend(final long id) {
        final CreditAccount account = CreditAccount.findById(id);
        account.suspend();
        
        return account;
    }
    
}
