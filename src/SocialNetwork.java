import java.util.HashSet;
import java.util.Collection;

public class SocialNetwork {
	
	private Collection<Account> accounts = new HashSet<Account>();

	// join SN with a new user name
	public Account join(String userName) {
		if (userName == null || userName =="") return null;
		// check if user name already exists
		Account existingUser = findAccountForUserName(userName);
		if (existingUser == null) {
			Account newAccount = new Account(userName);
			accounts.add(newAccount);
			return newAccount;
		}
		return null;
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
	
	// list user names of all members
	public Collection<String> listMembers() {
		Collection<String> members = new HashSet<String>();
		for (Account each : accounts) {
			members.add(each.getUserName());
		}
		return members;
	}
	
	// from my account, send a friend request to user with userName from my account
	public void sendFriendshipTo(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		if (accountForUserName != null) {
			accountForUserName.requestFriendship(me);
		}
	}

	// from my account, accept a pending friend request from another user with userName
	public void acceptFriendshipFrom(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipAccepted(me);
	}
	
	// Accept all friend requests that are pending a response from me
	public void acceptAllFriendshipsTo(Account me) {
		me.getIncomingRequests().forEach(requester -> acceptFriendshipFrom(requester, me));
	}

	// from my account, accept a pending friend request from another user with userName
	public void rejectFriendshipFrom(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.friendshipRejected(me);
	}
	
	// Accept all friend requests that are pending a response from me
	public void rejectAllFriendshipsTo(Account me) {
		for (String requester : new HashSet<String>(me.getIncomingRequests())) {
			rejectFriendshipFrom(requester, me);
		}	}

	public void autoAcceptFriendshipsTo(Account me){
		me.autoAcceptFriendships();
	};

	// from another user with userName account, unfriending me as a friend
	public void sendFriendshipCancellationTo(String userName, Account me) {
		Account accountForUserName = findAccountForUserName(userName);
		accountForUserName.cancelFriendship(me);
	}

	// from my account, leaving the social network
	public void leave(Account me) {
		//break off all friends
		for (String friend : me.getFriends()) {
			sendFriendshipCancellationTo(friend, me);
		}
		//reject all incoming friend requests
		for (String requester : me.getIncomingRequests()) {
			rejectFriendshipFrom(requester, me);
		}
		//retract all outgoing friend requests
		for (String requestee : me.getOutgoingRequests()) {
			rejectFriendshipFrom(me.getUserName(), findAccountForUserName(requestee));
		}
		accounts.remove(me);
	}

}
