package txreadonly;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityTransaction;
import java.util.logging.Logger;

@ApplicationScoped
public class WriteAllowedCheck {

    private static final Logger LOG = Logger.getLogger(WriteAllowedCheck.class.getName());

    private boolean readonlyEnabled;

    public boolean isReadonlyEnabled() {
        return readonlyEnabled;
    }

    public void setReadonlyEnabled(boolean readonlyEnabled) {
        LOG.info("Setting readonly to " + readonlyEnabled);
        this.readonlyEnabled = readonlyEnabled;
    }

    EntityTransaction wrap(final EntityTransaction t) {
        if (readonlyEnabled) {
            // error on new transactions that are not set to rollback-only
            if (!t.getRollbackOnly()) {
                throw new RuntimeException("Cannot write in read-only");
            }
            return new TxAdapter(t) {
                @Override
                public void commit() {
                    super.commit();

                    // No error, changes are not persisted
                    // tx.setRollbackOnly();

                    // No error, changes are not persisted, state is reset
                    // tx.rollback();
                    // super.commit();

                    // also fails for read
                    //throw new RuntimeException();
                }
            };
        }
        return t;
    }

    static class TxAdapter implements EntityTransaction {
        protected final EntityTransaction tx;

        public TxAdapter(EntityTransaction tx) {
            this.tx = tx;
        }

        public void begin() {
            tx.begin();
        }

        public void commit() {
            tx.commit();
        }

        public void rollback() {
            tx.rollback();
        }

        public void setRollbackOnly() {
            tx.setRollbackOnly();
        }

        public boolean getRollbackOnly() {
            return tx.getRollbackOnly();
        }

        public boolean isActive() {
            return tx.isActive();
        }
    }
}
