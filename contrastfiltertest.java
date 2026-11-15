// Import the JUnit Test annotation.
import org.junit.jupiter.api.Test;

// Import JUnit methods used to check test results.
import static org.junit.jupiter.api.Assertions.*;

// This class contains a test for the contrast filter.
public class ContrastFilterTest {

    // This test checks whether a contrast filter object can be created.
    @Test
    public void testFilterInitialization() {

        // Create an object of the contrastfilter class.
        contrastfilter filter = new contrastfilter();

        // Check that the object was created successfully and is not null.
        assertNotNull(filter, "The contrast filter object should be successfully created.");
    }
}
