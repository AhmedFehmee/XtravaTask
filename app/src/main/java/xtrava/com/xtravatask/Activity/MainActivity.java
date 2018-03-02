package xtrava.com.xtravatask.Activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.wang.avi.AVLoadingIndicatorView;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xtrava.com.xtravatask.Adapter.TodoAdapter;
import xtrava.com.xtravatask.DB.DatabaseHandler;
import xtrava.com.xtravatask.Model.TodoModel;
import xtrava.com.xtravatask.R;
import xtrava.com.xtravatask.Service.AddMissionService;
import xtrava.com.xtravatask.Service.CallMissionService;
import xtrava.com.xtravatask.Service.DeleteMissionService;
import xtrava.com.xtravatask.Service.UpdateMissionService;
import xtrava.com.xtravatask.Utility;

public class MainActivity extends AppCompatActivity {

    TodoAdapter todoAdapter;
    public ArrayList<TodoModel> todoList = new ArrayList<>();

    @BindView(R.id.todo_recycle)
    RecyclerView todoRecycle;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.avi_progress)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.progress_frame)
    FrameLayout progressFrame;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    EditText et_title;
    LabeledSwitch labeledSwitch;

    int edit_position;
    View view;
    boolean add = false;
    Paint p = new Paint();
    AlertDialog.Builder alertDialog;
    Boolean missionState = false;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        todoAdapter = new TodoAdapter(this, todoList);
        db = new DatabaseHandler(getApplicationContext());
        if (Utility.isNetworkAvailable(this)) {
            CallMissionService callMissionService = new CallMissionService();
            callMissionService.callTodoService(getApplicationContext(), progressFrame, todoList, todoRecycle, todoAdapter);
        } else {
            //todoList = db.getAllMissions();
            List<TodoModel> contacts = db.getAllMissions();
            for (TodoModel cn : contacts) {
                todoList.add(cn);
            }
            todoRecycle.setAdapter(todoAdapter);
            Toast.makeText(getApplicationContext(), "Cashed List", Toast.LENGTH_LONG).show();
        }
        todoRecycle.setHasFixedSize(true);
        todoRecycle.setLayoutManager(new LinearLayoutManager(this));

        initSwipe();
        initDialog();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    CallMissionService callMissionService = new CallMissionService();
                    callMissionService.callTodoService(getApplicationContext(), progressFrame, todoList, todoRecycle, todoAdapter);
                } else {
                    todoList.clear();
                    List<TodoModel> contacts = db.getAllMissions();
                    for (TodoModel cn : contacts) {
                        todoList.add(cn);
                    }
                    todoRecycle.setAdapter(todoAdapter);
                    Toast.makeText(getApplicationContext(), "Cashed List", Toast.LENGTH_LONG).show();
                }
                // Stop refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //Menu Work
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage(Html.fromHtml(MainActivity.this.getString(R.string.info_text) +
                    " <a href='https://github.com/AhmedFehoO/XtravaTask'>GitHub.</a>"));
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        DeleteMissionService deleteMissionService = new DeleteMissionService();
                        deleteMissionService.callTodoRemoveService(todoList.get(position).getId(), position, getApplicationContext(), progressFrame, todoAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    }
                    todoAdapter.notifyDataSetChanged();
                } else {
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle("Edit Mission");
                    et_title.setText(todoList.get(position).getTitle());
                    alertDialog.show();
                    todoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(todoRecycle);
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        labeledSwitch = view.findViewById(R.id.switch_state);
        et_title = (EditText) view.findViewById(R.id.et_title);
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                // Implement your switching logic here
                if (isOn) {
                    missionState = true;
                } else {
                    missionState = false;
                }
            }
        });
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (add) {
                    add = false;
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        AddMissionService addMissionService = new AddMissionService();
                        addMissionService.callTodoAddService(et_title.getText().toString(), missionState,
                                getApplicationContext(), progressFrame, todoAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else {
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        UpdateMissionService updateMissionService = new UpdateMissionService();
                        updateMissionService.callTodoUpdateService(todoList.get(edit_position).getId(),
                                et_title.getText().toString(), missionState, getApplicationContext(),
                                edit_position, progressFrame, todoAdapter, todoList);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        removeView();
        add = true;
        alertDialog.setTitle("Add Mission");
        et_title.setText("");
        alertDialog.show();
    }
}
