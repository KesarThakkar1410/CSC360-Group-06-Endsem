import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContrastFilterTest {

    @Test
    public void testFilterInitialization() {
        contrastfilter filter = new contrastfilter();
        assertNotNull(filter, "The contrast filter object should be successfully created.");
    }
}