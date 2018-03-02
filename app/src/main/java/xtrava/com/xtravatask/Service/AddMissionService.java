package xtrava.com.xtravatask.Service;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xtrava.com.xtravatask.Adapter.TodoAdapter;
import xtrava.com.xtravatask.Model.TodoModel;

/**
 * Created by Fehoo on 3/2/2018.
 */

public class AddMissionService {
    public void callTodoAddService(final String title, final Boolean isComplete ,
                                    final Context context, final FrameLayout progressFrame,
                                    final TodoAdapter todoAdapter) {
        progressFrame.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://todo-backend-modern-js.herokuapp.com/todos";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("update response", response + "");
                            JSONObject todoObject = new JSONObject(response);
                            if (todoObject.getString("title").equals(title)) {
                                TodoModel todoModel = new TodoModel();
                                todoModel.setTitle(title);
                                if (isComplete) {
                                    todoModel.setCompleted("true");
                                } else {
                                    todoModel.setCompleted("false");
                                }
                                todoAdapter.addItem(todoModel);
                                todoAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressFrame.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressFrame.setVisibility(View.GONE);
                Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        }) {
            // this is the relevant method
            @Override
            public byte[] getBody() throws AuthFailureError {
                // usually you'd have a field with some values you'd want to escape, you need to do it yourself if overriding getBody. here's how you do it
                String httpPostBody = "{\"title\": \"" + title + "\",\"order\": 2,\"completed\": " + isComplete + "}";
                return httpPostBody.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
