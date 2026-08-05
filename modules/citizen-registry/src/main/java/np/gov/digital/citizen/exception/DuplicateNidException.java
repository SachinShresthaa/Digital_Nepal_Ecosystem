package np.gov.digital.citizen.exception;

public class DuplicateNidException extends RuntimeException {
    private final String nidHash;

    public DuplicateNidException(String nidHash) {
        super("An active citizen already exists with this NID");
        this.nidHash = nidHash;
    }

    public String getNidHash() {
        return nidHash;
    }
}