package test;

import br.com.redrails.torpedos.parse.SyncActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.TestCase;

/**
 * Created by swav on 21/08/13.
 */
public class SyncActivityTest extends TestCase {
    public SyncActivityTest() {
        super(SyncActivity.class);
    }

    @MediumTest
    public void testToFail(){
        fail("nil");
    }
}