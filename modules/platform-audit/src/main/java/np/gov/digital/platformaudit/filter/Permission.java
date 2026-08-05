package np.gov.digital.platformaudit.filter;

/**
 * Each role maps to a set of permissions:
 *   WARD_ADMIN:
 *     CITIZEN_READ, CITIZEN_WRITE,
 *     ID_CARD_INITIATE, GRIEVANCE_RECEIVE
 *
 *   LOCAL_BODY_ADMIN:
 *     CITIZEN_READ, CITIZEN_WRITE,
 *     CITIZEN_APPROVE, CITIZEN_ARCHIVE,
 *     ID_CARD_INITIATE, ID_CARD_APPROVE,
 *     GRIEVANCE_RESOLVE, EDIT_APPROVE
 *
 *   PROVINCE_ADMIN:
 *     CITIZEN_READ (own province only — RLS enforced)
 *
 *   CENTRAL_ADMIN:
 *     CITIZEN_READ (all — RLS USING true)
 *     SYSTEM_CONFIG
 */
public final class Permission {

    private Permission() {} // utility class — no instances

    // Citizen
    public static final String CITIZEN_READ    = "CITIZEN_READ";
    public static final String CITIZEN_WRITE   = "CITIZEN_WRITE";
    public static final String CITIZEN_APPROVE = "CITIZEN_APPROVE";
    public static final String CITIZEN_ARCHIVE = "CITIZEN_ARCHIVE";

    // ID Card
    public static final String ID_CARD_INITIATE = "ID_CARD_INITIATE";
    public static final String ID_CARD_APPROVE  = "ID_CARD_APPROVE";

    // Edit approval
    public static final String EDIT_APPROVE = "EDIT_APPROVE";
    public static final String EDIT_REJECT  = "EDIT_REJECT";

    // Grievance
    public static final String GRIEVANCE_RECEIVE = "GRIEVANCE_RECEIVE";
    public static final String GRIEVANCE_RESOLVE = "GRIEVANCE_RESOLVE";

    // System
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
}