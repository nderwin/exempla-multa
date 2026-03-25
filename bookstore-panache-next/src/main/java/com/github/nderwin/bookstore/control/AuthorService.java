package com.github.nderwin.bookstore.control;

import com.github.nderwin.bookstore.entity.Author_;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthorService {
    
    public void printAllBritish() {
        Author_.repo().findByCountry("UK")
                .forEach(a -> System.out.println(a.getName()));
    }
    
    @Transactional
    public long promoteBritishAuthors() {
        return Author_.repo().findByCountry("UK").stream()
                .peek(a -> a.setName(a.getName() + " (Featured)"))
                .count();
        
    }
    
    @Transactional
    public void transferAuthors(final String fromCountry, final String toCountry) {
        Author_.repo().findByCountry(fromCountry)
                .forEach(a -> a.setCountry(toCountry));
        
    }

}
