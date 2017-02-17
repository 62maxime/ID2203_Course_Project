package se.kth.id2203.sharedmemory.port;

import se.kth.id2203.sharedmemory.event.AR_Read_Request;
import se.kth.id2203.sharedmemory.event.AR_Read_Response;
import se.kth.id2203.sharedmemory.event.AR_Write_Request;
import se.kth.id2203.sharedmemory.event.AR_Write_Response;
import se.sics.kompics.PortType;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class ReadImposeWriteConsult extends PortType {

    {
        indication(AR_Read_Response.class);
        indication(AR_Write_Response.class);
        request(AR_Read_Request.class);
        request(AR_Write_Request.class);

    }
}
