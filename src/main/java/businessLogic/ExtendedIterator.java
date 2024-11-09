package businessLogic;

import java.util.Iterator;

public interface ExtendedIterator<Object> extends Iterator<Object> {
    // Elementua itzuli eta aurreko elementura joan
    public Object previous();

    // Aurreko elementurik dagoen ala ez
    public boolean hasPrevious();

    // Lehendabiziko elementuan kokatu
    public void goFirst();

    // Azkeneko elementuan kokatu
    public void goLast();
}

