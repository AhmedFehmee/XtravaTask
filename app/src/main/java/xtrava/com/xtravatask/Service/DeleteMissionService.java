package xtrava.com.xtravatask.Service;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xtrava.com.xtravatask.Adapter.TodoAdapter;
import xtrava.com.xtravatask.Model.TodoModel;

/**
 * Created by Fehoo on 3/2/2018.
 */

public class DeleteMissionService {
    public void callTodoRemoveService(String todoID, final int position, final Context context,
                                      final FrameLayout progressFrame, final TodoAdapter todoAdapter) {
        progressFrame.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://todo-backend-modern-js.herokuapp.com/todos/" + todoID;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray todoArray = new JSONArray(response);
                            JSONObject todoObject = todoArray.getJSONObject(1);
                            if (todoObject.getBoolean("ok")) {
                                todoAdapter.removeItem(position);
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
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        System.out.println("finall //// " + stringRequest);
    }

}
