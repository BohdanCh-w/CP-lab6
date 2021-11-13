import java.util.ArrayList;
import java.util.List;

public class Subscription {
    public int subID;
    public String fName;
    public String mName;
    public String lName;
    public String email;
    public ArrayList<Lending> lendings;

    public String getFName() { return fName; }
    public String getMName() { return mName; }
    public String getLName() { return lName; }
    public String getEmail() { return email; }
    public List<Lending> getLendings() { return lendings; }

    public Subscription(int id, String fName_, String mName_, String lName_, String email_) {
        subID = id;
        fName = fName_;
        mName = mName_;
        lName = lName_;
        email = email_;
        lendings = new ArrayList<Lending>();
    }

    public String toString() {
        return String.format("%6d: %s %s %s. %s", subID, fName, mName, lName, email);
    }
}
