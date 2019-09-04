package mobi.lab.scrolls.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogPost;
import mobi.lab.scrolls.LogPostBuilder;
import mobi.lab.scrolls.LogViewBuilder;

public class HelloLogPostActivity extends Activity {

    /* We can grab our log instance this way now: */
    private final Log log = Log.getInstance(this);

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        log.d("onCreate");
        log.d("1 2 3");
        log.i("Info level log line here");
        log.d("Some common debug level logging ..");
        log.v("When we need to get really verbose we can use this");
        log.w("Warnings are should be rare.");
        log.e("But errors tend to be plentiful ..");
        log.wtf(" .. and something really crazy stuff happens!");
        log.d("Some more testing: \n");
        try {
            Object o = null;
            o.equals("test");
        } catch (Exception e) {
            log.e(e, "Error with a full stacktrace");
        }


        try {
            int i = 0;
            int o = 1 / i;
        } catch (Exception e) {
            log.wtf(e, "WTF?!");
        }
    }

    /**
     * Our onclick method that posts dem logs
     *
     * @param v view that was clicked
     */
    public void postLogsWithActivity(View v) {
        LogPostBuilder builder = new LogPostBuilder();
        builder.addTags("my", "first", "post").launchActivity(this);
    }

    public void postLogsWithActivityFromLogcat(View v) {
        LogPostBuilder builder = new LogPostBuilder(LogPost.LOG_TYPE_LOGCAT);
        builder.addTags("my", "logcat", "post").launchActivity(this);
    }

    public void viewLogs(View v) {
        new LogViewBuilder().defaultLogs().launchActivity(this);
    }

    public void viewCurrentLog(View v) {
        new LogViewBuilder().currentLog().launchActivity(this);
    }

    public void logALongLine(View view) {
        log.e("Bacon\nipsum dolor amet meatball turkey strip steak pastrami hamburger biltong picanha frankfurter cupim.\nTurkey cow beef short ribs salami prosciutto tongue shank ball tip jerky ham hock pork belly kevin flank. Turkey meatball strip steak, swine jowl pork loin doner venison jerky turducken beef ribs pork belly. Pig hamburger tongue pastrami strip steak drumstick meatball shankle ball tip flank meatloaf picanha pork loin brisket. Pastrami meatball tri-tip flank swine drumstick brisket, salami frankfurter hamburger pork loin ribeye tongue turducken. Leberkas doner beef, ribeye andouille cow jowl pork loin boudin capicola flank. Turkey tri-tip tenderloin sirloin ham, filet mignon andouille meatball salami shoulder corned beef tail fatback. T-bone capicola shankle tail pancetta short loin jowl sausage ball tip ground round bresaola corned beef rump. Prosciutto ham hock picanha, fatback turkey biltong capicola rump chuck ball tip andouille. Spare ribs capicola pork belly, beef ribs short loin tongue shoulder meatloaf tri-tip alcatra brisket sausage biltong sirloin bresaola. Porchetta filet mignon swine, short ribs ham hock brisket flank pork loin ribeye tenderloin. Capicola prosciutto kielbasa spare ribs pork meatball cupim turkey filet mignon ham hock pork chop sirloin sausage. Sausage porchetta pork loin pastrami filet mignon landjaeger meatball turducken. Beef ribs doner ribeye venison meatloaf. Spare ribs prosciutto beef corned beef, chicken beef ribs ball tip flank cupim bacon shank frankfurter chuck meatloaf. Sirloin pork chop cow prosciutto, ball tip turkey ham salami tongue ground round pancetta ham hock bacon hamburger porchetta. Spare ribs filet mignon tongue rump ham frankfurter doner beef pig venison ribeye jowl pork belly. Shoulder pancetta swine porchetta tri-tip tenderloin andouille ball tip drumstick t-bone kielbasa beef biltong pork loin sausage. Pig jerky beef cupim. Pig leberkas hamburger ham flank venison. T-bone picanha shankle beef meatloaf swine. Kielbasa pork chop prosciutto landjaeger, beef beef ribs swine short ribs bresaola pork chicken corned beef. Brisket tongue cow pig shankle pork chop biltong shoulder meatball leberkas. Meatloaf sausage venison strip steak, meatball corned beef alcatra t-bone bacon pork chicken jerky boudin pastrami. Flank tri-tip tongue, beef strip steak turkey alcatra pancetta sausage picanha tenderloin shank shankle drumstick kielbasa. Ball tip pork belly short ribs tail, tri-tip ground round pork chop. Andouille shank pastrami, frankfurter alcatra pork loin pork tri-tip sirloin kevin prosciutto. Turkey pork loin shank picanha. Andouille kevin t-bone porchetta landjaeger. Kielbasa doner prosciutto pork loin tail cow turducken turkey chicken alcatra flank ham porchetta strip steak pork chop. Chuck pork belly porchetta cupim cow shankle, strip steak jowl sausage beef tail doner. Ribeye landjaeger prosciutto, corned beef venison sirloin ball tip pork chop pork shoulder leberkas. Hamburger strip steak shoulder doner chuck tenderloin jowl bresaola boudin fatback pork chop. Shoulder landjaeger leberkas, fatback tri-tip chicken ball tip strip steak short loin flank brisket corned beef. Bacon landjaeger pork tongue boudin ham hock, swine frankfurter turkey salami. Shankle tongue pig beef. Pork loin jowl turkey, kielbasa bacon alcatra landjaeger pork beef ribs flank. Drumstick sirloin shank pork belly ribeye porchetta bacon pork pancetta beef ribs. Turkey meatloaf pig bacon jowl spare ribs corned beef. Strip steak cupim tail doner pork chop. Landjaeger biltong prosciutto picanha salami, boudin flank. Biltong swine spare ribs boudin turkey. Drumstick meatball meatloaf prosciutto. T-bone capicola meatball, flank boudin pork chop filet mignon. Jerky rump shoulder doner, ribeye pastrami leberkas beef ribs frankfurter meatloaf pork. Boudin short loin picanha, shoulder andouille rump jowl bresaola shankle tri-tip turducken pork belly. Cow ball tip hamburger, kielbasa chicken cupim boudin.123456789101112\nX");
    }

    // some random activity state transitions follow. It is usually a good idea to log these out.

    @Override
    protected void onStart() {
        super.onStart();
        log.d("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log.d("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log.d("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log.d("onDestroy");
    }
}
