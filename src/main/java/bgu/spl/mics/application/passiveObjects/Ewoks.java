package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public  class  Ewoks {
    private final LinkedList<Ewok> collectionOfEwok;


    private Ewoks(){
        collectionOfEwok=new LinkedList<>();

    }

    //creates sub class to make Ewoks to be Singleton

    private static class SingeltonEwoks{
        private static Ewoks instance=new Ewoks();
    }

    public static Ewoks getInstance(){
        return Ewoks.SingeltonEwoks.instance;
    }

    //serialNumber got from Attack, and added by calling from microService
    public  synchronized void add(int serialNumber){
        Ewok ewok=new Ewok();
        ewok.setSerialNumber(serialNumber);
        //add the ewok in the right place accoring to the number to avoid deadlocks.
        if(collectionOfEwok.isEmpty()){
        collectionOfEwok.add(ewok);}
        else{
            boolean end=false;
            //add the ewok to the list according to its seriel number.
            for( int i=0;i<=collectionOfEwok.size() &&!end;i=i+1)
            {
                if(i==collectionOfEwok.size())
                {
                    collectionOfEwok.addLast(ewok);
                    end=true;
                }
                else if(collectionOfEwok.get(i).getSerialNumber()>serialNumber)
                {
                    collectionOfEwok.add(i,ewok);
                    end=true;
                }
            }
        }
    }

    //returns the ewok with the specific seriel number.
    public  Ewok get(int i){
        Ewok result;
        //delete
        for(Ewok e:collectionOfEwok)
        {
            if(e.getSerialNumber()==i)
            {
                result=e;
                e.acquire();
                return result;
            }
        }
        //if there is no ewok like this we return null
        return null;
    }



}
