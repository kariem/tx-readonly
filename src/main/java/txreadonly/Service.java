package txreadonly;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Service {

    private static final Logger log = Logger.getLogger(Service.class.getName());

    private @Inject EntityManager em;

    @Transactional
    public LogEntry createLog(String description) {
        log.info("Start write");
        LogEntry e = new LogEntry();
        e.setName(description);
        e.setDate(new Date());
        em.persist(e);
        log.info("End write");
        return e;
    }

    @Transactional(readOnly = true)
    public List<LogEntry> findAll() {
        log.info("Start read");
        List<LogEntry> l = em.createQuery("select l From LogEntry as l", LogEntry.class).getResultList();
        log.info("End read");
        return l;
    }
}
