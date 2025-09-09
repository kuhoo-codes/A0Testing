import static org.junit.Assert.*;

import java.util.Collection;
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
	public void socialNetworkIsCreated() {
		me = sn.join("Hakan");
		assertNotNull(me);
		assertEquals("Hakan", me.getUserName());
	}
	@Test
	public void joiningTheSocialNetworkWithNullUserNameShouldReturnNullAccount() {
		me = sn.join(null);
		assertNull(me);
	}	
	
	@Test
	public void joiningTheSocialNetworkWithEmptyUserNameShouldReturnNullAccount() {
		me = sn.join("");
		assertNull(me);
	}

	@Test 
	public void canListSingleMemberOfSocialNetworkAfterOnePersonJoiningAndSizeOfNetworkEqualsOne() {
		sn.join("Hakan");
		Collection<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	@Test 
	public void twoPeopleJoiningSocialNetworkResultsInSizeOfNetworkEqualsTwo() {
		Set<Account> users = new HashSet<Account>();
		users.add(sn.join("Hakan"));
		users.add(sn.join("Cecile"));

		Collection<String> members = sn.listMembers();
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
	//[to do - fix this test]
	@Test
	public void shouldNotBeAbleToBecomeFriendsIfOneIsNotAskedFirst() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.acceptFriendshipFrom("Cecile", me);
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
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
	

	//Task 5 Testing
	@Test
	public void autoAcceptFriendshipsWithOneIncomingAddsOneFriend() {
		
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.autoAcceptFriendshipsTo(me);
		sn.sendFriendshipTo(me.getUserName(), her);
		assertTrue(me.hasFriend(her.getUserName()));
		// [ TODO ] fix this test
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
	
	//Task 6 Testing
	@Test
	public void sendingFriendshipCancellationToAccountCausesBothAccountsToNoLongerBeFriends() {
		acceptFriendRequestResultsInFriendshipEstablished();
		sn.sendFriendshipCancellationTo("Cecile", me);
		assertFalse(me.hasFriend("Cecile"));
		assertFalse(her.hasFriend("Hakan"));
	}

	@Test
    public void sendFriendshipCancellationTwiceResultsInStillNotFriends() {
        acceptFriendRequestResultsInFriendshipEstablished();
        sn.sendFriendshipCancellationTo("Cecile", me);
        sn.sendFriendshipCancellationTo("Cecile", me);
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void sendFriendshipCancellationWhenNotFriendsResultsInNotBeingFriends() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
        sn.sendFriendshipCancellationTo(her.getUserName(), me);
        assertFalse(me.hasFriend(her.getUserName()));
        assertFalse(her.hasFriend(me.getUserName()));
    }
	
	//Task 7 Testing
	@Test
	public void existingMemberLeavingRemovesAccountFromMemberList() {
		me = sn.join("Hakan");
		sn.leave(me);
		assertFalse(sn.listMembers().contains(me.getUserName()));
	}

	@Test
    public void leavingUserHasTheirAccountRemovedFromTheirFriendsLists() {
        acceptFriendRequestResultsInFriendshipEstablished();
        sn.leave(me);
        assertFalse(her.hasFriend(me.getUserName()));
    }

	@Test
	public void leavingUserIsRemovedFromIncomingRequestsOfOtherUserThatLeavingUserRequestedFriendshipFrom() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		her.requestFriendship(me);
		assertTrue(her.getIncomingRequests().contains(me.getUserName()));
		sn.leave(me);
		assertFalse(her.getIncomingRequests().contains(me.getUserName()));
	}
	
	@Test
    public void leavingUserIsRemovedFromOutgoingRequestsOfOtherUserWhoRequestedFriendshipFromLeavingUser() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
        me.requestFriendship(her);
        assertTrue(her.getOutgoingRequests().contains(me.getUserName()));
        sn.leave(me);
        assertFalse(her.getOutgoingRequests().contains(me.getUserName()));
    }

	 @Test
	public void sendingAFriendRequestToAccountThatDoesNotExistShouldHaveNoEffect() {
		me = sn.join("Hakan");
		int requestSize = me.getOutgoingRequests().size();
		String noExistUsername = "John";
		sn.sendFriendshipTo(noExistUsername, me);
		assertEquals(requestSize, me.getOutgoingRequests().size());
	}

	@Test
	public void cannotJoinSocialNetworkIfAlreadyAnExistingMember() {
		me = sn.join("Hakan");
		another = sn.join("Hakan");
		assertEquals(another, null);
	}

    @Test 
	public void existingMemberCanRejectAllFriendshipsAtOnce() {
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

}
