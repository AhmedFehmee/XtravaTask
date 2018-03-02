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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xtrava.com.xtravatask.Activity.MainActivity;
import xtrava.com.xtravatask.Adapter.TodoAdapter;
import xtrava.com.xtravatask.DB.DatabaseHandler;
import xtrava.com.xtravatask.Model.TodoModel;
import xtrava.com.xtravatask.Utility;

/**
 * Created by Fehoo on 3/2/2018.
 */

public class CallMissionService {
    DatabaseHandler db;

    public void callTodoService(final Context context, final FrameLayout progressFrame, final ArrayList<TodoModel> todoList
            , final RecyclerView todoRecycle, final TodoAdapter todoAdapter) {
        progressFrame.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://todo-backend-modern-js.herokuapp.com/todos";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray todoArray = new JSONArray(response);
                            todoList.clear();
                            db = new DatabaseHandler(context);
                            List<TodoModel> missions = db.getAllMissions();
                            for (TodoModel cn : missions) {
                                db.deleteContact(cn);
                            }
                            for (int i = 0; i < todoArray.length(); i++) {
                                TodoModel todo = new TodoModel();
                                JSONObject todoObject = todoArray.getJSONObject(i);
                                todo.setTitle(todoObject.getString("title"));
                                todo.setCompleted(todoObject.getString("completed"));
                                todo.setUrl(todoObject.getString("url"));
                                todo.setId(todoObject.getString("id"));
                                //cashed data into DB
                                db.addContact(new TodoModel(todo.getTitle(),
                                        todo.getCompleted(),
                                        todo.getId(),
                                        todo.getUrl()));
                                todoList.add(todo);
                            }
                            todoRecycle.setAdapter(todoAdapter);
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
    }
}
