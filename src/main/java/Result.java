import java.util.Date;
import java.util.UUID;

public class Result {

    private UUID uuid = null;
    private long sequence = 0;
    private long startedOn = 0;
    private long completedOn = 0;
    private String data = null;

    private String parameter;

    private String payoutAddress;


    public long getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(long startedOn) {
        this.startedOn = startedOn;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getPayoutAddress() {
        return payoutAddress;
    }

    public void setPayoutAddress(String payoutAddress) {
        this.payoutAddress = payoutAddress;
    }
}
