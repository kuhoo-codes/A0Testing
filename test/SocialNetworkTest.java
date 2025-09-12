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

	// Social Network creation test
	@Test 
	public void possibleToCreateSocialNetworkWithSingleMember() { // rename --> 
		me = sn.join("Hakan");
		assertNotNull(me);
		assertEquals("Hakan", me.getUserName());
	}

	// join tests
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
	public void sizeOfSNMatchesNoOfMultiplePeopleJoining() 
			throws NoUserLoggedInException {
		Set<Account> users = new HashSet<Account>();
		me = sn.join("Hakan");
		users.add(me);
		users.add(sn.join("Cecile"));
		sn.login(me);
		Set<String> members = sn.listMembers();
		assertEquals(users.size(), members.size());
		assertTrue(members.contains("Hakan"));
		assertTrue(members.contains("Cecile"));
	}

	// listMembers test
	@Test 
	public void listMembersAfterOneJoinContainsUserAndSizeMatches() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		Set<String> members = sn.listMembers();
		assertEquals(1, members.size());
		assertTrue(members.contains("Hakan"));
	}
	
	// login Tests
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
	public void canLoginAndSwitchAccountsWithoutLogout() {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		current = sn.login(me);
		assertEquals(me.getUserName(), current.getUserName());
		current = sn.login(her);
		assertEquals(her.getUserName(), current.getUserName());
	}

	// hasMember tests
	@Test(expected = NoUserLoggedInException.class)
	public void hasMemberRequiresLogin() throws NoUserLoggedInException {
		sn.hasMember("Anyone");
	}

	@Test
	public void hasMemberReturnsTrueForJoinedMember() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertTrue(sn.hasMember(me.getUserName()));
	}

	@Test
	public void hasMemberReturnsFalseForNotYetJoinedAccount() throws NoUserLoggedInException {
		Account random = new Account("Random");
		me = sn.join("Hakan");
		sn.login(me);
		assertFalse(sn.hasMember(random.getUserName()));
	}

	@Test
	public void hasMemberReturnsFalseForNullAccount() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		sn.login(me);
		assertFalse(sn.hasMember(null));
	}

	@Test
	public void hasMemberReturnsFalseForAccountThatJoinedAndThenLeft() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		assertTrue(sn.hasMember(her.getUserName()));
		sn.login(her);
		sn.leave();
		sn.login(me);
		assertFalse(sn.hasMember(her.getUserName()));
	}
	
	// sendFriendshipTo tests
	@Test 
	public void sendFriendRequestResultsInIncomingRequest() 
			throws NoUserLoggedInException {
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
	public void sendFriendshipToMissingAccountDoesNotChangeOutgoing() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
        int requestSize = me.getOutgoingRequests().size();
        sn.login(me);
        sn.sendFriendshipTo("John"); 
        assertEquals(requestSize, me.getOutgoingRequests().size());
	}

	// acceptFriendshipFrom tests
	@Test 
	public void acceptFriendRequestResultsInFriendshipEstablished() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		sn.sendFriendshipTo(her.getUserName());
		sn.login(her);
		sn.acceptFriendshipFrom(me.getUserName());
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	// acceptAllFriendships tests
	@Test 
	public void acceptingAllFriendshipsResultsInEmptyIncomingRequestsAndFriendshipEstablished() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertFalse("Incoming requests should not be empty after sending a request",
				me.getIncomingRequests().isEmpty());
		sn.login(me);
		sn.acceptAllFriendships();
		assertTrue("Incoming requests should be empty after accepting all requests",
				me.getIncomingRequests().isEmpty());
		assertTrue(me.hasFriend(her.getUserName()));
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
	
	// rejectFriendshipFrom tests
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

	// rejectAllFriendships tests
	@Test 
	public void rejectAllFriendshipsResultsInEmptyIncomingRequestsAndNoFriendshipEstablished() 
			throws NoUserLoggedInException {
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
	
	// autoAcceptFriendships tests
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
	
	// cancelAutoAcceptFriendships tests
	@Test(expected = NoUserLoggedInException.class)
	public void cancelAutoAcceptRequiresLogin() throws NoUserLoggedInException {
		sn.cancelAutoAcceptFriendships();
	}

	@Test
	public void cancelAutoAcceptPreventsFutureAutoAcceptance() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		another = sn.join("Rafal");
		sn.login(me);
		sn.autoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
		sn.login(me);
		sn.cancelAutoAcceptFriendships();
		sn.login(another);
		sn.sendFriendshipTo(me.getUserName());
		sn.login(me);
		assertTrue(me.getIncomingRequests().contains(another.getUserName()));
		assertFalse(me.hasFriend(another.getUserName()));
		assertFalse(another.hasFriend(me.getUserName()));
	}

	@Test
	public void cancelAutoAcceptHasNoEffectIfAutoAcceptanceWasNeverSet() throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
		sn.login(me);
		sn.cancelAutoAcceptFriendships();
		sn.login(her);
		sn.sendFriendshipTo(me.getUserName());
		sn.login(me);
		assertTrue(me.getIncomingRequests().contains(her.getUserName()));
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}
	
	// sendFriendshipCancellationTo tests
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
	
	// leave tests
	@Test
	public void leaveRemovesUserFromMembersList() 
			throws NoUserLoggedInException {
		me = sn.join("Hakan");
		her = sn.join("Cecile");
        sn.login(me);
		sn.leave();
		sn.login(her);
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
	// --- Task 7: recommendFriends tests ---

	@Test(expected = NoUserLoggedInException.class)
	public void recommendFriendsRequiresLogin() throws NoUserLoggedInException {
		// not logged in
		sn.recommendFriends();
	}

	@Test
	public void recommendFriendsReturnsOnlyUsersWithAtLeastTwoMutuals() throws NoUserLoggedInException {
		// Hakan is me, Cecile and Rafal are my friends.
		// Kuhoo is friend with both Cecile and Hakan => should be recommended.
		// Shannon is friend with only Cecile => should NOT be recommended.
		Account me = sn.join("Hakan");
		Account cecile = sn.join("Cecile");
		Account rafal = sn.join("Rafal");
		Account kuhoo = sn.join("Kuhoo");
		Account shannon = sn.join("Shannon");

		sn.login(me);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(me);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(kuhoo);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		sn.login(kuhoo);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		sn.login(shannon);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(shannon.getUserName());

		sn.login(me);
		Set<String> recs = sn.recommendFriends();

		assertTrue(recs.contains(kuhoo.getUserName()));     
		assertFalse(recs.contains(shannon.getUserName()));    
		assertFalse(recs.contains(cecile.getUserName()));   
		assertFalse(recs.contains(rafal.getUserName()));    
		//assertFalse(recs.contains(me.getUserName()));    
	}
//Kuhoo [To do]
	@Test
	public void recommendFriendsExcludesBlockedUsersInEitherDirection() throws NoUserLoggedInException {
		Account me = sn.join("Hakan");
		Account cecile = sn.join("Cecile");
		Account rafal = sn.join("Rafal");
		Account kuhoo = sn.join("Kuhoo"); //  two mutuals

		// A <-> B and A <-> C
		sn.login(me);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(me);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(me.getUserName());

		// D <-> B and D <-> C (two mutuals with A)
		sn.login(kuhoo);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		sn.login(kuhoo);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		// Hakan blocks Kuhoo 
		sn.login(me);
		sn.block(kuhoo.getUserName());
		Set<String> recs1 = sn.recommendFriends();
		assertFalse(recs1.contains(kuhoo.getUserName()));

		// Kuhoo blocks Hakan -> 
		sn.unblock(kuhoo.getUserName());
		sn.login(kuhoo);
		sn.block(me.getUserName());

		sn.login(me);
		Set<String> recs2 = sn.recommendFriends();
		assertFalse(recs2.contains(kuhoo.getUserName()));
	}

	@Test
	public void recommendFriendsIsEmptyWhenNoCandidateHasTwoMutuals() throws NoUserLoggedInException {
		Account me = sn.join("Hakan");
		Account cecile = sn.join("Cecile");
		Account shannon = sn.join("Shannon"); // only one mutual

		sn.login(me);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(shannon);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(shannon.getUserName());


		sn.login(me);
		Set<String> recs = sn.recommendFriends();
		assertTrue(recs.isEmpty());
	}

	@Test
	public void recommendFriendsDoesNotIncludeExistingFriendsEvenWithTwoMutuals() throws NoUserLoggedInException {
		Account me = sn.join("A");
		Account cecile = sn.join("B");
		Account rafal = sn.join("C");
		Account kuhoo = sn.join("D"); // will be existing friend and also have mutuals

		// A <-> B, A <-> C
		sn.login(me);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(me);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(me.getUserName());

		// Make D friend with B and C
		sn.login(kuhoo);
		sn.sendFriendshipTo(cecile.getUserName());
		sn.login(cecile);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		sn.login(kuhoo);
		sn.sendFriendshipTo(rafal.getUserName());
		sn.login(rafal);
		sn.acceptFriendshipFrom(kuhoo.getUserName());

		// Also A <-> D (so D is an existing friend and must be excluded)
		sn.login(me);
		sn.sendFriendshipTo(kuhoo.getUserName());
		sn.login(kuhoo);
		sn.acceptFriendshipFrom(me.getUserName());

		sn.login(me);
		Set<String> recs = sn.recommendFriends();
		assertFalse(recs.contains(kuhoo.getUserName())); // exclude existing friend
	}
				

}
