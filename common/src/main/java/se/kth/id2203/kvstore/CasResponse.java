package se.kth.id2203.kvstore;

import java.util.UUID;

/**
 * Created by 62maxime on 23/02/2017.
 */
public class CasResponse extends OpResponse {

    private boolean success;

    public CasResponse(UUID id, Code status, boolean success) {
        super(id, status);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
