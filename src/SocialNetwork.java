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
	
	private java.util.Map<String, java.util.Set<String>> blocked = new java.util.HashMap<>();

	public void unblock(String userName) throws NoUserLoggedInException {
		ensureLoggedIn();
		if (userName == null) return;

		java.util.Set<String> set = blocked.get(current.getUserName());
		if (set != null) {
			set.remove(userName);
		}

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

    String me = current.getUserName();
    Set<String> myFriends = new HashSet<String>(current.getFriends());
    java.util.Map<String, Integer> counts = new java.util.HashMap<String, Integer>();

	myFriends.forEach(name -> {
		Account friendAccount = findAccountForUserName(name);
		if (friendAccount != null) {
			friendAccount.getFriends().forEach(friendOfFriend -> {
				if (friendOfFriend.equals(me)) return;
				if (myFriends.contains(friendOfFriend)) return;

				// check blocking both directions
				if (blocked.containsKey(me) && blocked.get(me).contains(friendOfFriend)) return;
				if (blocked.containsKey(friendOfFriend) && blocked.get(friendOfFriend).contains(me)) return;

				Account fofAccount = findAccountForUserName(friendOfFriend);
				if (fofAccount == null) return;

				if (counts.containsKey(friendOfFriend)) {
					counts.put(friendOfFriend, counts.get(friendOfFriend) + 1);
				} else {
					counts.put(friendOfFriend, 1);
				}
			});
		}
	});

    Set<String> recommendations = new HashSet<String>();
    for (String candidate : counts.keySet()) {
        if (counts.get(candidate) >= 2) {
            recommendations.add(candidate);
        }
    }
    return recommendations;
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
