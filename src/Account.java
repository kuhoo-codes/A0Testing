import java.util.HashSet;
import java.util.Set;


public class Account  {
    
    // the unique user name of account owner
    private String userName;
    
    // list of members who are awaiting an acceptance response from this account's owner 
    private Set<String> incomingRequests = new HashSet<String>();

    // list of members who this account's owner is awating an acceptance response from
    private Set<String> outgoingRequests = new HashSet<String>();
    
    // list of members who are friends of this account's owner
    private Set<String> friends = new HashSet<String>();

    private boolean autoAcceptFriendships = false;

    private Set<String> blocked = new HashSet<>();

    public Account(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    // return list of members who had sent a friend request to this account's owner 
    // and are still waiting for a response
    public Set<String> getIncomingRequests() {
        return incomingRequests;
    }

    // return list of members who this account's owner had send a friend request to
    // and is still waiting for a response
    public Set<String> getOutgoingRequests() {
        return outgoingRequests;
    }

    // an incoming friend request to this account's owner from another member account
    public void requestFriendship(Account fromAccount) {
        if (fromAccount == null)
            return;
        if (!friends.contains(fromAccount.getUserName())) {
            incomingRequests.add(fromAccount.getUserName());
            fromAccount.outgoingRequests.add(this.getUserName());
            if (autoAcceptFriendships) {
                fromAccount.friendshipAccepted(this);
            }
        }
    }

    // check if account owner has a member with user name userName as a friend
    public boolean hasFriend(String userName) {
        return friends.contains(userName);
    }

    public boolean hasBlocked(String userName) {
        return blocked.contains(userName);
    }

    public void block(String userName) {
        if (userName != null)
            blocked.add(userName);
    }

    // receive an acceptance from a member to whom a friend request has been sent and from whom no response has been received
    public void friendshipAccepted(Account toAccount) {
        if (toAccount.incomingRequests.contains(this.getUserName())) {
            friends.add(toAccount.getUserName());
            toAccount.friends.add(this.getUserName());
            toAccount.incomingRequests.remove(this.getUserName());
            outgoingRequests.remove(toAccount.getUserName());
        }
    }
    
    public Set<String> getFriends() {
        return friends;
    }
    
    public void friendshipRejected(Account toAccount) {
		toAccount.incomingRequests.remove(this.getUserName());
        outgoingRequests.remove(toAccount.getUserName());
    }

    public void autoAcceptFriendships() {
        this.autoAcceptFriendships = true;
    }
    
    public void cancelAutoAcceptFriendships() {
		this.autoAcceptFriendships = false;
	}

	// an existing friend of this account's owner is unfriending them
	public void cancelFriendship(Account fromAccount) {
		if (friends.contains(fromAccount.getUserName())) {
			friends.remove(fromAccount.getUserName());
			fromAccount.friends.remove(this.getUserName());
		}
	}
    
}
