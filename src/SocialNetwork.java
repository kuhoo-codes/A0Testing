import java.util.HashSet;
import java.util.Set;

public class SocialNetwork implements ISocialNetwork {
	
	private Set<Account> accounts = new HashSet<Account>();

	private Account current;

	public Account join(String userName) {
		if (userName == null || userName == "")
			return null;
		// check if user name already exists
		Account existingUser = findAccountForUserName(userName);
		if (existingUser == null) {
			Account newAccount = new Account(userName);
			accounts.add(newAccount);
			return newAccount;
		}
		return null;
	}
	
	public Account login(Account me) {
        if (me == null) return null;
        Account existing = findAccountForUserName(me.getUserName());
        if (existing == null) return null;
        current = existing; 
        return current;
    }

	// find a member by user name 
	private Account findAccountForUserName(String userName) {
		// find account with user name userName
		// not accessible to outside because that would give a user full access to another member's account
		for (Account each : accounts) {
			if (each.getUserName().equals(userName)) 
				return each;
		}
		return null;
	}
	
	public Set<String> listMembers() throws NoUserLoggedInException {
		ensureLoggedIn();
		Set<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}
	
	public boolean hasMember(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
        return findAccountForUserName(userName) != null;
    }
	
	public void sendFriendshipTo(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName != null) {
			accountForUserName.requestFriendship(current);
		}
	}

	private void ensureLoggedIn() throws NoUserLoggedInException {
        if (current == null) throw new NoUserLoggedInException();
    }

	public void block(String userName) throws NoUserLoggedInException {
        ensureLoggedIn();
    }
	
	public void unblock(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
	}
	
	public void sendFriendshipCancellationTo(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.cancelFriendship(current);
	}

	public void acceptFriendshipFrom(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipAccepted(current);
	}

	public void acceptAllFriendships() throws NoUserLoggedInException {
		ensureLoggedIn();
		for (String requester : new HashSet<String>(current.getIncomingRequests())) {
			acceptFriendshipFrom(requester);
		}
	}

	public void rejectFriendshipFrom(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipRejected(current);
	}
	
	public void rejectAllFriendships() throws NoUserLoggedInException {
		ensureLoggedIn();
		for (String requester : new HashSet<String>(current.getIncomingRequests())) {
			rejectFriendshipFrom(requester);
		}
	}

	public void autoAcceptFriendships() throws NoUserLoggedInException {
		ensureLoggedIn();
		current.autoAcceptFriendships();
	};

	
	public void cancelAutoAcceptFriendships() throws NoUserLoggedInException {
		ensureLoggedIn();
		current.cancelAutoAcceptFriendships();
    }

	
	public Set<String> recommendFriends() throws NoUserLoggedInException {
        ensureLoggedIn();
        return new HashSet<>();
    }

	public void leave() throws NoUserLoggedInException {
		ensureLoggedIn();
		//break off all friends
		for (String friend : current.getFriends()) {
			sendFriendshipCancellationTo(friend);
		}
		//reject all incoming friend requests
		for (String requester : current.getIncomingRequests()) {
			rejectFriendshipFrom(requester);
		}
		//retract all outgoing friend requests
		for (String requestee : new HashSet<String>(current.getOutgoingRequests())) {
			Account acc = findAccountForUserName(requestee);
			if (acc != null) {
				acc.getIncomingRequests().remove(current.getUserName());
			}
			current.getOutgoingRequests().remove(requestee);
		}
		accounts.remove(current);
		current = null;
	}

}
