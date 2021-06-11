package com.asimpson.tasks;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskService {

    public static final String TASKS_URL = "http://127.0.0.1:5000/todo/api/v1.0/tasks/";

    Context context;

    public TaskService(Context context) {
        this.context = context;
    }

    public interface Volleyresponserlisterner{
        void onError(String message);

        void onResponse(List<TaskModel> taskModel);
    }

    public void getTaskbyId(String taskId, Volleyresponserlisterner volleyresponserlisterner){

       String url = TASKS_URL + taskId;

       List<TaskModel> taskModels = new ArrayList<>();

       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               try{
                   JSONArray tasks = response.getJSONArray("tasks");

                    for(int i=0; i<tasks.length(); i++){
                        TaskModel allTasks = new TaskModel();
                        JSONObject first_task_from_api = (JSONObject) tasks.get(i);
                        allTasks.setId(first_task_from_api.getInt("id"));
                        allTasks.setTitle(first_task_from_api.getString("title"));
                        allTasks.setDescription(first_task_from_api.getString("description"));
                        allTasks.setDone(first_task_from_api.getBoolean("done"));
                        taskModels.add(allTasks);
                    }
                    volleyresponserlisterner.onResponse(taskModels);
               } catch ( JSONException e){
                   e.printStackTrace();
               }

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });
       // Instantiate and Add the request to the RequestQueue.
       MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
