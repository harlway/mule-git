import org.mule.umo.UMOEventContext
import org.mule.umo.lifecycle.Callable

public class GroovyDynamicScript implements Callable
{
    public Object onCall(UMOEventContext eventContext) throws Exception
    {
        return eventContext.getMessage().getPayloadAsString() + " Received2"
    }
}