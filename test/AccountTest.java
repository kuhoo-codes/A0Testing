import static org.junit.Assert.*;

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
    public void sendingFriendRequestResultsInIncomingRequest() {
        me.requestFriendship(her);
        assertTrue(me.getIncomingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void noFriendRequestsResultsInNoIncomingRequest() {
        assertEquals(0, me.getIncomingRequests().size());
    }
    
    @Test
    public void twoFriendRequestsFromDifferentPeopleResultsInTwoIncomingRequests() {
        me.requestFriendship(her);
        me.requestFriendship(another);
        assertEquals(2, me.getIncomingRequests().size());
        assertTrue(me.getIncomingRequests().contains(another.getUserName()));
        assertTrue(me.getIncomingRequests().contains(her.getUserName()));
    }
    
    @Test
    public void twoFriendRequestsFromOnePersonResultsInOneIncomingRequest() {
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
    public void everybodyAreFriends() {
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

}