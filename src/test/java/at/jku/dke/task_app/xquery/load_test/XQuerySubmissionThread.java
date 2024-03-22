package at.jku.dke.task_app.xquery.load_test;

import at.jku.dke.etutor.task_app.test.SubmissionThread;

public class XQuerySubmissionThread extends SubmissionThread {

    private static final Long[] IDS = new Long[]{9990L, 9991L, 9992L};
    private static final String[] QUERIES = new String[]{
        "let $db := doc('etutor.xml')/handelskette for $x in $db//rechnung order by $x/@rechnungNr return $x",
        "let $db := doc('etutor.xml')/handelskette for $x in $db//filiale return $x/inhName/text()",
        "let $hk := doc('etutor.xml')/handelskette let $bs := distinct-values($hk/kunden/kunde/bonStufe) for $bStufe in $bs let $kdNrBs := $hk/kunden//kunde[bonStufe eq $bStufe]/@kundeNr, $kdCnt := count(distinct-values($kdNrBs)) let $rPosten := (for $r in $hk/rechnungen/rechnung[kundeNr = $kdNrBs]/rposition return ($r/einzelPreis * $r/menge)) order by $bStufe return <bStufe val=\\\"{$bStufe}\\\"><umsGesamt>{sum($rPosten)}</umsGesamt><umsProKunde>{sum($rPosten) div $kdCnt}</umsProKunde></bStufe>"
    };

    public XQuerySubmissionThread(String name, String url, String apiKey, int initialSleep, int totalAmountOfRequests, int pauseBetweenRequestsInMs) {
        super(name, url, apiKey, initialSleep, totalAmountOfRequests, pauseBetweenRequestsInMs);
    }

    @Override
    protected String buildAdditionalDataJson(int iteration, long taskId) {
        return "\"input\": \"" + QUERIES[this.random.nextInt(0, QUERIES.length)] + "\"";
    }

    @Override
    protected long getTaskId(int iteration) {
        return IDS[iteration % IDS.length];
    }

}
