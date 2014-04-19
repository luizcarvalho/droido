package test;

import br.com.redrails.torpedos.BuildConfig;
import br.com.redrails.torpedos.DataBaseHelper;
import br.com.redrails.torpedos.R;
import br.com.redrails.torpedos.daos.MensagemDAO;
import br.com.redrails.torpedos.models.Mensagem;
import br.com.redrails.torpedos.parse.ParseHelper;
import br.com.redrails.torpedos.parse.SyncActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.List;


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

