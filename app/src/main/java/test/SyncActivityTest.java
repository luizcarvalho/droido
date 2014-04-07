package test;

import br.com.redrails.torpedos.BuildConfig;
import br.com.redrails.torpedos.R;
import br.com.redrails.torpedos.parse.SyncActivity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class SyncActivityTest extends ActivityInstrumentationTestCase2<SyncActivity> {
    SyncActivity activity;
    Instrumentation mInstrumentation;

    public SyncActivityTest() {
        super(SyncActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        mInstrumentation = getInstrumentation();
    }



    @LargeTest
    public void notestReceiveDataFromParse() throws Exception {
        String result;

        TextView syncResult = (TextView) activity.findViewById(R.id.sync_result);
        assertNotNull(syncResult);

        activity.runOnUiThread(new Runnable() {
            public void run() {
                Button syncActionButton = (Button) activity.findViewById(R.id.sync_action_button);
                syncActionButton.performClick();
            }
        });
        String defaultResult = (String) syncResult.getText();
        result=defaultResult;

        while (result==defaultResult){
            result = (String) syncResult.getText();
        }
       assertTrue(result.matches("\\d+"));
        }




    }

