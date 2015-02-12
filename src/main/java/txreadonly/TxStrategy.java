package txreadonly;

import org.apache.deltaspike.jpa.impl.transaction.BeanManagedUserTransactionStrategy;
import org.apache.deltaspike.jpa.impl.transaction.context.EntityManagerEntry;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Dependent @Alternative
public class TxStrategy extends BeanManagedUserTransactionStrategy {

    private @Inject WriteAllowedCheck writeAllowedCheck;

    @Override
    protected Exception prepareException(Exception e) {
        ConstraintViolationException cve = null;
        if (e instanceof ConstraintViolationException) {
            cve = (ConstraintViolationException) e;
        }
        if (cve == null) {
            // sometimes the CVE is wrapped in some JPA Exception...
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof ConstraintViolationException) {
                cve = (ConstraintViolationException) cause;
            }
        }

        if (cve != null) {
            StringBuilder msg = new StringBuilder(e.getMessage());
            msg.append('\n');
            if (cve.getConstraintViolations() != null) {
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    msg.append(cv.getPropertyPath()).append(" -> ").append(cv.getMessage()).append("\n");
                }
            }
            ConstraintViolationException newCve = new ConstraintViolationException(msg.toString(), cve.getConstraintViolations());
            newCve.setStackTrace(cve.getStackTrace());
            e = newCve;
        }

        return e;
    }

    @Override
    protected EntityTransaction getTransaction(EntityManagerEntry entityManagerEntry) {
        EntityTransaction t = super.getTransaction(entityManagerEntry);
        return writeAllowedCheck.wrap(t);
    }

}
