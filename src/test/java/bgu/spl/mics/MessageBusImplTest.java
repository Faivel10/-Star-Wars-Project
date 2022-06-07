package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;




class MessageBusImplTest {
    /**
     UPDATE: using microservices which we will unregister at the end of each test so we will get a new empty bus!
     */
    private MessageBusImpl messageBus;
    /**update added two microservices because we  will need them and reuse them so we can unregister them later.
     */
    private MicroService microOne,microTwo;

    @BeforeEach
    public void setUp(){
        /**
         UPDATE creating the new microcervises created.
         */
        messageBus=MessageBusImpl.getInstance();
        microOne = new HanSoloMicroservice();
        microTwo = new C3POMicroservice();
    }

    /**
     UPDATE TO testComplete!!
     Instead of using two microservices for no reason, changed to one because to test complete we need only one microservice.
     also using the microservices we created at the start.
     */

    @Test
    void testComplete()  {

        messageBus.register(microOne);
        AttackEvent event=new AttackEvent();
        //subscring to the event and getting the future object of the event we just sent.
        messageBus.subscribeEvent(event.getClass(),microOne);
        Future<?> result =  messageBus.sendEvent(event);
        //updating the result of the object we just sent.
        messageBus.complete(event,true);
//checking if we got a result and also if it matches the value complete gave it.
        assertTrue(result.isDone());
        assertEquals(true,result.get());

    }

    /**
     UPDATE TO testSendBroadcast!!
     1. using the microservices we created at the start.
     2. we test here both the subscribe and send broadcast.
     3. We test 2 instead of one because a broadcast gets to multiple microservices.
     **/

    @Test
    void testSendBroadcast() throws InterruptedException {
        Broadcast broadcast=new BroadcastImpl();

        messageBus.register(microOne);
        messageBus.register(microTwo);
        //testing subscribing broadcast
        messageBus.subscribeBroadcast(broadcast.getClass(),microOne);
        messageBus.subscribeBroadcast(broadcast.getClass(),microTwo);

        //sending the broadcast needed
        messageBus.sendBroadcast(broadcast);

        //getting the broadcast from the queue.
        Message broadcast1=messageBus.awaitMessage(microOne);
        Message broadcast2=messageBus.awaitMessage(microTwo);

        //checking if what we sent= what we got.
        assertEquals(broadcast,broadcast1);
        assertEquals(broadcast,broadcast2);
    }


    /**
     UPDATE TO testAwaitMessage!!
     1. using the microservices we created at the start.
     */
    @Test
    void testSendEvent() throws InterruptedException {
        AttackEvent event=new AttackEvent();
        messageBus.register(microOne);

        messageBus.subscribeEvent(event.getClass(),microOne);
        messageBus.sendEvent(event);
        Message event1=messageBus.awaitMessage(microOne);
        //the message sent and the message got are the same.
        assertEquals(event1, event);

    }
    /**
     UPDATE TO testAwaitMessage!!
     1. using the microservices we created at the start.
     */
    @Test
    void testAwaitMessage() throws InterruptedException {
        messageBus.register(microOne);
        messageBus.register(microTwo);
        AttackEvent event=new AttackEvent();
        Broadcast b= new BroadcastImpl();
        //subscribing the events and the broadcast needed.
        messageBus.subscribeEvent(event.getClass(),microOne);
        messageBus.subscribeBroadcast(b.getClass(),microTwo);
        //send a broadcast corresponding to b.
        //send event corresponding to "event".
        messageBus.sendBroadcast(b);
        messageBus.sendEvent(event);
        Message q=null,s=null;
        try{
            q=messageBus.awaitMessage(microOne);
            s=messageBus.awaitMessage(microTwo);
        }
        catch (InterruptedException e)
        {}

        //check if the event and broadcast sent are the same as recivied.
        assertEquals(event,q);
        assertEquals(s,b);


    }

    @AfterEach
    public void tearDown(){
        messageBus.unregister(microOne);
        messageBus.unregister(microTwo);

    }



}