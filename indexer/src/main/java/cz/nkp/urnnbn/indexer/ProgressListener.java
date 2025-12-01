package cz.nkp.urnnbn.indexer;

/**
 * Created by Martin Řehánek on 26.1.18.
 */
public interface ProgressListener {

    public void onProgress(int processed, int total);

    public void onFinished(int processed, int total);

}
