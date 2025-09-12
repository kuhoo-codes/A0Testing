import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SocialNetworkTest {
	SocialNetwork sn;
	Account me, her, another, current;
	
	@Before
	public void setUp() throws Exception {
		sn = new SocialNetwork();
	}

	@After
	public void tearDown() throws Exception {
	
	}

	@Test 
	public void possibleToCreateSocialNetworkWithSingleMember() { // rename --> 
		me = sn.join("Hakan");
		assertNotNull(me);
		assertEquals("Hakan", me.getUserName());
	}

	@Test
	public void joinSNWhenUserNameTakenReturnsNull() {
		me = sn.join("Hakan");
		another = sn.join("Hakan");
		assertEquals(another, null);
	}

	@Test
	public void joinWithNullUserNameReturnsNull() {
		me = sn.join(null);
		assertNull(me);
	}	
	
	@Test
	public void joinWithEmptyUserNameReturnsNull() {
		me = sn.join("");
		assertNull(me);
	}

	@Test 
	public void listMembersAfterOneJoinContainsUserAndSizeMatches() 
			throws NoUserLoggedInException {
		sn.join("Hakan");
		Set<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	@Test 
	public void sizeOfSNMatchesNoOfMultiplePeopleJoining() 
			throws NoUserLoggedInException {
		Set<Account> users = new HashSet<Account>();
		users.add(sn.join("Hakan"));
		users.add(sn.join("Cecile"));

		Set<String> members = sn.listMembers();
		assertEquals(users.size(), members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}
	
	@Test
	public void loginReturnsCorrectUserName() {
		me = sn.join("Hakan");
		current = sn.login(me);
		assertEquals(me.getUserName(), current.getUserName());
	}

	@Test
	public void loginWithNullReturnsNullAccount() {
		current = sn.login(null);
		assertNull(current);
	}

	@Test
	public void loginWithNonExistentAccountReturnsNullAccount() {
		Account nullAccount = new Account("NullAccount");
		current = sn.login(nullAccount);
		assertNull(current);
	}

	@Test
	public void canSwitchAccountsWithoutLogout() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		current = sn.login(me);
		assertEquals(me.getUserName(), current.getUserName());
		current = sn.login(her);                
		assertEquals(her.getUserName(), current.getUserName());
	}
	
	@Test 
	public void sendFriendRequestResultsInIncomingRequest() 
			throws NoUserLoggedInException {
		// test sending friend request
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		assertNotNull(me);
		assertNotNull(her);
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
	}
	
	@Test
	public void shouldNotBeAbleToSendFriendRequestToNonExistantAccount() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		sn.sendFriendshipTo("Cecile");
        assertFalse(me.getOutgoingRequests().contains("Cecile"));
    }

	@Test 
	public void acceptFriendRequestResultsInFriendshipEstablished() 
			throws NoUserLoggedInException {
		// test accepting a friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		sn.login(her);
		sn.acceptFriendshipFrom(me.getUserName());
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

		
	@Test 
	public void acceptingAllFriendshipsResultsInEmptyIncomingRequestsAndFriendshipEstablished() 
			throws NoUserLoggedInException {
		// test accepting all friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertFalse("Incoming requests should not be empty after sending a request",
				me.getIncomingRequests().isEmpty());
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue("Incoming requests should be empty after accepting all requests", me.getIncomingRequests().isEmpty());
		assertTrue(me.hasFriend(her.getUserName()));

	}
	
	@Test 
	public void rejectFriendRequestResultsInNoFriendshipEstablished() throws NoUserLoggedInException {
        me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipTo(her.getUserName());
        sn.login(her);
        sn.rejectFriendshipFrom(me.getUserName());
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }

		
	@Test 
	public void rejectAllFriendshipsResultsInEmptyIncomingRequestsAndNoFriendshipEstablished() 
			throws NoUserLoggedInException {
		// test reject all friendRequest
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        assertFalse(me.getIncomingRequests().isEmpty());
        sn.login(me);
        sn.rejectAllFriendships();
        assertTrue(me.getIncomingRequests().isEmpty());
        assertFalse(me.hasFriend(her.getUserName()));

	}
	
	@Test
	public void autoAcceptFriendshipsWithOneIncomingAddsOneFriend() 
			throws NoUserLoggedInException {
		
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.autoAcceptFriendships();
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        assertTrue(me.hasFriend(her.getUserName()));
        assertTrue(her.hasFriend(me.getUserName()));

	}

    @Test
	public void autoAcceptFriendshipsWithTwoIncomingAddsTwoFriends() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        another = sn.join("Rafal");
        sn.login(me);
        sn.autoAcceptFriendships();
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        sn.login(another);
        sn.sendFriendshipTo(me.getUserName());
        assertTrue(me.hasFriend(her.getUserName()));
        assertTrue(her.hasFriend(me.getUserName()));
        assertTrue(me.hasFriend(another.getUserName()));
        assertTrue(another.hasFriend(me.getUserName()));
    }
	
	@Test
	public void cancelFriendshipRemovesMutualFriendship() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipTo(her.getUserName());
        sn.login(her);
        sn.acceptFriendshipFrom(me.getUserName());
        sn.login(me);
        sn.sendFriendshipCancellationTo(her.getUserName());
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void cancelFriendshipTwiceKeepsUsersNotFriends() 
			throws NoUserLoggedInException {
        me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipTo(her.getUserName());
        sn.login(her);
        sn.acceptFriendshipFrom(me.getUserName());
        sn.login(me);
        sn.sendFriendshipCancellationTo(her.getUserName());
        sn.sendFriendshipCancellationTo(her.getUserName());
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void cancelFriendshipWhenNotFriendsHasNoEffect() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipCancellationTo(her.getUserName());
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }
	
	@Test
	public void leaveRemovesUserFromMembersList() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        sn.login(me);
        sn.leave();
        assertFalse(sn.listMembers().contains(me.getUserName()));
    }

	@Test
	public void leaveRemovesUserFromFriendsLists() 
			throws NoUserLoggedInException {
        me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipTo(her.getUserName());
        sn.login(her);
        sn.acceptFriendshipFrom(me.getUserName());
        sn.login(me);
        sn.leave();
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void leaveRemovesUserFromOthersIncomingRequests() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(me);
        sn.sendFriendshipTo(her.getUserName());
        assertTrue(her.getIncomingRequests().contains(me.getUserName()));
        sn.login(me);
        sn.leave();
        assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}
	
	@Test
	public void leaveRemovesUserFromOthersOutgoingRequests() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
        sn.leave();
        assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
    }

	@Test
	public void sendFriendshipToMissingAccountDoesNotChangeOutgoing() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        int requestSize = me.getOutgoingRequests().size();
        sn.login(me);
        sn.sendFriendshipTo("John"); 
        assertEquals(requestSize, me.getOutgoingRequests().size());
	}

    @Test 
	public void rejectAllFriendshipsWithMultipleIncomingClearsIncomingAndAddsNoFriends() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        another = sn.join("Rafal");
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        sn.login(another);
        sn.sendFriendshipTo(me.getUserName());
        sn.login(me);
        sn.rejectAllFriendships();
        assertTrue(me.getIncomingRequests().isEmpty());
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(me.hasFriend(another.getUserName()));
    }
	
	@Test
	public void acceptAllFriendshipsWithMultipleIncomingClearsIncomingAndAddsAllFriends() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        her = sn.join("Cecile");
        another = sn.join("Rafal");
        sn.login(her);
        sn.sendFriendshipTo(me.getUserName());
        sn.login(another);
        sn.sendFriendshipTo(me.getUserName());
        sn.login(me);
        sn.acceptAllFriendships();
        assertTrue(me.getIncomingRequests().isEmpty());
        assertTrue(me.hasFriend(her.getUserName()));
        assertTrue(me.hasFriend(another.getUserName()));
    }
}
