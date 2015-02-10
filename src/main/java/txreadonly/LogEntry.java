package txreadonly;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class LogEntry {

    @Id @GeneratedValue
    private Long id;

    @Version
    private Long optLock;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date date;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public Long getOptLock() {
        return optLock;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
