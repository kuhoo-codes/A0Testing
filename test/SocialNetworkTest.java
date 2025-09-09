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
	public void sendFriendRequest() {
		// test sending friend request
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		assertNotNull(me);
		assertNotNull(her);
		sn.sendFriendshipTo("Cecile", me);
		assertTrue(her.getIncomingRequests().contains("Hakan"));
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
	
	// @Test
	// public void autoAcceptFriendships() {
	// 	me = sn.join("Hakan");
	// 	her = sn.join("Cecile");
	// 	sn.sendFriendshipTo("Hakan", her);
	// 	sn.autoAcceptFriendshipsTo(me);
	// 	assertTrue(me.hasFriend("Cecile"));
	// 	// [ TODO ] fix this test

	// 	assertTrue(her.hasFriend("Hakan"));

	// }
}
