package bgu.spl.mics;

public class Ewok {
    int serialNumber;
    boolean available;

    public boolean acquire(){
        if(available == false)
            return false;
        available = false;
        return true;

    }

    public void release() {
        available = true;
    }
}
