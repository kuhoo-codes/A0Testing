import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class AccountTest {
    
    Account me, her, another;
    
    @Before
    public void setUp() throws Exception {
        me = new Account("Hakan");
        her = new Account("Serra");
        another = new Account("Cecile");
    }

    @Test
    public void receivingFriendRequestResultsInIncomingRequest() {
        me.requestFriendship(her);
        assertTrue(me.getIncomingRequests().contains(her.getUserName()));
    }

    @Test
    public void sendingFriendRequestResultsInOutgoingRequest() {
        her.requestFriendship(me);
        assertTrue(me.getOutgoingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void noFriendRequestsResultsInNoIncomingRequest() {
        assertEquals(0, me.getIncomingRequests().size());
    }
    
    @Test
    public void requestFriendshipFromTwoUsersAddsBothToIncomingAndSizeMatches() {
        me.requestFriendship(her);
        me.requestFriendship(another);
        Set<String> expected = new HashSet<>();
        expected.add(her.getUserName());
        expected.add(another.getUserName());

        assertEquals(expected.size(), me.getIncomingRequests().size());
        assertTrue(me.getIncomingRequests().contains(another.getUserName()));
        assertTrue(me.getIncomingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void duplicateRequestsFromSameUserAddsSingleIncoming() {
        me.requestFriendship(her);
        me.requestFriendship(her);
        assertEquals(1, me.getIncomingRequests().size());
    }
    
    @Test
    public void afterAcceptingFriendRequestIncomingRequestsUpdated() {
        me.requestFriendship(her);
        her.friendshipAccepted(me);
        assertFalse(me.getIncomingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void afterFriendAcceptingFriendRequestOutgoingRequestsUpdated() {
        her.requestFriendship(me);
        me.friendshipAccepted(her);
        assertFalse(me.getOutgoingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void mutualAcceptsAmongAllEstablishMutualFriendships() {
        me.requestFriendship(her);
        me.requestFriendship(another);
        her.requestFriendship(another);
        her.friendshipAccepted(me);
        another.friendshipAccepted(her);
        another.friendshipAccepted(me);
        assertTrue(me.hasFriend(her.getUserName()));
        assertTrue(me.hasFriend(another.getUserName()));
        assertTrue(her.hasFriend(me.getUserName()));
        assertTrue(her.hasFriend(another.getUserName()));
        assertTrue(another.hasFriend(her.getUserName()));
        assertTrue(her.hasFriend(me.getUserName()));
    }
    
    @Test
    public void sendingFriendRequestToAnExistingFriendResultInNoIncomingRequests() {
        me.requestFriendship(her);
        her.friendshipAccepted(me);
        assertTrue(her.hasFriend(me.getUserName()));
        me.requestFriendship(her);
        assertFalse(me.getIncomingRequests().contains(her.getUserName()));
        assertFalse(her.getIncomingRequests().contains(me.getUserName()));
    }

	@Test
	public void friendshipAcceptedEstablishesMutualFriendship() {
		me.requestFriendship(her);
		her.friendshipAccepted(me);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
	}

	@Test
    public void autoAcceptFriendshipsWithOneIncomingAddsThatFriendMutually() {
        me.autoAcceptFriendships();
        me.requestFriendship(her);
        assertTrue(me.hasFriend(her.getUserName()));
        assertTrue(her.hasFriend(me.getUserName()));
    }

    @Test
    public void autoAcceptFriendshipsWithTwoIncomingAddsTwoFriends() {
        me.autoAcceptFriendships();
        me.requestFriendship(her);
		me.requestFriendship(another);
		assertTrue(me.hasFriend(her.getUserName()));
		assertTrue(her.hasFriend(me.getUserName()));
		assertTrue(me.hasFriend(another.getUserName()));
		assertTrue(another.hasFriend(me.getUserName()));
    }

	@Test
	public void cancelFriendshipRemovesMutualFriendship() {
		friendshipAcceptedEstablishesMutualFriendship();
		her.cancelFriendship(me);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

	@Test
	public void cancelFriendshipTwiceKeepsUsersNotFriends() {
		friendshipAcceptedEstablishesMutualFriendship();
		her.cancelFriendship(me);
		her.cancelFriendship(me);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}
	
	@Test
	public void cancelFriendshipWhenNotFriendsHasNoEffect() {
		her.cancelFriendship(me);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));
	}

    @Test
	public void requestFriendshipWithNullAccountHasNoEffect() {
		int requestSize = me.getIncomingRequests().size();
		me.requestFriendship(null);
		assertEquals(requestSize, me.getIncomingRequests().size());
	}

    @Test
	public void friendshipAcceptedWithoutRequestDoesNotEstablishFriendship() {
		her.friendshipAccepted(me);
		assertFalse(me.hasFriend(her.getUserName()));
		assertFalse(her.hasFriend(me.getUserName()));;
	}

}