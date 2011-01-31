package org.fb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Some simple tests.
 * 
 * @author Song Qian
 * 
 */
public class Test {

    public static final String accessToken = ""; /*
                                                  * REPLACE WITH A VALID ACCESS
                                                  * TOKEN.
                                                  */

    public static void main(String[] args) {
        GraphAPI graph = new GraphAPI(accessToken);
        try {
            // Get my info
            JSONObject json = graph.getObject("me", null);
            System.out.println(json.toString());

            // Get my events
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(GraphAPI.PARAM_FIELDS,
                    "name,location,description,venue,start_time,end_time");
            json = graph.getConnections("me", GraphAPI.CT_EVENTS, params);
            System.out.println(json.toString());

            // Get some objects
            List<String> ids = new ArrayList<String>();
            ids.add("platform");
            ids.add("cocacola");
            json = graph.getObjects(ids, null);
            System.out.println(json.toString());
        } catch (GraphAPIError e) {
            System.out.println(e.getMessage());
        }
    }

}
