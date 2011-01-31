package org.fb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * GraphAPI provides some helper methods for interactions with the Facebook
 * Graph API. Results are returned in the form of a JSONObject. Object models
 * may be created later for easier access.
 * 
 * @author Song Qian
 * 
 */
public class GraphAPI {

    // Connection types
    public static final String CT_FRIENDS = "friends";
    public static final String CT_NEWS_FEED = "home";
    public static final String CT_PROFILE_FEED = "feed";
    public static final String CT_LIKES = "likes";
    public static final String CT_MOVIES = "movies";
    public static final String CT_MUSIC = "music";
    public static final String CT_BOOKS = "books";
    public static final String CT_NOTES = "notes";
    public static final String CT_PHOTO_TAGS = "photos";
    public static final String CT_PHOTO_ALBUMS = "albums";
    public static final String CT_VIDEO_TAGS = "videos";
    public static final String CT_VIDEO_UPLOADS = "videos/uploaded";
    public static final String CT_EVENTS = "events";
    public static final String CT_GROUPS = "groups";
    public static final String CT_CHECKINS = "checkins";
    public static final String CT_COMMENTS = "comments";

    // Common URL parameters
    public static final String PARAM_FIELDS = "fields";
    public static final String PARAM_IDS = "ids";
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_METHOD = "method";

    public static final String ID_ME = "me";

    private String accessToken;

    /**
     * Constructor for GraphAPI object.
     * 
     * @param accessToken
     *            a valid, URL-decoded access token
     */
    public GraphAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets a single object by id.
     * 
     * @param id
     *            object id
     * @param params
     *            optional parameters e.g. fields
     * @return
     * @throws GraphAPIError
     */
    public JSONObject getObject(String id, Map<String, Object> params)
            throws GraphAPIError {
        return request(id, params, null);
    }

    /**
     * Gets objects by ids.
     * 
     * @param ids
     *            list of object ids
     * @param params
     *            optional parameters e.g. fields
     * @return
     * @throws GraphAPIError
     */
    public JSONObject getObjects(List<String> ids, Map<String, Object> params)
            throws GraphAPIError {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String id : ids) {
            if (!isFirst) {
                sb.append(',');
            } else {
                isFirst = false;
            }
            sb.append(id);
        }
        params.put(PARAM_IDS, sb.toString());
        return request("", params, null);
    }

    /**
     * Gets connection type objects for the given object.
     * 
     * @param id
     *            object id
     * @param connectionName
     *            connection name
     * @param params
     *            optional parameters e.g. fields
     * @return
     * @throws GraphAPIError
     */
    public JSONObject getConnections(String id, String connectionName,
            Map<String, Object> params) throws GraphAPIError {
        return request(id + '/' + connectionName, params, null);
    }

    /**
     * Puts a new object under the given object and connection
     * 
     * @param id
     *            object id
     * @param connectionName
     *            connection name
     * @param postArgs
     *            post arguments
     * @return
     * @throws GraphAPIError
     */
    public JSONObject putObject(String id, String connectionName,
            Map<String, Object> postArgs) throws GraphAPIError {
        return request(id + '/' + connectionName, null, postArgs);
    }

    /**
     * Puts a wall post for given object.
     * 
     * @param id
     *            object id, default to "me" if empty or null
     * @param message
     *            wall post message
     * @param attachment
     *            wall post attachment
     * @return
     * @throws GraphAPIError
     */
    public JSONObject putWallPost(String id, String message,
            Map<String, Object> attachment) throws GraphAPIError {
        if (id == null || id.length() == 0) {
            id = ID_ME;
        }
        if (attachment == null) {
            attachment = new HashMap<String, Object>();
        }
        attachment.put(PARAM_MESSAGE, message);
        return putObject(id, CT_PROFILE_FEED, attachment);
    }

    /**
     * Puts a comment for the given object.
     * 
     * @param id
     *            object id
     * @param message
     *            comment message
     * @return
     * @throws GraphAPIError
     */
    public JSONObject putComment(String id, String message)
            throws GraphAPIError {
        Map<String, Object> postArgs = new HashMap<String, Object>();
        postArgs.put(PARAM_MESSAGE, message);
        return putObject(id, CT_COMMENTS, postArgs);
    }

    /**
     * Puts a like for the given object.
     * 
     * @param id
     *            object id
     * @return
     * @throws GraphAPIError
     */
    public JSONObject putLike(String id) throws GraphAPIError {
        return putObject(id, CT_LIKES, null);
    }

    /**
     * Deletes the given object.
     * 
     * @param id
     *            object id
     * @return
     * @throws GraphAPIError
     */
    public JSONObject deleteObject(String id) throws GraphAPIError {
        Map<String, Object> postArgs = new HashMap<String, Object>();
        postArgs.put(PARAM_METHOD, "delete");
        return request(id, null, postArgs);
    }

    /**
     * Makes a graph API request. If postArgs is null, a GET request is made.
     * Otherwise a POST request is made.
     * 
     * @param path
     *            graph API path
     * @param params
     *            URL parameters (query string)
     * @param postArgs
     *            POST arguments
     * @return
     * @throws GraphAPIError
     */
    public JSONObject request(String path, Map<String, Object> params,
            Map<String, Object> postArgs) throws GraphAPIError {
        if (accessToken != null) {
            if (postArgs != null) {
                postArgs.put(PARAM_ACCESS_TOKEN, accessToken);
            } else {
                if (params == null) {
                    params = new HashMap<String, Object>();
                }
                params.put(PARAM_ACCESS_TOKEN, accessToken);
            }
        }
        String urlStr = "https://graph.facebook.com/" + path + '?'
                + Utility.encodeURLParameters(params);
        String method = (postArgs == null) ? "GET" : "POST";

        JSONObject resp = new JSONObject();
        try {
            String response = Utility.openUrl(urlStr, method, postArgs);
            resp = Utility.parseJson(response);
        } catch (MalformedURLException ex) {
            throw new GraphAPIError("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            throw new GraphAPIError("IOException: " + ex.getMessage());
        } catch (JSONException ex) {
            throw new GraphAPIError("JSONException: " + ex.getMessage());
        }

        return resp;
    }

}
