import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SocialNetworkTest {
	SocialNetwork sn;
	Account me, her, another;
	
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
	public void listMembersAfterOneJoinContainsUserAndSizeMatches() {
		sn.join("Hakan");
		Set<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	@Test 
	public void sizeOfSNMatchesNoOfMultiplePeopleJoining() { // rename --> make it generic like if people join the network increases
		Set<Account> users = new HashSet<Account>();
		users.add(sn.join("Hakan"));
		users.add(sn.join("Cecile"));

		Set<String> members = sn.listMembers();
		assertEquals(users.size(), members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}
	
	@Test 
	public void sendFriendRequestResultsInIncomingRequest() {
		// test sending friend request
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		assertNotNull(me);
		assertNotNull(her);
		sn.sendFriendshipTo("Cecile", me);
		assertTrue(her.getIncomingRequests().contains("Hakan"));
	}
	
	@Test
    public void shouldNotBeAbleToSendFriendRequestToNonExistantAccount() {
        me = sn.join("Hakan");
		sn.sendFriendshipTo("Cecile", me);
        assertFalse(me.getOutgoingRequests().contains("Cecile"));
    }

	@Test 
	public void acceptFriendRequestResultsInFriendshipEstablished() {
		// test accepting a friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.sendFriendshipTo("Cecile", me);
		sn.acceptFriendshipFrom("Hakan", her);
		assertTrue(me.hasFriend("Cecile"));
		assertTrue(her.hasFriend("Hakan"));
	}

		
	@Test 
	public void acceptingAllFriendshipsResultsInEmptyIncomingRequestsAndFriendshipEstablished() {
		// test accepting all friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.sendFriendshipTo("Hakan", her);

		assertFalse("Incoming requests should not be empty after sending a request", me.getIncomingRequests().isEmpty());
		sn.acceptAllFriendshipsTo(me);
		assertTrue("Incoming requests should be empty after accepting all requests", me.getIncomingRequests().isEmpty());
		assertTrue(me.hasFriend("Cecile"));

	}
	
	@Test 
	public void rejectFriendRequestResultsInNoFriendshipEstablished() {
		// test reject a friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.sendFriendshipTo("Cecile", me);
		sn.rejectFriendshipFrom("Hakan", her);
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

		
	@Test 
	public void rejectAllFriendshipsResultsInEmptyIncomingRequestsAndNoFriendshipEstablished() {
		// test reject all friendRequest
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.sendFriendshipTo("Hakan", her);

		assertFalse("Incoming requests should not be empty after sending a request", me.getIncomingRequests().isEmpty());
		sn.rejectAllFriendshipsTo(me);
		assertTrue("Incoming requests should be empty after accepting all requests", me.getIncomingRequests().isEmpty());
		assertFalse(me.hasFriend("Cecile"));

	}
	
	@Test
	public void autoAcceptFriendshipsWithOneIncomingAddsOneFriend() {
		
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.autoAcceptFriendshipsTo(me);
		sn.sendFriendshipTo(me.getUserName(), her);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));

	}

    @Test
	public void autoAcceptFriendshipsWithTwoIncomingAddsTwoFriends() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		another = sn.join("Rafal");
		me.autoAcceptFriendships();

		sn.sendFriendshipTo(me.getUserName(), her);
		sn.sendFriendshipTo(me.getUserName(), another);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
		assertTrue(another.hasFriend(me.getUserName()));
    }
	
	@Test
	public void cancelFriendshipRemovesMutualFriendship() {
		acceptFriendRequestResultsInFriendshipEstablished();
		sn.sendFriendshipCancellationTo("Cecile", me);
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
    public void cancelFriendshipTwiceKeepsUsersNotFriends() {
        acceptFriendRequestResultsInFriendshipEstablished();
        sn.sendFriendshipCancellationTo("Cecile", me);
        sn.sendFriendshipCancellationTo("Cecile", me);
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void cancelFriendshipWhenNotFriendsHasNoEffect() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
        sn.sendFriendshipCancellationTo(her.getUserName(), me);
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }
	
	@Test
	public void leaveRemovesUserFromMembersList() {
		me = sn.join("Hakan");
		sn.leave(me);
		assertFalse(sn.listMembers().contains(me.getUserName()));
	}

	@Test
    public void leaveRemovesUserFromFriendsLists() {
        acceptFriendRequestResultsInFriendshipEstablished();
        sn.leave(me);
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void leaveRemovesUserFromOthersIncomingRequests() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		her.requestFriendship(me);
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
		sn.leave(me);
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}
	
	@Test
    public void leaveRemovesUserFromOthersOutgoingRequests() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
        me.requestFriendship(her);
        assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
        sn.leave(me);
        assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
    }

	 @Test
	public void sendFriendshipToMissingAccountDoesNotChangeOutgoing() {
		me = sn.join("Hakan");
		int requestSize = me.getOutgoingRequests().size();
		String noExistUsername = "John";
		sn.sendFriendshipTo(noExistUsername, me);
		assertEquals(requestSize, me.getOutgoingRequests().size());
	}

    @Test 
	public void rejectAllFriendshipsWithMultipleIncomingClearsIncomingAndAddsNoFriends() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		another = sn.join("Rafal");
		sn.sendFriendshipTo("Hakan", her);
		sn.sendFriendshipTo("Hakan", another);
		sn.rejectAllFriendshipsTo(me);
		assertTrue("Incoming requests should be empty after accepting all requests", me.getIncomingRequests().isEmpty());
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(me.hasFriend(another.getUserName()));
	}
	
	@Test
	public void acceptAllFriendshipsWithMultipleIncomingClearsIncomingAndAddsAllFriends() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		another = sn.join("Rafal");
		sn.sendFriendshipTo(me.getUserName(), her);
		sn.sendFriendshipTo(me.getUserName(), another);
		sn.acceptAllFriendshipsTo(me);
		assertTrue("Incoming requests should be empty after accepting all requests",
				me.getIncomingRequests().isEmpty());
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
	}
}
