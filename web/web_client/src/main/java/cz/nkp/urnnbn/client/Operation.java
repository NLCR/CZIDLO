package cz.nkp.urnnbn.client;

public interface Operation<T> {

    public void run(T data);

}
