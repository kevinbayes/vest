package refer.api.jaxrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refer.api.jaxrs.vest.VestApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.util.*;

/**
 * <pre>
 * An abstract implementation of a jaxrs {@link javax.ws.rs.core.Application} for refer.
 * </pre>
 *
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
@ApplicationPath("/")
public class ReferApplication extends VestApplication {

}
