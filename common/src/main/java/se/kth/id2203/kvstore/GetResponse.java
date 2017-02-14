package se.kth.id2203.kvstore;

import java.util.UUID;

/**
 * Created by 62maxime on 13/02/2017.
 */
public class GetResponse extends OpResponse {

    public GetResponse(UUID id, Code status) {
        super(id, status);
    }
}
