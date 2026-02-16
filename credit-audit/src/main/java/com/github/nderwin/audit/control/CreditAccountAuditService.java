package com.github.nderwin.audit.control;

import com.github.nderwin.audit.entity.CreditAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

@ApplicationScoped
public class CreditAccountAuditService {

    @Inject
    EntityManager em;
    
    public List<Number> revisions(final long accountId) {
        final AuditReader reader = AuditReaderFactory.get(em);
        return reader.getRevisions(CreditAccount.class, accountId);
    }
    
    public CreditAccount atRevision(final long accountId, final Number revision) {
        final AuditReader reader = AuditReaderFactory.get(em);
        return reader.find(CreditAccount.class, accountId, revision);
    }
    
    public AuditSnapshot snapshot(final long accountId, final Number revision) {
        final AuditReader reader = AuditReaderFactory.get(em);
        final CreditAccount account = reader.find(CreditAccount.class, accountId, revision);
        
        if (null == account) {
            return null;
        }
        
        final Date revisionDate = reader.getRevisionDate(revision);
        
        return new AuditSnapshot(
                account, 
                revision.longValue(), 
                revisionDate.toInstant()
        );
    }

    public static record AuditSnapshot(
            CreditAccount account,
            long revision,
            Instant timestamp) {
        
    }

}
